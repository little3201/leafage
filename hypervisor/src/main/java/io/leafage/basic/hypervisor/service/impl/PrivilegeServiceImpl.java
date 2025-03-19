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
import io.leafage.basic.hypervisor.domain.RoleMembers;
import io.leafage.basic.hypervisor.domain.RolePrivileges;
import io.leafage.basic.hypervisor.dto.PrivilegeDTO;
import io.leafage.basic.hypervisor.repository.PrivilegeRepository;
import io.leafage.basic.hypervisor.repository.RoleMembersRepository;
import io.leafage.basic.hypervisor.repository.RolePrivilegesRepository;
import io.leafage.basic.hypervisor.service.PrivilegeService;
import io.leafage.basic.hypervisor.vo.PrivilegeVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import top.leafage.common.TreeNode;
import top.leafage.common.servlet.ServletAbstractTreeNodeService;

import java.util.*;

/**
 * privilege service impl.
 *
 * @author wq li
 */
@Service
public class PrivilegeServiceImpl extends ServletAbstractTreeNodeService<Privilege> implements PrivilegeService {

    public final RoleMembersRepository roleMembersRepository;
    public final RolePrivilegesRepository rolePrivilegesRepository;
    private final PrivilegeRepository privilegeRepository;

    /**
     * <p>Constructor for PrivilegeServiceImpl.</p>
     *
     * @param rolePrivilegesRepository a {@link io.leafage.basic.hypervisor.repository.RolePrivilegesRepository} object
     * @param privilegeRepository      a {@link io.leafage.basic.hypervisor.repository.PrivilegeRepository} object
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
    public List<TreeNode> tree(String username) {
        Assert.hasText(username, "username must not be empty.");

        List<RoleMembers> roleMembers = roleMembersRepository.findAllByUsername(username);
        if (CollectionUtils.isEmpty(roleMembers)) {
            return Collections.emptyList();
        }

        Map<Long, Privilege> privilegeMap = new HashMap<>();
        for (RoleMembers roleMember : roleMembers) {
            List<RolePrivileges> rolePrivileges = rolePrivilegesRepository.findAllByRoleId(roleMember.getRoleId());
            for (RolePrivileges rolePrivilege : rolePrivileges) {
                privilegeRepository.findById(rolePrivilege.getPrivilegeId()).ifPresent(privilege -> {
                    if (privilege.isEnabled() && !privilegeMap.containsKey(privilege.getId())) {
                        privilegeMap.put(privilege.getId(), privilege);
                        // 处理没有勾选父级的数据（递归查找父级数据）
                        addSuperior(privilege, privilegeMap);
                    }
                });
            }
        }

        List<Privilege> privileges = new ArrayList<>(privilegeMap.values());
        return this.convertTree(privileges);
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
                    Privilege privilege = convert(dto, existing);
                    privilege = privilegeRepository.save(privilege);
                    return convertToVO(privilege, PrivilegeVO.class);
                })
                .orElseThrow();
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

    /**
     * 转换为TreeNode
     *
     * @param privileges 集合数据
     * @return 树集合
     */
    private List<TreeNode> convertTree(List<Privilege> privileges) {
        if (CollectionUtils.isEmpty(privileges)) {
            return Collections.emptyList();
        }
        Set<String> meta = new HashSet<>();
        meta.add("path");
        meta.add("redirect");
        meta.add("component");
        meta.add("icon");
        meta.add("actions");
        return convertToTree(privileges, meta);
    }

}
