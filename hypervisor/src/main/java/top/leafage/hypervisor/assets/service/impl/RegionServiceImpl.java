/*
 *  Copyright 2018-2025 little3201.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package top.leafage.hypervisor.assets.service.impl;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.assets.domain.Region;
import top.leafage.hypervisor.assets.domain.dto.RegionDTO;
import top.leafage.hypervisor.assets.domain.vo.RegionVO;
import top.leafage.hypervisor.assets.repository.RegionRepository;
import top.leafage.hypervisor.assets.service.RegionService;

import java.util.NoSuchElementException;

/**
 * region service impl
 *
 * @author wq li
 */
@Service
public class RegionServiceImpl implements RegionService {

    private static final BeanCopier copier = BeanCopier.create(RegionDTO.class, Region.class, false);
    private final RegionRepository regionRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    /**
     * Constructor for RegionServiceImpl.
     *
     * @param regionRepository a {@link RegionRepository} object
     */
    public RegionServiceImpl(RegionRepository regionRepository, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.regionRepository = regionRepository;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Page<RegionVO>> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        Criteria criteria = buildCriteria(filters, Region.class);
        criteria = criteria.and("superiorId").isNull();

        return r2dbcEntityTemplate.select(Region.class)
                .matching(Query.query(criteria).with(pageable))
                .all()
                .map(RegionVO::from)
                .collectList()
                .zipWith(r2dbcEntityTemplate.count(Query.query(criteria), Region.class))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<RegionVO> fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return regionRepository.findById(id)
                .map(RegionVO::from);
    }

    @Transactional
    @Override
    public Mono<Boolean> enable(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return regionRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new NoSuchElementException("region not found: " + id));
                    }
                    return regionRepository.updateEnabledById(id)
                            .map(count -> count > 0);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<RegionVO> subset(Long superiorId) {
        Assert.notNull(superiorId, "superiorId must not be null.");

        return regionRepository.findBySuperiorId(superiorId)
                .map(RegionVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<RegionVO> create(RegionDTO dto) {
        return regionRepository.existsByName(dto.getName())
                .flatMap(exists -> {
                    if (exists) {
                        throw new IllegalArgumentException("name already exists: " + dto.getName());
                    }
                    return regionRepository.save(RegionDTO.toEntity(dto))
                            .map(RegionVO::from);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<RegionVO> modify(Long id, RegionDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return regionRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .flatMap(existing -> {
                    if (!existing.getName().equals(dto.getName())) {
                        return Mono.just(existing);
                    }

                    return regionRepository.existsByName(dto.getName())
                            .flatMap(exists -> {
                                if (exists) {
                                    return Mono.error(new IllegalArgumentException("region name already exists: " + dto.getName()));
                                }
                                return Mono.just(existing);
                            });
                })
                .flatMap(existing -> {
                    copier.copy(dto, existing, null);
                    return regionRepository.save(existing);
                })
                .map(RegionVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        return regionRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new NoSuchElementException("region not found: " + id));
                    }
                    return regionRepository.deleteById(id);
                });
    }

}
