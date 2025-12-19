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

package top.leafage.hypervisor.system.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import top.leafage.hypervisor.system.domain.GroupAuthorities;
import top.leafage.hypervisor.system.domain.RolePrivileges;
import top.leafage.hypervisor.system.repository.GroupAuthoritiesRepository;
import top.leafage.hypervisor.system.repository.GroupRolesRepository;
import top.leafage.hypervisor.system.repository.PrivilegeRepository;
import top.leafage.hypervisor.system.repository.RolePrivilegesRepository;
import top.leafage.hypervisor.system.service.RolePrivilegesService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static top.leafage.common.data.Service._MUST_NOT_BE_NULL;

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
     * Constructor for RolePrivilegesServiceImpl.
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
        Assert.notNull(roleId, String.format(_MUST_NOT_BE_NULL, "roleId"));

        return rolePrivilegesRepository.findAllByRoleId(roleId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RolePrivileges> roles(Long privilegeId) {
        Assert.notNull(privilegeId, String.format(_MUST_NOT_BE_NULL, "privilegeId"));

        return rolePrivilegesRepository.findAllByPrivilegeId(privilegeId);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public RolePrivileges relation(Long roleId, Long privilegeId, String action) {
        Assert.notNull(roleId, String.format(_MUST_NOT_BE_NULL, "roleId"));
        Assert.notNull(privilegeId, String.format(_MUST_NOT_BE_NULL, "privilegeId"));

        RolePrivileges rolePrivilege = new RolePrivileges(roleId, privilegeId,
                StringUtils.hasText(action) ? Set.of(action) : Collections.emptySet());

        privilegeRepository.findById(privilegeId).ifPresent(privilege ->
                addGroupAuthority(roleId, privilege.getName(),
                        StringUtils.hasText(action) ? Set.of("", action) : Set.of("")));
        // 保存并立即刷新
        return rolePrivilegesRepository.saveAndFlush(rolePrivilege);
    }

    @Transactional
    @Override
    public void removeRelation(Long roleId, Long privilegeId, String action) {
        Assert.notNull(roleId, String.format(_MUST_NOT_BE_NULL, "roleId"));
        Assert.notNull(privilegeId, String.format(_MUST_NOT_BE_NULL, "privilegeId"));

        rolePrivilegesRepository.findByRoleIdAndPrivilegeId(roleId, privilegeId)
                .ifPresent(rolePrivilege -> {
                    // actions为空，删除菜单
                    if (!StringUtils.hasText(action)) {
                        rolePrivilegesRepository.deleteById(rolePrivilege.getId());
                    }
                    privilegeRepository.findById(privilegeId).ifPresent(privilege ->
                            removeGroupAuthority(roleId, privilege.getName(), action));
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

    private void removeGroupAuthority(Long roleId, String name, String action) {
        groupRolesRepository.findAllByRoleId(roleId).forEach(groupRole -> {
                    // 移除授权actions
                    if (StringUtils.hasText(action)) {
                        groupAuthoritiesRepository.deleteByGroupIdAndAuthority(groupRole.getGroupId(), name + ":" + action);
                    } else {
                        groupAuthoritiesRepository.deleteByGroupIdAndAuthorityStartingWith(groupRole.getGroupId(), name);
                    }
                }
        );
    }

}
