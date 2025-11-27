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
package top.leafage.hypervisor.service.impl;

import top.leafage.hypervisor.domain.Role;
import top.leafage.hypervisor.domain.dto.RoleDTO;
import top.leafage.hypervisor.domain.vo.RoleVO;
import top.leafage.hypervisor.repository.RoleRepository;
import top.leafage.hypervisor.service.RoleService;
import org.jspecify.annotations.NonNull;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * role service impl.
 *
 * @author wq li
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private static final BeanCopier copier = BeanCopier.create(RoleDTO.class, Role.class, false);

    /**
     * Constructor for RoleServiceImpl.
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
    public Page<@NonNull RoleVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<@NonNull Role> spec = (root, query, cb) ->
                buildPredicate(filters, cb, root).orElse(null);

        return roleRepository.findAll(spec, pageable)
                .map(RoleVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoleVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return roleRepository.findById(id)
                .map(RoleVO::from)
                .orElse(null);
    }

    @Override
    public boolean enable(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return roleRepository.updateEnabledById(id) > 0;
    }

    @Override
    public boolean exists(String name, Long id) {
        Assert.hasText(name, String.format(_MUST_NOT_BE_EMPTY, "name"));

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
        Role entity = roleRepository.saveAndFlush(RoleDTO.toEntity(dto));
        return RoleVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoleVO modify(Long id, RoleDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        Role entity = roleRepository.findById(id).map(existing -> {
                    copier.copy(dto, existing, null);
                    return roleRepository.save(existing);
                })
                .orElseThrow();
        return RoleVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        roleRepository.deleteById(id);
    }

}
