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

package io.leafage.assets.controller;

import io.leafage.assets.service.RegionService;
import io.leafage.assets.vo.RegionVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

import static org.mockito.BDDMockito.given;

/**
 * region api test
 *
 * @author wq li
 **/
@WithMockUser
@ExtendWith(SpringExtension.class)
@WebFluxTest(RegionController.class)
class RegionControllerTest {

    @MockitoBean
    private RegionService regionService;

    @Autowired
    private WebTestClient webTestClient;

    private RegionVO regionVO;

    @BeforeEach
    void setUp() {
        regionVO = new RegionVO(1L, true, Instant.now());
        regionVO.setName("test");
        regionVO.setAreaCode("023333");
        regionVO.setPostalCode(232);
        regionVO.setDescription("region");
    }

    @Test
    void retrieve() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<RegionVO> voPage = new PageImpl<>(List.of(regionVO), pageable, 1L);
        given(this.regionService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean())).willReturn(Mono.just(voPage));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/regions")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RegionVO.class);
    }

    @Test
    void retrieve_error() {
        given(this.regionService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean())).willThrow(new RuntimeException());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/regions")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .build())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void fetch() {
        given(this.regionService.fetch(Mockito.anyLong())).willReturn(Mono.just(regionVO));

        webTestClient.get().uri("/regions/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void fetch_error() {
        given(this.regionService.fetch(Mockito.anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/regions/{id}", 1L)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void subset() {
        given(this.regionService.subset(Mockito.anyLong())).willReturn(Flux.just(regionVO));

        webTestClient.get().uri("/regions/{id}/subset", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RegionVO.class);
    }

    @Test
    void subordinates_error() {
        given(this.regionService.subset(Mockito.anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/regions/{id}/subset", 1L)
                .exchange()
                .expectStatus().isNoContent();
    }

}