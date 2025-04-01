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

import io.leafage.basic.hypervisor.domain.Privilege;
import io.leafage.basic.hypervisor.domain.RolePrivileges;
import io.leafage.basic.hypervisor.repository.*;
import io.leafage.basic.hypervisor.service.RolePrivilegesService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;

/**
 * role privileges service impl.
 *
 * @author wq li
 */
@Service
public class RolePrivilegesServiceImpl implements RolePrivilegesService {

    private final RolePrivilegesRepository rolePrivilegesRepository;
    private final GroupRolesRepository groupRolesRepository;
    private final GroupRepository groupRepository;
    private final PrivilegeRepository privilegeRepository;
    private final DataSource dataSource;

    /**
     * <p>Constructor for RolePrivilegesServiceImpl.</p>
     *
     * @param rolePrivilegesRepository a {@link RolePrivilegesRepository} object
     */
    public RolePrivilegesServiceImpl(RolePrivilegesRepository rolePrivilegesRepository,
                                     GroupRolesRepository groupRolesRepository, GroupRepository groupRepository,
                                     GroupMembersRepository groupMembersRepository,
                                     RoleMembersRepository roleMembersRepository,
                                     PrivilegeRepository privilegeRepository, DataSource dataSource) {
        this.rolePrivilegesRepository = rolePrivilegesRepository;
        this.groupRolesRepository = groupRolesRepository;
        this.groupRepository = groupRepository;
        this.privilegeRepository = privilegeRepository;
        this.dataSource = dataSource;
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

                    addGroupAuthority(roleId, privilege, actions);

                    // 保存并立即刷新
                    return rolePrivilegesRepository.saveAndFlush(rolePrivilege);
                })
                .orElse(null);
    }

    @Override
    public void removeRelation(Long roleId, Long privilegeId, Set<String> actions) {
        rolePrivilegesRepository.findByRoleIdAndPrivilegeId(roleId, privilegeId).ifPresent(rolePrivilege -> {
            // actions为空，删除菜单
            if (CollectionUtils.isEmpty(actions) || rolePrivilege.getActions().containsAll(actions)) {
                rolePrivilegesRepository.deleteById(rolePrivilege.getId());
            }
            privilegeRepository.findById(privilegeId).ifPresent(privilege ->
                    removeGroupAuthority(roleId, privilege, actions));
        });
    }

    private void addGroupAuthority(Long roleId, Privilege privilege, Set<String> actions) {
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
        // 添加 read
        actions.add("read");
        groupRolesRepository.findAllByRoleId(roleId).forEach(groupRole ->
                groupRepository.findById(groupRole.getGroupId()).ifPresent(group ->
                        actions.forEach(action -> userDetailsManager.addGroupAuthority(group.getName(),
                                new SimpleGrantedAuthority(privilege.getName() + ":" + action)))
                )
        );
    }

    private void removeGroupAuthority(Long roleId, Privilege privilege, Set<String> actions) {
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
        groupRolesRepository.findAllByRoleId(roleId).forEach(groupRole ->
                groupRepository.findById(groupRole.getGroupId()).ifPresent(group -> {
                    // 移除授权actions
                    if (CollectionUtils.isEmpty(actions)) {
                        userDetailsManager.removeGroupAuthority(group.getName(), new SimpleGrantedAuthority(privilege.getName() + ":read"));
                    } else {
                        actions.forEach(action ->
                                userDetailsManager.removeGroupAuthority(group.getName(), new SimpleGrantedAuthority(privilege.getName() + ":" + action)));
                    }
                })
        );
    }

}
