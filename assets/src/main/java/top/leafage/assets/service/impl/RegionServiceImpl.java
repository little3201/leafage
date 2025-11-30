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

import jakarta.persistence.EntityNotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.leafage.assets.domain.Region;
import top.leafage.assets.domain.dto.RegionDTO;
import top.leafage.assets.domain.vo.RegionVO;
import top.leafage.assets.repository.RegionRepository;
import top.leafage.assets.service.RegionService;

/**
 * region service impl.
 *
 * @author wq li
 */
@Service
public class RegionServiceImpl implements RegionService {

    private static final BeanCopier copier = BeanCopier.create(RegionDTO.class, Region.class, false);
    private final RegionRepository regionRepository;

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
                .orElseThrow(() -> new EntityNotFoundException("region not found: " + id));
    }


    @Override
    public boolean enable(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        if (!regionRepository.existsById(id)) {
            throw new EntityNotFoundException("region not found: " + id);
        }
        return regionRepository.updateEnabledById(id) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RegionVO create(RegionDTO dto) {
        if (regionRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("name already exists: " + dto.getName());
        }
        Region entity = regionRepository.saveAndFlush(RegionDTO.toEntity(dto));
        return RegionVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RegionVO modify(Long id, RegionDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        Region existing = regionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("region not found: " + id));
        if (!existing.getName().equals(dto.getName()) &&
                regionRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("name already exists: " + dto.getName());
        }
        copier.copy(dto, existing, null);
        Region entity = regionRepository.save(existing);
        return RegionVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        if (!regionRepository.existsById(id)) {
            throw new EntityNotFoundException("region not found: " + id);
        }
        regionRepository.deleteById(id);
    }

}
