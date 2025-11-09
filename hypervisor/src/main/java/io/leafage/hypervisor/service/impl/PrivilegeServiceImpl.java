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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.leafage.common.TreeNode;
import top.leafage.common.jdbc.JdbcTreeAndDomainConverter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        Specification<Privilege> spec = (root, query, cb) ->
                buildPredicate(filters, cb, root).orElse(null);
        spec = spec.and((root, query, cb) -> cb.isNull(root.get("superiorId")));

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
        Assert.hasText(username, String.format(_MUST_NOT_BE_EMPTY, "username"));

        Map<Long, Set<String>> privilegeActionsMap = new HashMap<>();
        // Group
        groupMembersRepository.findAllByUsername(username).forEach(gm ->
                groupRolesRepository.findAllByGroupId(gm.getGroupId()).forEach(gr -> {
                    // GroupPrivileges
                    groupPrivilegesRepository.findAllByGroupId(gr.getGroupId())
                            .forEach(gp -> mergeActions(gp.getPrivilegeId(), gp.getActions(), privilegeActionsMap));
                    // RolePrivileges (from GroupRole)
                    rolePrivilegesRepository.findAllByRoleId(gr.getRoleId())
                            .forEach(rp -> mergeActions(rp.getPrivilegeId(), rp.getActions(), privilegeActionsMap));
                })
        );

        // Role
        roleMembersRepository.findAllByUsername(username).forEach(rm ->
                rolePrivilegesRepository.findAllByRoleId(rm.getRoleId())
                        .forEach(rp -> mergeActions(rp.getPrivilegeId(), rp.getActions(), privilegeActionsMap))
        );

        if (privilegeActionsMap.isEmpty()) {
            return Collections.emptyList();
        }
        List<Privilege> directPrivileges = privilegeRepository.findAllById(privilegeActionsMap.keySet());
        Map<Long, Privilege> privilegeMap = directPrivileges.stream()
                .filter(Privilege::isEnabled)
                .collect(Collectors.toMap(Privilege::getId, Function.identity(), (a, b) -> a));

        // 设置 actions
        privilegeActionsMap.forEach((id, actions) -> {
            Privilege p = privilegeMap.get(id);
            if (p != null) {
                p.setActions(actions);
            }
        });

        expandPrivileges(privilegeMap);

        List<Privilege> allPrivileges = new ArrayList<>(privilegeMap.values());
        Set<String> meta = Set.of("path", "redirect", "component", "icon", "actions");
        return convertToTree(allPrivileges, meta);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PrivilegeVO> subset(Long superiorId) {
        Assert.notNull(superiorId, String.format(_MUST_NOT_BE_NULL, "superiorId"));

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
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return privilegeRepository.findById(id)
                .map(privilege -> convertToVO(privilege, PrivilegeVO.class))
                .orElse(null);
    }

    @Override
    public boolean enable(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return privilegeRepository.updateEnabledById(id) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrivilegeVO modify(Long id, PrivilegeDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return privilegeRepository.findById(id).map(existing -> {
                    Privilege privilege = convert(dto, existing);
                    privilege = privilegeRepository.save(privilege);
                    return convertToVO(privilege, PrivilegeVO.class);
                })
                .orElseThrow();
    }

    private void mergeActions(Long privilegeId, Set<String> actions, Map<Long, Set<String>> map) {
        map.computeIfAbsent(privilegeId, k -> new HashSet<>()).addAll(actions);
    }

    private Set<Long> collectMissingSuperiorIds(Map<Long, Privilege> privilegeMap) {
        return privilegeMap.values().stream()
                .map(Privilege::getSuperiorId)
                .filter(Objects::nonNull)
                .filter(id -> !privilegeMap.containsKey(id))
                .collect(Collectors.toSet());
    }

    private void expandPrivileges(Map<Long, Privilege> privilegeMap) {
        Set<Long> toLoad;
        do {
            toLoad = collectMissingSuperiorIds(privilegeMap);
            if (toLoad.isEmpty()) break;

            privilegeRepository.findAllById(toLoad).stream()
                    .filter(Privilege::isEnabled)
                    .forEach(sup -> privilegeMap.putIfAbsent(sup.getId(), sup));

        } while (true);
    }

}
