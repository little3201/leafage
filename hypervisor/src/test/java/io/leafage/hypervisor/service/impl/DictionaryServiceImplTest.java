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

package io.leafage.hypervisor.service.impl;

import io.leafage.hypervisor.domain.Dictionary;
import io.leafage.hypervisor.dto.DictionaryDTO;
import io.leafage.hypervisor.repository.DictionaryRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveSelectOperation;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * dictionary service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class DictionaryServiceImplTest {

    @Mock
    private DictionaryRepository dictionaryRepository;

    @InjectMocks
    private DictionaryServiceImpl dictionaryService;

    @Mock
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    private DictionaryDTO dto;
    private Dictionary entity;

    @BeforeEach
    void setUp() {
        dto = new DictionaryDTO();
        dto.setName("Gender");
        dto.setDescription("描述");

        entity = new Dictionary();
        entity.setName("Gender");
        entity.setDescription("描述");
    }

    @Test
    void retrieve() {
        ReactiveSelectOperation.ReactiveSelect<Dictionary> select = mock(ReactiveSelectOperation.ReactiveSelect.class);
        ReactiveSelectOperation.TerminatingSelect<Dictionary> terminating = mock(ReactiveSelectOperation.TerminatingSelect.class);

        given(r2dbcEntityTemplate.select(Dictionary.class)).willReturn(select);
        given(select.matching(any(Query.class))).willReturn(terminating);
        given(terminating.all()).willReturn(Flux.just(entity));
        given(r2dbcEntityTemplate.count(any(Query.class), eq(Dictionary.class))).willReturn(Mono.just(1L));

        StepVerifier.create(dictionaryService.retrieve(0, 2, "id", true, "name:like:test"))
                .assertNext(page -> {
                    assertThat(page.getContent()).hasSize(1);
                    AssertionsForClassTypes.assertThat(page.getTotalElements()).isEqualTo(1);
                    AssertionsForClassTypes.assertThat(page.getNumber()).isEqualTo(0);
                    AssertionsForClassTypes.assertThat(page.getSize()).isEqualTo(2);
                }).verifyComplete();
    }

    @Test
    void fetch() {
        given(this.dictionaryRepository.findById(anyLong())).willReturn(Mono.just(mock(Dictionary.class)));

        StepVerifier.create(dictionaryService.fetch(anyLong())).expectNextCount(1).verifyComplete();
    }

    @Test
    void subset() {
        given(this.dictionaryRepository.findBySuperiorId(anyLong())).willReturn(Flux.just(mock(Dictionary.class)));

        StepVerifier.create(dictionaryService.subset(anyLong())).expectNextCount(1).verifyComplete();
    }

    @Test
    void create() {
        given(this.dictionaryRepository.save(any(Dictionary.class))).willReturn(Mono.just(mock(Dictionary.class)));

        StepVerifier.create(dictionaryService.create(dto)).expectNextCount(1).verifyComplete();
    }

    @Test
    void modify() {
        given(this.dictionaryRepository.findById(anyLong())).willReturn(Mono.just(mock(Dictionary.class)));

        given(this.dictionaryRepository.save(any(Dictionary.class))).willReturn(Mono.just(mock(Dictionary.class)));

        StepVerifier.create(dictionaryService.modify(anyLong(), dto)).expectNextCount(1).verifyComplete();
    }

    @Test
    void exists() {
        given(this.dictionaryRepository.existsByNameAndIdNot(anyString(), anyLong())).willReturn(Mono.just(Boolean.TRUE));

        StepVerifier.create(dictionaryService.exists("test", 1L)).expectNext(Boolean.TRUE).verifyComplete();
    }

    @Test
    void exists_id_null() {
        given(this.dictionaryRepository.existsByName(anyString())).willReturn(Mono.just(Boolean.TRUE));

        StepVerifier.create(dictionaryService.exists("test", null)).expectNext(Boolean.TRUE).verifyComplete();
    }

    @Test
    void remove() {
        given(this.dictionaryRepository.deleteById(anyLong())).willReturn(Mono.empty());

        StepVerifier.create(dictionaryService.remove(anyLong())).verifyComplete();
    }
}