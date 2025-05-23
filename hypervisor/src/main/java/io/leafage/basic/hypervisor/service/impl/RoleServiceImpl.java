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

import io.leafage.basic.hypervisor.domain.Role;
import io.leafage.basic.hypervisor.dto.RoleDTO;
import io.leafage.basic.hypervisor.repository.RoleRepository;
import io.leafage.basic.hypervisor.service.RoleService;
import io.leafage.basic.hypervisor.vo.RoleVO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * role service impl.
 *
 * @author wq li
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    /**
     * <p>Constructor for RoleServiceImpl.</p>
     *
     * @param roleRepository a {@link io.leafage.basic.hypervisor.repository.RoleRepository} object
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

        Specification<Role> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return roleRepository.findAll(spec, pageable)
                .map(role -> convertToVO(role, RoleVO.class));
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
    public boolean enable(Long id) {
        return roleRepository.updateEnabledById(id) > 0;
    }

    @Override
    public boolean exists(String name, Long id) {
        if (id == null) {
            return roleRepository.existsByName(name);
        }
        return roleRepository.existsByNameAndIdNot(name, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoleVO create(RoleDTO dto) {
        Role role = convertToDomain(dto, Role.class);

        roleRepository.saveAndFlush(role);
        return convertToVO(role, RoleVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoleVO modify(Long id, RoleDTO dto) {
        Assert.notNull(id, "id must not be null.");
        return roleRepository.findById(id).map(existing -> {
            Role role = convert(dto, existing);
            role = roleRepository.save(role);
            return convertToVO(role, RoleVO.class);
        }).orElseThrow();
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
