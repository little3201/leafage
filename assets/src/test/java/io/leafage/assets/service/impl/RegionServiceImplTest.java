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
 * region service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class RegionServiceImplTest {

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private RegionServiceImpl regionService;

    private RegionDTO dto;

    @BeforeEach
    void setUp() {
        dto = new RegionDTO();
        dto.setName("西安市");
        dto.setAreaCode("029");
        dto.setPostalCode(710000);
        dto.setSuperiorId(1L);
    }

    @Test
    void retrieve() {
        given(this.regionRepository.findAllBySuperiorIdIsNull(Mockito.any(PageRequest.class))).willReturn(Flux.just(Mockito.mock(Region.class)));

        given(this.regionRepository.countBySuperiorIdIsNullAndEnabledTrue()).willReturn(Mono.just(Mockito.anyLong()));

        StepVerifier.create(regionService.retrieve(0, 2, "id", true, "name:like:a")).expectNextCount(1).verifyComplete();
    }

    @Test
    void fetch() {
        given(this.regionRepository.findById(Mockito.anyLong())).willReturn(Mono.just(Mockito.mock(Region.class)));

        StepVerifier.create(regionService.fetch(Mockito.anyLong())).expectNextCount(1).verifyComplete();
    }

    @Test
    void subset() {
        given(this.regionRepository.findBySuperiorId(Mockito.anyLong())).willReturn(Flux.just(Mockito.mock(Region.class)));

        StepVerifier.create(regionService.subset(Mockito.anyLong())).expectNextCount(1).verifyComplete();
    }

    @Test
    void exists() {
        given(this.regionRepository.existsByNameAndIdNot(Mockito.anyString(), Mockito.anyLong())).willReturn(Mono.just(Boolean.TRUE));

        StepVerifier.create(regionService.exists("test", 1L)).expectNext(Boolean.TRUE).verifyComplete();
    }

    @Test
    void exists_id_null() {
        given(this.regionRepository.existsByName(Mockito.anyString())).willReturn(Mono.just(Boolean.TRUE));

        StepVerifier.create(regionService.exists("test", null)).expectNext(Boolean.TRUE).verifyComplete();
    }

    @Test
    void create() {
        given(this.regionRepository.save(Mockito.any(Region.class))).willReturn(Mono.just(Mockito.mock(Region.class)));

        StepVerifier.create(regionService.create(Mockito.mock(RegionDTO.class))).expectNextCount(1).verifyComplete();
    }

    @Test
    void modify() {
        given(this.regionRepository.findById(Mockito.anyLong())).willReturn(Mono.just(Mockito.mock(Region.class)));

        given(this.regionRepository.save(Mockito.any(Region.class))).willReturn(Mono.just(Mockito.mock(Region.class)));

        StepVerifier.create(regionService.modify(Mockito.anyLong(), dto)).expectNextCount(1).verifyComplete();
    }

    @Test
    void remove() {
        given(this.regionRepository.deleteById(Mockito.anyLong())).willReturn(Mono.empty());

        StepVerifier.create(regionService.remove(Mockito.anyLong())).verifyComplete();
    }

}