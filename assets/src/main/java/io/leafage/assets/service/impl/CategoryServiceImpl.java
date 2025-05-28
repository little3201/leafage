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

import io.leafage.assets.domain.Category;
import io.leafage.assets.dto.CategoryDTO;
import io.leafage.assets.repository.CategoryRepository;
import io.leafage.assets.service.CategoryService;
import io.leafage.assets.vo.CategoryVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import javax.naming.NotContextException;

/**
 * category service impl
 *
 * @author wq li
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * <p>Constructor for CategoryServiceImpl.</p>
     *
     * @param categoryRepository a {@link CategoryRepository} object
     */
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Page<CategoryVO>> retrieve(int page, int size, String sortBy, boolean descending) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        return categoryRepository.findAllBy(pageable)
                .map(c -> convertToVO(c, CategoryVO.class))
                .collectList()
                .zipWith(categoryRepository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<CategoryVO> fetch(Long id) {
        Assert.notNull(id, "id must not be null.");

        return categoryRepository.findById(id)
                .map(c -> convertToVO(c, CategoryVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Boolean> exists(String name, Long id) {
        Assert.hasText(name, "name must not be empty.");

        return categoryRepository.existsByName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<CategoryVO> create(CategoryDTO dto) {
        return categoryRepository.save(convertToDomain(dto, Category.class))
                .map(c -> convertToVO(c, CategoryVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<CategoryVO> modify(Long id, CategoryDTO dto) {
        Assert.notNull(id, "id must not be null.");

        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(NotContextException::new))
                .map(category -> convert(dto, category))
                .flatMap(categoryRepository::save)
                .map(c -> convertToVO(c, CategoryVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> remove(Long id) {
        Assert.notNull(id, "id must not be null.");

        return categoryRepository.deleteById(id);
    }

}
