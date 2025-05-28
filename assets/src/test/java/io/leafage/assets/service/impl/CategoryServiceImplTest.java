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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.BDDMockito.given;

/**
 * category service test
 *
 * @author wq li
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        categoryDTO = new CategoryDTO();
        categoryDTO.setName("test");
        categoryDTO.setDescription("category");
    }

    @Test
    void retrieve() {
        given(this.categoryRepository.findAllBy(Mockito.any(PageRequest.class))).willReturn(Flux.just(Mockito.mock(Category.class)));

        given(this.categoryRepository.count()).willReturn(Mono.just(Mockito.anyLong()));

        StepVerifier.create(this.categoryService.retrieve(0, 2, "id", true)).expectNextCount(1).verifyComplete();
    }

    @Test
    void fetch() {
        given(this.categoryRepository.findById(Mockito.anyLong()))
                .willReturn(Mono.just(Mockito.mock(Category.class)));

        StepVerifier.create(categoryService.fetch(Mockito.anyLong())).expectNextCount(1).verifyComplete();
    }

    @Test
    void exists() {
        given(this.categoryRepository.existsByName(Mockito.anyString())).willReturn(Mono.just(Boolean.TRUE));

        StepVerifier.create(categoryService.exists("test", 1L)).expectNext(Boolean.TRUE).verifyComplete();
    }

    @Test
    void create() {
        given(this.categoryRepository.save(Mockito.any(Category.class))).willReturn(Mono.just(Mockito.mock(Category.class)));

        StepVerifier.create(categoryService.create(Mockito.mock(CategoryDTO.class))).expectNextCount(1).verifyComplete();
    }

    @Test
    void modify() {
        given(this.categoryRepository.findById(Mockito.anyLong())).willReturn(Mono.just(Mockito.mock(Category.class)));

        given(this.categoryRepository.save(Mockito.any(Category.class))).willReturn(Mono.just(Mockito.mock(Category.class)));

        StepVerifier.create(categoryService.modify(1L, categoryDTO)).expectNextCount(1).verifyComplete();
    }

    @Test
    void remove() {
        given(this.categoryRepository.deleteById(Mockito.anyLong())).willReturn(Mono.empty());

        StepVerifier.create(categoryService.remove(Mockito.anyLong())).verifyComplete();
    }
}