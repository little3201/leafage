/*
 * Copyright (c) 2024-2025.  little3201.
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

package io.leafage.basic.hypervisor.service.impl;

import io.leafage.basic.hypervisor.domain.RolePrivileges;
import io.leafage.basic.hypervisor.repository.GroupMembersRepository;
import io.leafage.basic.hypervisor.repository.PrivilegeRepository;
import io.leafage.basic.hypervisor.repository.RoleMembersRepository;
import io.leafage.basic.hypervisor.repository.RolePrivilegesRepository;
import io.leafage.basic.hypervisor.service.RolePrivilegesService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

/**
 * role privileges service impl.
 *
 * @author wq li
 */
@Service
public class RolePrivilegesServiceImpl implements RolePrivilegesService {

    private final GroupMembersRepository groupMembersRepository;
    private final RolePrivilegesRepository rolePrivilegesRepository;
    private final RoleMembersRepository roleMembersRepository;
    private final PrivilegeRepository privilegeRepository;

    /**
     * <p>Constructor for RolePrivilegesServiceImpl.</p>
     *
     * @param rolePrivilegesRepository a {@link RolePrivilegesRepository} object
     */
    public RolePrivilegesServiceImpl(GroupMembersRepository groupMembersRepository,
                                     RolePrivilegesRepository rolePrivilegesRepository,
                                     RoleMembersRepository roleMembersRepository, PrivilegeRepository privilegeRepository) {
        this.groupMembersRepository = groupMembersRepository;
        this.rolePrivilegesRepository = rolePrivilegesRepository;
        this.roleMembersRepository = roleMembersRepository;
        this.privilegeRepository = privilegeRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RolePrivileges> privileges(Long roleId) {
        Assert.notNull(roleId, "roleId must not be null.");

        return rolePrivilegesRepository.findAllByRoleId(roleId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RolePrivileges> roles(Long privilegeId) {
        Assert.notNull(privilegeId, "privilegeId must not be null.");

        return rolePrivilegesRepository.findAllByPrivilegeId(privilegeId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RolePrivileges relation(Long roleId, Long privilegeId, Set<String> actions) {
        Assert.notNull(roleId, "roleId must not be null.");
        Assert.notNull(privilegeId, "privilegeId must not be null.");

        // 优化 Optional 的使用，减少嵌套
        return privilegeRepository.findById(privilegeId)
                .map(privilege -> {
                    RolePrivileges rolePrivilege = new RolePrivileges();
                    rolePrivilege.setRoleId(roleId);
                    rolePrivilege.setPrivilegeId(privilegeId);
                    rolePrivilege.setActions(actions);

                    // 保存并立即刷新
                    return rolePrivilegesRepository.saveAndFlush(rolePrivilege);
                })
                .orElse(null);
    }

    @Override
    public void removeRelation(Long roleId, Set<Long> privilegeIds) {
        List<RolePrivileges> rolePrivileges = rolePrivilegesRepository.findAllByRoleId(roleId);
        List<Long> filteredIds = rolePrivileges.stream()
                .map(RolePrivileges::getId)
                .filter(privilegeIds::contains).toList();
        rolePrivilegesRepository.deleteAllByIdInBatch(filteredIds);
    }
}
