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

package top.leafage.assets.service.impl;

import top.leafage.assets.domain.Region;
import top.leafage.assets.domain.dto.RegionDTO;
import top.leafage.assets.domain.vo.RegionVO;
import top.leafage.assets.repository.RegionRepository;
import top.leafage.assets.service.RegionService;
import org.jspecify.annotations.NonNull;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * region service impl.
 *
 * @author wq li
 */
@Service
public class RegionServiceImpl implements RegionService {

    private final RegionRepository regionRepository;
    private static final BeanCopier copier = BeanCopier.create(RegionDTO.class, Region.class, false);

    /**
     * Constructor for RegionServiceImpl.
     *
     * @param regionRepository a {@link RegionRepository} object
     */
    public RegionServiceImpl(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<@NonNull RegionVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<@NonNull Region> spec = (root, query, cb) ->
                buildPredicate(filters, cb, root).orElse(null);
        spec = spec.and((root, query, cb) -> cb.isNull(root.get("superiorId")));

        return regionRepository.findAll(spec, pageable)
                .map(RegionVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RegionVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return regionRepository.findById(id)
                .map(RegionVO::from)
                .orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String name, Long id) {
        Assert.hasText(name, String.format(_MUST_NOT_BE_EMPTY, "name"));

        if (id == null) {
            return regionRepository.existsByName(name);
        }
        return regionRepository.existsByNameAndIdNot(name, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RegionVO create(RegionDTO dto) {
        Region entity = regionRepository.saveAndFlush(RegionDTO.toEntity(dto));
        return RegionVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RegionVO modify(Long id, RegionDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return regionRepository.findById(id)
                .map(existing -> {
                    copier.copy(dto, existing, null);
                    return regionRepository.save(existing);
                })
                .map(RegionVO::from)
                .orElseThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        regionRepository.deleteById(id);
    }

}
