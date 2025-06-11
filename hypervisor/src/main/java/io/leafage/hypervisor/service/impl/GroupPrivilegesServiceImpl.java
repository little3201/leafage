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
import io.leafage.hypervisor.dto.AuthorizePrivilegesDTO;
import io.leafage.hypervisor.repository.GroupAuthoritiesRepository;
import io.leafage.hypervisor.repository.GroupPrivilegesRepository;
import io.leafage.hypervisor.repository.GroupRepository;
import io.leafage.hypervisor.repository.PrivilegeRepository;
import io.leafage.hypervisor.service.GroupPrivilegesService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GroupPrivilegesServiceImpl implements GroupPrivilegesService {

    private final GroupRepository groupRepository;
    private final GroupPrivilegesRepository groupPrivilegesRepository;
    private final PrivilegeRepository privilegeRepository;
    private final GroupAuthoritiesRepository groupAuthoritiesRepository;

    public GroupPrivilegesServiceImpl(GroupRepository groupRepository, GroupPrivilegesRepository groupPrivilegesRepository,
                                      PrivilegeRepository privilegeRepository, GroupAuthoritiesRepository groupAuthoritiesRepository) {
        this.groupRepository = groupRepository;
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
    public List<GroupPrivileges> relation(Long groupId, List<AuthorizePrivilegesDTO> dtoList) {
        Assert.notNull(groupId, "groupId must not be null.");

        // 优化 Optional 的使用，减少嵌套
        return dtoList.stream().map(dto -> {
            GroupPrivileges groupPrivileges = privileges(groupId, dto.getPrivilegeId(), dto.getActions());

            privilegeRepository.findById(dto.getPrivilegeId()).ifPresent(privilege ->
                    addGroupAuthority(groupId, privilege.getName(), dto.getActions()));
            // 保存并立即刷新
            return groupPrivilegesRepository.saveAndFlush(groupPrivileges);
        }).toList();
    }

    @Override
    public void removeRelation(Long groupId, Long privilegeId, Set<String> actions) {
        groupPrivilegesRepository.findByGroupIdAndPrivilegeId(groupId, privilegeId);
    }

    private GroupPrivileges privileges(Long groupId, Long privilegeId, Set<String> actions) {
        // actions添加read
        Set<String> effectiveActions = new HashSet<>();
        if (!CollectionUtils.isEmpty(actions)) {
            effectiveActions.addAll(actions);
        }

        return new GroupPrivileges(groupId, privilegeId, effectiveActions);
    }

    private void addGroupAuthority(Long groupId, String privilegeName, Set<String> actions) {
        actions.forEach(action -> groupAuthoritiesRepository.save(new GroupAuthorities(groupId, privilegeName + ":" + action)));
    }
}
