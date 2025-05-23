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

import io.leafage.basic.hypervisor.domain.GroupAuthorities;
import io.leafage.basic.hypervisor.domain.Privilege;
import io.leafage.basic.hypervisor.domain.RolePrivileges;
import io.leafage.basic.hypervisor.dto.AuthorizePrivilegesDTO;
import io.leafage.basic.hypervisor.repository.GroupAuthoritiesRepository;
import io.leafage.basic.hypervisor.repository.GroupRolesRepository;
import io.leafage.basic.hypervisor.repository.PrivilegeRepository;
import io.leafage.basic.hypervisor.repository.RolePrivilegesRepository;
import io.leafage.basic.hypervisor.service.RolePrivilegesService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
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
    private final PrivilegeRepository privilegeRepository;
    private final GroupAuthoritiesRepository groupAuthoritiesRepository;

    /**
     * <p>Constructor for RolePrivilegesServiceImpl.</p>
     *
     * @param rolePrivilegesRepository a {@link RolePrivilegesRepository} object
     */
    public RolePrivilegesServiceImpl(RolePrivilegesRepository rolePrivilegesRepository, GroupRolesRepository groupRolesRepository,
                                     PrivilegeRepository privilegeRepository, GroupAuthoritiesRepository groupAuthoritiesRepository) {
        this.rolePrivilegesRepository = rolePrivilegesRepository;
        this.groupRolesRepository = groupRolesRepository;
        this.privilegeRepository = privilegeRepository;
        this.groupAuthoritiesRepository = groupAuthoritiesRepository;
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
    public List<RolePrivileges> relation(Long roleId, List<AuthorizePrivilegesDTO> dtoList) {
        Assert.notNull(roleId, "roleId must not be null.");

        // 优化 Optional 的使用，减少嵌套
        return dtoList.stream().map(dto -> {
            RolePrivileges rolePrivilege = privileges(roleId, dto.getPrivilegeId(), dto.getActions());

            privilegeRepository.findById(dto.getPrivilegeId()).ifPresent(privilege ->
                    addGroupAuthority(roleId, privilege.getName(), dto.getActions()));
            // 保存并立即刷新
            return rolePrivilegesRepository.saveAndFlush(rolePrivilege);
        }).toList();
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

    private RolePrivileges privileges(Long roleId, Long privilegeId, Set<String> actions) {
        // actions添加read
        Set<String> effectiveActions = new HashSet<>();
        if (!CollectionUtils.isEmpty(actions)) {
            effectiveActions.addAll(actions);
        }
        effectiveActions.add("read");

        RolePrivileges rolePrivileges = new RolePrivileges();
        rolePrivileges.setRoleId(roleId);
        rolePrivileges.setPrivilegeId(privilegeId);
        rolePrivileges.setActions(effectiveActions);
        return rolePrivileges;
    }

    private void addGroupAuthority(Long roleId, String privilegeName, Set<String> actions) {
        groupRolesRepository.findAllByRoleId(roleId).forEach(groupRole ->
                actions.forEach(action ->
                        groupAuthoritiesRepository.save(new GroupAuthorities(groupRole.getGroupId(), privilegeName + ":" + action)))
        );
    }

    private void removeGroupAuthority(Long roleId, Privilege privilege, Set<String> actions) {
        groupRolesRepository.findAllByRoleId(roleId).forEach(groupRole -> {
                    // 移除授权actions
                    if (CollectionUtils.isEmpty(actions)) {
                        groupAuthoritiesRepository.deleteByGroupIdAndAuthority(groupRole.getGroupId(), privilege.getName() + ":read");
                    } else {
                        actions.forEach(action ->
                                groupAuthoritiesRepository.deleteByGroupIdAndAuthority(groupRole.getGroupId(), privilege.getName() + ":" + action));
                    }
                }
        );
    }

}
