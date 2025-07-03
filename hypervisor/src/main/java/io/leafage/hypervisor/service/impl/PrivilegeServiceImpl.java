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

import io.leafage.hypervisor.domain.Privilege;
import io.leafage.hypervisor.dto.PrivilegeDTO;
import io.leafage.hypervisor.repository.*;
import io.leafage.hypervisor.service.PrivilegeService;
import io.leafage.hypervisor.vo.PrivilegeVO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.leafage.common.TreeNode;
import top.leafage.common.jdbc.JdbcTreeAndDomainConverter;

import java.util.*;

/**
 * privilege service impl.
 *
 * @author wq li
 */
@Service
public class PrivilegeServiceImpl extends JdbcTreeAndDomainConverter<Privilege, Long> implements PrivilegeService {

    public final RoleMembersRepository roleMembersRepository;
    public final RolePrivilegesRepository rolePrivilegesRepository;
    private final PrivilegeRepository privilegeRepository;
    private final GroupMembersRepository groupMembersRepository;
    private final GroupRolesRepository groupRolesRepository;
    private final GroupPrivilegesRepository groupPrivilegesRepository;

    /**
     * <p>Constructor for PrivilegeServiceImpl.</p>
     *
     * @param rolePrivilegesRepository a {@link RolePrivilegesRepository} object
     * @param privilegeRepository      a {@link PrivilegeRepository} object
     */
    public PrivilegeServiceImpl(RoleMembersRepository roleMembersRepository, RolePrivilegesRepository rolePrivilegesRepository,
                                PrivilegeRepository privilegeRepository, GroupMembersRepository groupMembersRepository, GroupRolesRepository groupRolesRepository, GroupPrivilegesRepository groupPrivilegesRepository) {
        this.roleMembersRepository = roleMembersRepository;
        this.rolePrivilegesRepository = rolePrivilegesRepository;
        this.privilegeRepository = privilegeRepository;
        this.groupMembersRepository = groupMembersRepository;
        this.groupRolesRepository = groupRolesRepository;
        this.groupPrivilegesRepository = groupPrivilegesRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PrivilegeVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<Privilege> spec = (root, query, cb) -> {
            Predicate filterPredicate = buildPredicate(filters, cb, root).orElse(null);
            Predicate superiorIsNull = cb.isNull(root.get("superiorId"));

            if (filterPredicate == null) {
                return superiorIsNull; // 只有 superiorId is null 条件
            } else {
                return cb.and(filterPredicate, superiorIsNull);
            }
        };

        return privilegeRepository.findAll(spec, pageable)
                .map(privilege -> {
                    PrivilegeVO vo = convertToVO(privilege, PrivilegeVO.class);
                    long count = privilegeRepository.countBySuperiorId(privilege.getId());
                    vo.setCount(count);
                    return vo;
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TreeNode<Long>> tree(String username) {
        Assert.hasText(username, "username must not be empty.");

        Map<Long, Privilege> privilegeMap = new HashMap<>();

        // group
        groupMembersRepository.findAllByUsername(username).forEach(groupMember ->
                groupRolesRepository.findAllByGroupId(groupMember.getGroupId()).forEach(groupRole -> {
                    groupPrivilegesRepository.findAllByGroupId(groupRole.getGroupId()).forEach(groupPrivilege ->
                            privileges(groupPrivilege.getPrivilegeId(), groupPrivilege.getActions(), privilegeMap));
                    rolePrivilegesRepository.findAllByRoleId(groupRole.getRoleId()).forEach(rolePrivilege ->
                            privileges(rolePrivilege.getPrivilegeId(), rolePrivilege.getActions(), privilegeMap));
                })
        );

        // role
        roleMembersRepository.findAllByUsername(username).forEach(roleMember ->
                rolePrivilegesRepository.findAllByRoleId(roleMember.getRoleId()).forEach(rolePrivilege ->
                        privileges(rolePrivilege.getPrivilegeId(), rolePrivilege.getActions(), privilegeMap))
        );

        List<Privilege> privileges = new ArrayList<>(privilegeMap.values());
        Set<String> meta = new HashSet<>();
        meta.add("path");
        meta.add("redirect");
        meta.add("component");
        meta.add("icon");
        meta.add("actions");
        return convertToTree(privileges, meta);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PrivilegeVO> subset(Long superiorId) {
        return privilegeRepository.findAllBySuperiorId(superiorId).stream()
                .map(privilege -> {
                    PrivilegeVO vo = convertToVO(privilege, PrivilegeVO.class);
                    long count = privilegeRepository.countBySuperiorId(privilege.getId());
                    vo.setCount(count);
                    return vo;
                }).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrivilegeVO fetch(Long id) {
        Assert.notNull(id, "id must not be null.");

        return privilegeRepository.findById(id)
                .map(privilege -> convertToVO(privilege, PrivilegeVO.class))
                .orElse(null);
    }

    @Override
    public boolean enable(Long id) {
        return privilegeRepository.updateEnabledById(id) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrivilegeVO modify(Long id, PrivilegeDTO dto) {
        Assert.notNull(id, "id must not be null.");
        return privilegeRepository.findById(id).map(existing -> {
                    Privilege privilege = convert(dto, existing);
                    privilege = privilegeRepository.save(privilege);
                    return convertToVO(privilege, PrivilegeVO.class);
                })
                .orElseThrow();
    }

    private void privileges(Long privilegeId, Set<String> actions, Map<Long, Privilege> privilegeMap) {
        privilegeRepository.findById(privilegeId).ifPresent(privilege -> {
            if (privilege.isEnabled() && !privilegeMap.containsKey(privilege.getId())) {
                privilege.setActions(actions);
                privilegeMap.put(privilege.getId(), privilege);
                // 处理没有勾选父级的数据（递归查找父级数据）
                addSuperior(privilege, privilegeMap);
            }
        });
    }

    private void addSuperior(Privilege privilege, Map<Long, Privilege> privilegeMap) {
        Long superiorId = privilege.getSuperiorId();
        if (superiorId != null && !privilegeMap.containsKey(superiorId)) {
            privilegeRepository.findById(superiorId).ifPresent(superior -> {
                if (superior.isEnabled()) {
                    privilegeMap.put(superior.getId(), superior);
                    // 递归，添加上级
                    addSuperior(superior, privilegeMap);
                }
            });
        }
    }

}
