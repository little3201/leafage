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

package io.leafage.assets.service.impl;

import io.leafage.assets.domain.Region;
import io.leafage.assets.dto.RegionDTO;
import io.leafage.assets.repository.RegionRepository;
import io.leafage.assets.service.RegionService;
import io.leafage.assets.vo.RegionVO;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.leafage.common.DomainConverter;

/**
 * region service impl.
 *
 * @author wq li
 */
@Service
public class RegionServiceImpl extends DomainConverter implements RegionService {

    private final RegionRepository regionRepository;

    /**
     * <p>Constructor for RegionServiceImpl.</p>
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
    public Page<RegionVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        return regionRepository.findAll(pageable)
                .map(region -> convertToVO(region, RegionVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RegionVO fetch(Long id) {
        Assert.notNull(id, "id must not be null.");
        Region region = regionRepository.findById(id).orElse(null);
        if (region == null) {
            return null;
        }
        return convertToVO(region, RegionVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String name, Long id) {
        Assert.hasText(name, "name must not bu empty.");
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
        Region region = new Region();
        BeanCopier copier = BeanCopier.create(RegionDTO.class, Region.class, false);
        copier.copy(dto, region, null);

        regionRepository.saveAndFlush(region);
        return convertToVO(region, RegionVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RegionVO modify(Long id, RegionDTO dto) {
        Assert.notNull(id, "id must not be null.");
        Region region = regionRepository.findById(id).orElse(null);
        if (region == null) {
            return null;
        }
        BeanCopier copier = BeanCopier.create(RegionDTO.class, Region.class, false);
        copier.copy(dto, region, null);

        regionRepository.save(region);
        return convertToVO(region, RegionVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Long id) {
        Assert.notNull(id, "id must not be null.");
        regionRepository.deleteById(id);
    }

}
