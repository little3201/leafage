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
import top.leafage.hypervisor.assets.domain.Region;
import top.leafage.hypervisor.assets.domain.dto.RegionDTO;
import top.leafage.hypervisor.assets.repository.RegionRepository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * region service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class RegionServiceImplTest {

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    @InjectMocks
    private RegionServiceImpl regionService;

    private RegionDTO dto;
    private Region entity;

    @BeforeEach
    void setUp() {
        dto = new RegionDTO();
        dto.setName("西安市");
        dto.setAreaCode("029");
        dto.setPostalCode("710000");
        dto.setSuperiorId(1L);

        entity = new Region();
        entity.setName("西安市");
        entity.setAreaCode("029");
        entity.setPostalCode("710000");
        entity.setSuperiorId(1L);
    }

    @Test
    void retrieve() {
        ReactiveSelectOperation.ReactiveSelect<Region> select = mock(ReactiveSelectOperation.ReactiveSelect.class);
        ReactiveSelectOperation.TerminatingSelect<Region> terminating = mock(ReactiveSelectOperation.TerminatingSelect.class);

        given(r2dbcEntityTemplate.select(Region.class)).willReturn(select);
        given(select.matching(any(Query.class))).willReturn(terminating);
        given(terminating.all()).willReturn(Flux.just(entity));
        given(r2dbcEntityTemplate.count(any(Query.class), eq(Region.class))).willReturn(Mono.just(1L));

        StepVerifier.create(regionService.retrieve(0, 2, "id", true, "name:like:test"))
                .assertNext(page -> {
                    assertThat(page.getContent()).hasSize(1);
                    assertThat(page.getTotalElements()).isEqualTo(1);
                    assertThat(page.getNumber()).isEqualTo(0);
                    assertThat(page.getSize()).isEqualTo(2);
                }).verifyComplete();
    }

    @Test
    void fetch() {
        given(this.regionRepository.findById(anyLong())).willReturn(Mono.just(mock(Region.class)));

        StepVerifier.create(regionService.fetch(anyLong())).expectNextCount(1).verifyComplete();
    }

    @Test
    void subset() {
        given(this.regionRepository.findBySuperiorId(anyLong())).willReturn(Flux.just(mock(Region.class)));

        StepVerifier.create(regionService.subset(anyLong())).expectNextCount(1).verifyComplete();
    }

    @Test
    void create() {
        given(this.regionRepository.save(any(Region.class))).willReturn(Mono.just(mock(Region.class)));

        StepVerifier.create(regionService.create(mock(RegionDTO.class))).expectNextCount(1).verifyComplete();
    }

    @Test
    void modify() {
        given(this.regionRepository.findById(anyLong())).willReturn(Mono.just(mock(Region.class)));

        given(this.regionRepository.save(any(Region.class))).willReturn(Mono.just(mock(Region.class)));

        StepVerifier.create(regionService.modify(anyLong(), dto)).expectNextCount(1).verifyComplete();
    }

    @Test
    void remove() {
        given(this.regionRepository.deleteById(anyLong())).willReturn(Mono.empty());

        StepVerifier.create(regionService.remove(anyLong())).verifyComplete();
    }

}