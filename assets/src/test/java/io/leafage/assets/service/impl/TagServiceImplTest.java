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
 * tag service test
 *
 * @author wq li
 */
@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    private TagDTO dto;

    @BeforeEach
    void setUp() {
        dto = new TagDTO();
        dto.setName("test");
    }

    @Test
    void retrieve() {
        given(this.tagRepository.findAllBy(Mockito.any(PageRequest.class))).willReturn(Flux.just(Mockito.mock(Tag.class)));

        given(this.tagRepository.count()).willReturn(Mono.just(Mockito.anyLong()));

        StepVerifier.create(this.tagService.retrieve(0, 2, "id", true, "name:like:a")).expectNextCount(1).verifyComplete();
    }

    @Test
    void fetch() {
        given(this.tagRepository.findById(Mockito.anyLong()))
                .willReturn(Mono.just(Mockito.mock(Tag.class)));

        StepVerifier.create(tagService.fetch(Mockito.anyLong())).expectNextCount(1).verifyComplete();
    }

    @Test
    void exists() {
        given(this.tagRepository.existsByNameAndIdNot(Mockito.anyString(), Mockito.anyLong())).willReturn(Mono.just(Boolean.TRUE));

        StepVerifier.create(tagService.exists("test", 1L)).expectNext(Boolean.TRUE).verifyComplete();
    }

    @Test
    void exists_id_null() {
        given(this.tagRepository.existsByName(Mockito.anyString())).willReturn(Mono.just(Boolean.TRUE));

        StepVerifier.create(tagService.exists("test", null)).expectNext(Boolean.TRUE).verifyComplete();
    }

    @Test
    void create() {
        given(this.tagRepository.save(Mockito.any(Tag.class))).willReturn(Mono.just(Mockito.mock(Tag.class)));

        StepVerifier.create(tagService.create(Mockito.mock(TagDTO.class))).expectNextCount(1).verifyComplete();
    }

    @Test
    void modify() {
        given(this.tagRepository.findById(Mockito.anyLong())).willReturn(Mono.just(Mockito.mock(Tag.class)));

        given(this.tagRepository.save(Mockito.any(Tag.class))).willReturn(Mono.just(Mockito.mock(Tag.class)));

        StepVerifier.create(tagService.modify(1L, dto)).expectNextCount(1).verifyComplete();
    }

    @Test
    void remove() {
        given(this.tagRepository.deleteById(Mockito.anyLong())).willReturn(Mono.empty());

        StepVerifier.create(tagService.remove(Mockito.anyLong())).verifyComplete();
    }
}