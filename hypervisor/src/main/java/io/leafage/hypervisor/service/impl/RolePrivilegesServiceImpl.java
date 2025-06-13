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

package io.leafage.hypervisor.service.impl;

import io.leafage.hypervisor.domain.GroupAuthorities;
import io.leafage.hypervisor.domain.Privilege;
import io.leafage.hypervisor.domain.RolePrivileges;
import io.leafage.hypervisor.repository.GroupAuthoritiesRepository;
import io.leafage.hypervisor.repository.GroupRolesRepository;
import io.leafage.hypervisor.repository.PrivilegeRepository;
import io.leafage.hypervisor.repository.RolePrivilegesRepository;
import io.leafage.hypervisor.service.RolePrivilegesService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    public RolePrivileges relation(Long roleId, Long privilegeId, String action) {
        Assert.notNull(roleId, "roleId must not be null.");

        RolePrivileges rolePrivilege = new RolePrivileges(roleId, privilegeId,
                StringUtils.hasText(action) ? Set.of(action) : Collections.emptySet());
        // 如果已存在，更新
        rolePrivilegesRepository.findByRoleIdAndPrivilegeId(roleId, privilegeId)
                .ifPresent(r -> rolePrivilege.setId(r.getId()));

        privilegeRepository.findById(privilegeId).ifPresent(privilege ->
                addGroupAuthority(roleId, privilege.getName(),
                        StringUtils.hasText(action) ? Set.of("", action) : Set.of("")));
        // 保存并立即刷新
        return rolePrivilegesRepository.saveAndFlush(rolePrivilege);
    }

    @Override
    public void removeRelation(Long roleId, Long privilegeId, String action) {
        rolePrivilegesRepository.findByRoleIdAndPrivilegeId(roleId, privilegeId)
                .ifPresent(rolePrivilege -> {
                    // actions为空，删除菜单
                    if (!StringUtils.hasText(action)) {
                        rolePrivilegesRepository.deleteById(rolePrivilege.getId());
                    }
                    privilegeRepository.findById(privilegeId).ifPresent(privilege ->
                            removeGroupAuthority(roleId, privilege, action));
                });
    }

    private void addGroupAuthority(Long roleId, String privilegeName, Set<String> actions) {
        List<GroupAuthorities> groupAuthorities = groupRolesRepository.findAllByRoleId(roleId)
                .stream().flatMap(groupRole -> actions.stream().map(action -> {
                    String authority = action.isBlank() ? privilegeName : privilegeName + ":" + action;
                    // 检查是否已存在
                    Optional<GroupAuthorities> optional = groupAuthoritiesRepository.findByGroupIdAndAuthority(groupRole.getGroupId(), authority);
                    return optional.orElseGet(() -> new GroupAuthorities(groupRole.getGroupId(), authority));
                }))
                .toList();
        groupAuthoritiesRepository.saveAll(groupAuthorities);
    }

    private void removeGroupAuthority(Long roleId, Privilege privilege, String action) {
        groupRolesRepository.findAllByRoleId(roleId).forEach(groupRole -> {
                    // 移除授权actions
                    if (StringUtils.hasText(action)) {
                        groupAuthoritiesRepository.deleteByGroupIdAndAuthority(groupRole.getGroupId(), privilege.getName() + ":" + action);
                    } else {
                        groupAuthoritiesRepository.deleteByGroupIdAndAuthorityStartingWith(groupRole.getGroupId(), privilege.getName());
                    }
                }
        );
    }

}
