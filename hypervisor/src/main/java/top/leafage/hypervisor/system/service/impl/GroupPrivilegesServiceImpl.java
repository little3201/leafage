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

package top.leafage.hypervisor.system.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import top.leafage.hypervisor.system.domain.GroupAuthorities;
import top.leafage.hypervisor.system.domain.GroupPrivileges;
import top.leafage.hypervisor.system.repository.GroupAuthoritiesRepository;
import top.leafage.hypervisor.system.repository.GroupPrivilegesRepository;
import top.leafage.hypervisor.system.repository.PrivilegeRepository;
import top.leafage.hypervisor.system.service.GroupPrivilegesService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static top.leafage.common.data.Service._MUST_NOT_BE_NULL;

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

        privilegeRepository.findById(privilegeId).ifPresent(privilege ->
                addGroupAuthority(groupId, privilege.getName(),
                        StringUtils.hasText(action) ? Set.of("", action) : Set.of("")));
        // 保存并立即刷新
        return groupPrivilegesRepository.saveAndFlush(groupPrivilege);
    }

    @Override
    public void removeRelation(Long groupId, Long privilegeId, String action) {
        groupPrivilegesRepository.findByGroupIdAndPrivilegeId(groupId, privilegeId)
                .ifPresent(groupPrivilege -> {
                    // actions为空，删除菜单
                    if (!StringUtils.hasText(action)) {
                        groupPrivilegesRepository.deleteById(groupPrivilege.getId());
                    }
                    privilegeRepository.findById(privilegeId).ifPresent(privilege ->
                            removeGroupAuthority(groupId, privilege.getName(), action));
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

    private void removeGroupAuthority(Long groupId, String name, String action) {
        // 移除授权actions
        if (StringUtils.hasText(action)) {
            groupAuthoritiesRepository.deleteByGroupIdAndAuthority(groupId, name + ":" + action);
        } else {
            groupAuthoritiesRepository.deleteByGroupIdAndAuthorityStartingWith(groupId, name);
        }
    }

}
