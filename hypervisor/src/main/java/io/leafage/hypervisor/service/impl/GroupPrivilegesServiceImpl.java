/*
 * Copyright (c) 2025.  little3201.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.leafage.hypervisor.service.impl;

import io.leafage.hypervisor.domain.GroupAuthorities;
import io.leafage.hypervisor.domain.GroupPrivileges;
import io.leafage.hypervisor.domain.Privilege;
import io.leafage.hypervisor.repository.GroupAuthoritiesRepository;
import io.leafage.hypervisor.repository.GroupPrivilegesRepository;
import io.leafage.hypervisor.repository.PrivilegeRepository;
import io.leafage.hypervisor.service.GroupPrivilegesService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static top.leafage.common.DomainConverter._MUST_NOT_BE_NULL;

@Service
public class GroupPrivilegesServiceImpl implements GroupPrivilegesService {

    private final GroupPrivilegesRepository groupPrivilegesRepository;
    private final PrivilegeRepository privilegeRepository;
    private final GroupAuthoritiesRepository groupAuthoritiesRepository;

    public GroupPrivilegesServiceImpl(GroupPrivilegesRepository groupPrivilegesRepository, PrivilegeRepository privilegeRepository,
                                      GroupAuthoritiesRepository groupAuthoritiesRepository) {
        this.groupPrivilegesRepository = groupPrivilegesRepository;
        this.privilegeRepository = privilegeRepository;
        this.groupAuthoritiesRepository = groupAuthoritiesRepository;
    }

    @Override
    public List<GroupPrivileges> privileges(Long groupId) {
        return groupPrivilegesRepository.findAllByGroupId(groupId);
    }

    @Override
    public List<GroupPrivileges> groups(Long privilegeId) {
        return groupPrivilegesRepository.findAllByPrivilegeId(privilegeId);
    }

    @Override
    public GroupPrivileges relation(Long groupId, Long privilegeId, String action) {
        Assert.notNull(groupId, String.format(_MUST_NOT_BE_NULL, "groupId"));

        GroupPrivileges groupPrivilege = new GroupPrivileges(groupId, privilegeId,
                StringUtils.hasText(action) ? Set.of(action) : Collections.emptySet());
        // 如果已存在，更新
        groupPrivilegesRepository.findByGroupIdAndPrivilegeId(groupId, privilegeId)
                .ifPresent(r -> groupPrivilege.setId(r.getId()));

        privilegeRepository.findById(privilegeId).ifPresent(privilege ->
                addGroupAuthority(groupId, privilege.getName(),
                        StringUtils.hasText(action) ? Set.of("", action) : Set.of("")));
        // 保存并立即刷新
        return groupPrivilegesRepository.saveAndFlush(groupPrivilege);
    }

    @Override
    public void removeRelation(Long groupId, Long privilegeId, String action) {
        groupPrivilegesRepository.findByGroupIdAndPrivilegeId(groupId, privilegeId)
                .ifPresent(rolePrivilege -> {
                    // actions为空，删除菜单
                    if (!StringUtils.hasText(action)) {
                        groupPrivilegesRepository.deleteById(rolePrivilege.getId());
                    }
                    privilegeRepository.findById(privilegeId).ifPresent(privilege ->
                            removeGroupAuthority(groupId, privilege, action));
                });
    }

    private void addGroupAuthority(Long groupId, String privilegeName, Set<String> actions) {
        List<GroupAuthorities> groupAuthorities = actions.stream().map(action -> {
                    String authority = action.isBlank() ? privilegeName : privilegeName + ":" + action;
                    // 检查是否已存在
                    Optional<GroupAuthorities> optional = groupAuthoritiesRepository.findByGroupIdAndAuthority(groupId, authority);
                    return optional.orElseGet(() -> new GroupAuthorities(groupId, authority));
                })
                .toList();
        groupAuthoritiesRepository.saveAll(groupAuthorities);
    }

    private void removeGroupAuthority(Long groupId, Privilege privilege, String action) {
        // 移除授权actions
        if (StringUtils.hasText(action)) {
            groupAuthoritiesRepository.deleteByGroupIdAndAuthority(groupId, privilege.getName() + ":" + action);
        } else {
            groupAuthoritiesRepository.deleteByGroupIdAndAuthorityStartingWith(groupId, privilege.getName());
        }
    }

}
