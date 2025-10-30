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

import io.leafage.system.domain.Role;
import io.leafage.system.dto.RoleDTO;
import io.leafage.system.repository.RoleRepository;
import io.leafage.system.service.RoleService;
import io.leafage.system.vo.RoleVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import top.leafage.common.DomainConverter;

/**
 * role common impl.
 *
 * @author wq li
 */
@Service
public class RoleServiceImpl extends DomainConverter implements RoleService {

    private final RoleRepository roleRepository;

    /**
     * <p>Constructor for RoleServiceImpl.</p>
     *
     * @param roleRepository a {@link RoleRepository} object
     */
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<RoleVO> retrieve(int page, int size, String sortBy, boolean descending, String name) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        if (StringUtils.hasText(name)) {
            return roleRepository.findAllByNameContaining(name, pageable)
                    .map(role -> convertToVO(role, RoleVO.class));
        }
        return roleRepository.findAll(pageable).map(role -> convertToVO(role, RoleVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoleVO fetch(Long id) {
        Assert.notNull(id, "id must not be null.");

        return roleRepository.findById(id)
                .map(role -> convertToVO(role, RoleVO.class)).orElse(null);
    }

    @Override
    public boolean exists(String name, Long id) {
        Assert.hasText(name, "name must not be empty.");
        if (id == null) {
            return roleRepository.existsByName(name);
        }
        return roleRepository.existsByNameAndIdNot(name, id);
    }

    @Override
    public boolean enable(Long id) {
        Assert.notNull(id, "id must not be null.");

        return roleRepository.updateEnabledById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoleVO create(RoleDTO dto) {
        Role role = convertToDomain(dto, Role.class);

        roleRepository.save(role);
        return convertToVO(role, RoleVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoleVO modify(Long id, RoleDTO dto) {
        Assert.notNull(id, "id must not be null.");
        return roleRepository.findById(id).map(existing -> {
                    existing = convert(dto, existing);
                    existing = roleRepository.save(existing);
                    return convertToVO(existing, RoleVO.class);
                })
                .orElseThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Long id) {
        Assert.notNull(id, "id must not be null.");
        roleRepository.deleteById(id);
    }

}
