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
package io.leafage.system.service.impl;

import io.leafage.system.domain.Privilege;
import io.leafage.system.domain.RoleMembers;
import io.leafage.system.domain.RolePrivileges;
import io.leafage.system.dto.PrivilegeDTO;
import io.leafage.system.repository.PrivilegeRepository;
import io.leafage.system.repository.RoleMembersRepository;
import io.leafage.system.repository.RolePrivilegesRepository;
import io.leafage.system.service.PrivilegeService;
import io.leafage.system.vo.PrivilegeVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import top.leafage.common.TreeNode;
import top.leafage.common.jdbc.JdbcTreeAndDomainConverter;

import java.util.*;

/**
 * privilege common impl.
 *
 * @author wq li
 */
@Service
public class PrivilegeServiceImpl extends JdbcTreeAndDomainConverter<Privilege, Long> implements PrivilegeService {

    public final RoleMembersRepository roleMembersRepository;
    public final RolePrivilegesRepository rolePrivilegesRepository;
    private final PrivilegeRepository privilegeRepository;

    /**
     * Constructor for PrivilegeServiceImpl.
     *
     * @param roleMembersRepository    a {@link RoleMembersRepository} object
     * @param rolePrivilegesRepository a {@link RolePrivilegesRepository} object
     * @param privilegeRepository      a {@link PrivilegeRepository} object
     */
    public PrivilegeServiceImpl(RoleMembersRepository roleMembersRepository, RolePrivilegesRepository rolePrivilegesRepository, PrivilegeRepository privilegeRepository) {
        this.roleMembersRepository = roleMembersRepository;
        this.rolePrivilegesRepository = rolePrivilegesRepository;
        this.privilegeRepository = privilegeRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PrivilegeVO> retrieve(int page, int size, String sortBy, boolean descending, String name) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        return privilegeRepository.findAllBySuperiorIdIsNull(pageable)
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
        List<Privilege> privileges = new ArrayList<>();
        List<RoleMembers> roleMembers = roleMembersRepository.findAllByUsername(username);
        if (CollectionUtils.isEmpty(roleMembers)) {
            return Collections.emptyList();
        }
        for (RoleMembers roleMember : roleMembers) {
            List<RolePrivileges> rolePrivileges = rolePrivilegesRepository.findAllByRoleId(roleMember.getRoleId());
            for (RolePrivileges rolePrivilege : rolePrivileges) {
                privilegeRepository.findById(rolePrivilege.getPrivilegeId()).ifPresent(privilege -> {
                    if (privilege.isEnabled()) {
                        privileges.add(privilege);
                    }
                });
            }
        }
        Set<String> meta = new HashSet<>();
        meta.add("path");
        meta.add("redirect");
        meta.add("component");
        meta.add("icon");
        meta.add("actions");
        return this.convertToTree(privileges, meta);
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
        return privilegeRepository.updateEnabledById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrivilegeVO modify(Long id, PrivilegeDTO dto) {
        Assert.notNull(id, "id must not be null.");
        return privilegeRepository.findById(id).map(existing -> {
                    existing = convert(dto, existing);
                    existing = privilegeRepository.save(existing);
                    return convertToVO(existing, PrivilegeVO.class);
                })
                .orElseThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Long id) {
        Assert.notNull(id, "id must not be null.");

        privilegeRepository.deleteById(id);
    }

}
