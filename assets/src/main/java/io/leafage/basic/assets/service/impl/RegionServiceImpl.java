/*
 * Copyright (c) 2024.  little3201.
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

package io.leafage.basic.assets.service.impl;

import io.leafage.basic.assets.domain.Region;
import io.leafage.basic.assets.dto.RegionDTO;
import io.leafage.basic.assets.repository.RegionRepository;
import io.leafage.basic.assets.service.RegionService;
import io.leafage.basic.assets.vo.RegionVO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * region service impl.
 *
 * @author wq li
 */
@Service
public class RegionServiceImpl implements RegionService {

    private final RegionRepository regionRepository;

    /**
     * <p>Constructor for RegionServiceImpl.</p>
     *
     * @param regionRepository a {@link io.leafage.basic.assets.repository.RegionRepository} object
     */
    public RegionServiceImpl(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<RegionVO> retrieve(int page, int size, String sortBy, boolean descending, Long superiorId, String name) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<Region> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (superiorId == null) {
                predicates.add(cb.isNull(root.get("superiorId")));
            } else {
                predicates.add(cb.equal(root.get("superiorId"), superiorId));
            }
            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return regionRepository.findAll(spec, pageable).map(region -> convertToVO(region, RegionVO.class));
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
