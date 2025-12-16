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
import top.leafage.hypervisor.system.domain.GroupRoles;
import top.leafage.hypervisor.system.repository.GroupRolesRepository;
import top.leafage.hypervisor.system.service.GroupRolesService;

import java.util.List;
import java.util.Set;

import static top.leafage.common.data.Service._MUST_NOT_BE_EMPTY;
import static top.leafage.common.data.Service._MUST_NOT_BE_NULL;


/**
 * group roles service impl.
 *
 * @author wq li
 */
@Service
public class GroupRolesServiceImpl implements GroupRolesService {

    private final GroupRolesRepository groupRolesRepository;

    /**
     * Constructor for GroupMembersServiceImpl.
     *
     * @param groupRolesRepository a {@link GroupRolesRepository} object
     */
    public GroupRolesServiceImpl(GroupRolesRepository groupRolesRepository) {
        this.groupRolesRepository = groupRolesRepository;
    }

    @Override
    public List<GroupRoles> roles(Long groupId) {
        Assert.notNull(groupId, String.format(_MUST_NOT_BE_NULL, "groupId"));

        return groupRolesRepository.findAllByGroupId(groupId);
    }

    @Override
    public List<GroupRoles> groups(Long roleId) {
        Assert.notNull(roleId, String.format(_MUST_NOT_BE_NULL, "roleId"));

        return groupRolesRepository.findAllByRoleId(roleId);
    }

    @Override
    public List<GroupRoles> relation(Long groupId, Set<Long> roleIds) {
        Assert.notNull(groupId, String.format(_MUST_NOT_BE_NULL, "groupId"));
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
        Assert.notNull(groupId, String.format(_MUST_NOT_BE_NULL, "groupId"));
        Assert.notEmpty(roleIds, String.format(_MUST_NOT_BE_EMPTY, "roleIds"));

        List<GroupRoles> groupRoles = groupRolesRepository.findAllByGroupId(groupId);
        List<Long> filteredIds = groupRoles.stream()
                .filter(groupRole -> roleIds.contains(groupRole.getRoleId()))
                .map(GroupRoles::getId).toList();
        groupRolesRepository.deleteAllByIdInBatch(filteredIds);
    }
}
