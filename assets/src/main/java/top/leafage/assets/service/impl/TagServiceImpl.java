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

package top.leafage.assets.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import top.leafage.assets.domain.Tag;
import top.leafage.assets.domain.vo.TagVO;
import top.leafage.assets.repository.TagRepository;
import top.leafage.assets.service.TagService;

import java.util.NoSuchElementException;

/**
 * tag service impl
 *
 * @author wq li
 */
@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    /**
     * <p>Constructor for CategoryServiceImpl.</p>
     *
     * @param tagRepository a {@link TagRepository} object
     */
    public TagServiceImpl(TagRepository tagRepository, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.tagRepository = tagRepository;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Page<TagVO>> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        Criteria criteria = buildCriteria(filters, Tag.class);

        return r2dbcEntityTemplate.select(Tag.class)
                .matching(Query.query(criteria).with(pageable))
                .all()
                .map(TagVO::from)
                .collectList()
                .zipWith(r2dbcEntityTemplate.count(Query.query(criteria), Tag.class))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<TagVO> fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return tagRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .map(TagVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        return tagRepository.existsById(id)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new NoSuchElementException("tag not found: " + id));
                    }
                    return tagRepository.deleteById(id);
                });
    }

}
