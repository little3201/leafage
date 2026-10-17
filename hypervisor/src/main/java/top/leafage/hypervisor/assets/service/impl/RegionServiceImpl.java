/*
 *
 *  * Copyright(c) 2019-present the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *       https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package top.leafage.hypervisor.assets.service.impl;

import org.jspecify.annotations.NonNull;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import top.leafage.hypervisor.assets.domain.Region;
import top.leafage.hypervisor.assets.domain.dto.RegionDTO;
import top.leafage.hypervisor.assets.domain.vo.RegionVO;
import top.leafage.hypervisor.assets.repository.RegionRepository;
import top.leafage.hypervisor.assets.service.RegionService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.data.relational.core.query.Query.query;

/**
 * region service impl.
 *
 * @author wq li
 */
@Service
public class RegionServiceImpl implements RegionService {

    private static final BeanCopier copier = BeanCopier.create(RegionDTO.class, Region.class, false);
    private final RegionRepository regionRepository;
    private final JdbcAggregateTemplate jdbcAggregateTemplate;

    /**
     * Constructor for RegionServiceImpl.
     *
     * @param regionRepository a {@link RegionRepository} object
     */
    public RegionServiceImpl(RegionRepository regionRepository, JdbcAggregateTemplate jdbcAggregateTemplate) {
        this.regionRepository = regionRepository;
        this.jdbcAggregateTemplate = jdbcAggregateTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<@NonNull RegionVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        Criteria criteria = buildCriteria(filters, Region.class);
        criteria = criteria.and("superiorId").isNull();

        List<RegionVO> voList = jdbcAggregateTemplate.findAll(query(criteria).with(pageable), Region.class)
                .stream().map(entity -> {
                    long count = regionRepository.countBySuperiorId(entity.getId());
                    return RegionVO.from(entity, count);
                })
                .toList();
        return new PageImpl<>(voList, pageable, jdbcAggregateTemplate.count(query(criteria), Region.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RegionVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return regionRepository.findById(id)
                .map(RegionVO::from)
                .orElseThrow(() -> new NoSuchElementException("region not found: " + id));
    }

    @Transactional
    @Override
    public boolean enable(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        if (!regionRepository.existsById(id)) {
            throw new NoSuchElementException("region not found: " + id);
        }
        return regionRepository.updateEnabledById(id) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RegionVO> subset(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return regionRepository.findAllBySuperiorId(id)
                .stream().map(entity -> {
                    long count = regionRepository.countBySuperiorId(entity.getId());
                    return RegionVO.from(entity, count);
                })
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public RegionVO create(RegionDTO dto) {
        if (regionRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("name already exists: " + dto.getName());
        }
        Region entity = regionRepository.save(RegionDTO.toEntity(dto));
        return RegionVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public RegionVO modify(Long id, RegionDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        Region existing = regionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("region not found: " + id));
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
    @Transactional
    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        if (!regionRepository.existsById(id)) {
            throw new NoSuchElementException("region not found: " + id);
        }
        regionRepository.deleteById(id);
    }

}
