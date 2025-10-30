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

import io.leafage.assets.domain.Tag;
import io.leafage.assets.dto.TagDTO;
import io.leafage.assets.repository.TagRepository;
import io.leafage.assets.service.TagService;
import io.leafage.assets.vo.TagVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import top.leafage.common.DomainConverter;

import javax.naming.NotContextException;

/**
 * tag service impl
 *
 * @author wq li
 */
@Service
public class TagServiceImpl extends DomainConverter implements TagService {

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
                .map(tag -> convertToVO(tag, TagVO.class))
                .collectList()
                .zipWith(r2dbcEntityTemplate.count(Query.query(criteria), Tag.class))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<TagVO> fetch(Long id) {
        Assert.notNull(id, "id must not be null.");

        return tagRepository.findById(id)
                .map(c -> convertToVO(c, TagVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Boolean> exists(String name, Long id) {
        Assert.hasText(name, "name must not be empty.");
        if (id == null) {
            return tagRepository.existsByName(name);
        }
        return tagRepository.existsByNameAndIdNot(name, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<TagVO> create(TagDTO dto) {
        return tagRepository.save(convertToDomain(dto, Tag.class))
                .map(c -> convertToVO(c, TagVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<TagVO> modify(Long id, TagDTO dto) {
        Assert.notNull(id, "id must not be null.");

        return tagRepository.findById(id)
                .switchIfEmpty(Mono.error(NotContextException::new))
                .map(tag -> convert(dto, tag))
                .flatMap(tagRepository::save)
                .map(c -> convertToVO(c, TagVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> remove(Long id) {
        Assert.notNull(id, "id must not be null.");

        return tagRepository.deleteById(id);
    }

}
