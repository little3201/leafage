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

import io.leafage.hypervisor.domain.GroupRoles;
import io.leafage.hypervisor.repository.GroupRolesRepository;
import io.leafage.hypervisor.service.GroupRolesService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;


/**
 * group roles service impl.
 *
 * @author wq li
 */
@Service
public class GroupRolesServiceImpl implements GroupRolesService {

    private final GroupRolesRepository groupRolesRepository;

    /**
     * <p>Constructor for GroupMembersServiceImpl.</p>
     *
     * @param groupRolesRepository a {@link GroupRolesRepository} object
     */
    public GroupRolesServiceImpl(GroupRolesRepository groupRolesRepository) {
        this.groupRolesRepository = groupRolesRepository;
    }

    @Override
    public List<GroupRoles> roles(Long groupId) {
        Assert.notNull(groupId, "groupId must not be null.");

        return groupRolesRepository.findAllByGroupId(groupId);
    }

    @Override
    public List<GroupRoles> groups(Long roleId) {
        Assert.notNull(roleId, "roleId must not be null.");

        return groupRolesRepository.findAllByRoleId(roleId);
    }

    @Override
    public List<GroupRoles> relation(Long groupId, Set<Long> roleIds) {
        Assert.notNull(groupId, "groupId must not be null.");
        List<GroupRoles> groupRoles = roleIds.stream().map(roleId -> {
            GroupRoles groupRole = new GroupRoles();
            groupRole.setGroupId(groupId);
            groupRole.setRoleId(roleId);
            return groupRole;
        }).toList();
        return groupRolesRepository.saveAllAndFlush(groupRoles);
    }

    @Override
    public void removeRelation(Long groupId, Set<Long> roleIds) {
        Assert.notNull(groupId, "groupId must not be null.");
        Assert.notEmpty(roleIds, "role ids must not be empty.");

        List<GroupRoles> groupRoles = groupRolesRepository.findAllByGroupId(groupId);
        List<Long> filteredIds = groupRoles.stream()
                .filter(groupRole -> roleIds.contains(groupRole.getRoleId()))
                .map(GroupRoles::getId).toList();
        groupRolesRepository.deleteAllByIdInBatch(filteredIds);
    }
}
