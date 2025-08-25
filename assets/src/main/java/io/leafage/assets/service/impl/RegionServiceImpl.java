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

package io.leafage.assets.service.impl;

import io.leafage.assets.domain.Region;
import io.leafage.assets.dto.RegionDTO;
import io.leafage.assets.repository.RegionRepository;
import io.leafage.assets.service.RegionService;
import io.leafage.assets.vo.RegionVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.DomainConverter;

import java.util.NoSuchElementException;

/**
 * region service impl
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
    public Mono<Page<RegionVO>> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        return regionRepository.findAllBySuperiorIdIsNull(pageable)
                .map(r -> convertToVO(r, RegionVO.class))
                .collectList()
                .zipWith(regionRepository.countBySuperiorIdIsNullAndEnabledTrue())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<RegionVO> fetch(Long id) {
        Assert.notNull(id, "id must not be null.");

        return regionRepository.findById(id)
                .map(r -> convertToVO(r, RegionVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Boolean> exists(String name, Long id) {
        Assert.hasText(name, "name must not be empty.");

        if (id == null) {
            return regionRepository.existsByName(name);
        }
        return regionRepository.existsByNameAndIdNot(name, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<RegionVO> subset(Long superiorId) {
        Assert.notNull(superiorId, "superiorId must not be null.");

        return regionRepository.findBySuperiorId(superiorId)
                .map(r -> convertToVO(r, RegionVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<RegionVO> create(RegionDTO dto) {
        return regionRepository.save(convertToDomain(dto, Region.class))
                .map(r -> convertToVO(r, RegionVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<RegionVO> modify(Long id, RegionDTO dto) {
        Assert.notNull(id, "id must not be null.");
        return regionRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .map(region -> convert(dto, region))
                .flatMap(regionRepository::save)
                .map(r -> convertToVO(r, RegionVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> remove(Long id) {
        Assert.notNull(id, "id must not be null.");
        return regionRepository.deleteById(id);
    }

}
