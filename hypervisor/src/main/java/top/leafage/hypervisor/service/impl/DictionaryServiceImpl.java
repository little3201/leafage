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

package top.leafage.hypervisor.service.impl;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.domain.Dictionary;
import top.leafage.hypervisor.domain.dto.DictionaryDTO;
import top.leafage.hypervisor.domain.vo.DictionaryVO;
import top.leafage.hypervisor.repository.DictionaryRepository;
import top.leafage.hypervisor.service.DictionaryService;

import java.util.NoSuchElementException;

/**
 * dictionary service impl
 *
 * @author wq li
 */
@Service
public class DictionaryServiceImpl implements DictionaryService {

    private static final BeanCopier copier = BeanCopier.create(DictionaryDTO.class, Dictionary.class, false);
    private final DictionaryRepository dictionaryRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    /**
     * Constructor for DictionaryServiceImpl.
     *
     * @param dictionaryRepository a {@link DictionaryRepository} object
     */
    public DictionaryServiceImpl(DictionaryRepository dictionaryRepository, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.dictionaryRepository = dictionaryRepository;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Page<DictionaryVO>> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        Criteria criteria = buildCriteria(filters, Dictionary.class);
        criteria = criteria.and("superiorId").isNull();

        return r2dbcEntityTemplate.select(Dictionary.class)
                .matching(Query.query(criteria).with(pageable))
                .all()
                .map(DictionaryVO::from)
                .collectList()
                .zipWith(r2dbcEntityTemplate.count(Query.query(criteria), Dictionary.class))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<DictionaryVO> subset(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return dictionaryRepository.findBySuperiorId(id)
                .map(DictionaryVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<DictionaryVO> fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return dictionaryRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .map(DictionaryVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<DictionaryVO> create(DictionaryDTO dto) {
        return dictionaryRepository.existsByName(dto.getName())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("name already exists: " + dto.getName()));
                    }
                    return dictionaryRepository.save(DictionaryDTO.toEntity(dto))
                            .map(DictionaryVO::from);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<DictionaryVO> modify(Long id, DictionaryDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return dictionaryRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .flatMap(existing -> {
                    if (!existing.getName().equals(dto.getName())) {
                        return Mono.just(existing);
                    }

                    return dictionaryRepository.existsByName(dto.getName())
                            .flatMap(exists -> {
                                if (exists) {
                                    return Mono.error(new IllegalArgumentException("dictionary name already exists: " + dto.getName()));
                                }
                                return Mono.just(existing);
                            });
                })
                .flatMap(existing -> {
                    copier.copy(dto, existing, null);
                    return dictionaryRepository.save(existing);
                })
                .map(DictionaryVO::from);
    }

    @Override
    public Mono<Void> remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        return dictionaryRepository.existsById(id)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new NoSuchElementException("dictionary not found: " + id));
                    }
                    return dictionaryRepository.deleteById(id);
                });
    }
}
