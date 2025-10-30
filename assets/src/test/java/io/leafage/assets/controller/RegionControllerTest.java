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

import io.leafage.assets.dto.RegionDTO;
import io.leafage.assets.service.RegionService;
import io.leafage.assets.vo.RegionVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

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

    private RegionVO vo;
    private RegionDTO dto;

    @BeforeEach
    void setUp() {
        vo = new RegionVO();
        vo.setId(1L);
        vo.setName("test");
        vo.setAreaCode("023333");
        vo.setPostalCode(232);
        vo.setDescription("region");

        dto = new RegionDTO();
        dto.setName("test");
        dto.setAreaCode("023333");
        dto.setPostalCode(232);
        dto.setDescription("region");
    }

    @Test
    void retrieve() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<RegionVO> voPage = new PageImpl<>(List.of(vo), pageable, 1L);
        given(this.regionService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willReturn(Mono.just(voPage));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/regions")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                        .queryParam("filters", "")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RegionVO.class);
    }

    @Test
    void retrieve_error() {
        given(this.regionService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willThrow(new RuntimeException());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/regions")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                        .queryParam("filters", "name:like:a")
                        .build())
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void fetch() {
        given(this.regionService.fetch(anyLong())).willReturn(Mono.just(vo));

        webTestClient.get().uri("/regions/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void fetch_error() {
        given(this.regionService.fetch(anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/regions/{id}", 1L)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void subset() {
        given(this.regionService.subset(anyLong())).willReturn(Flux.just(vo));

        webTestClient.get().uri("/regions/{id}/subset", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RegionVO.class);
    }

    @Test
    void subordinates_error() {
        given(this.regionService.subset(anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/regions/{id}/subset", 1L)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void create() {
        given(this.regionService.create(any(RegionDTO.class))).willReturn(Mono.just(vo));

        webTestClient.mutateWith(csrf()).post().uri("/regions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void create_error() {
        given(this.regionService.create(any(RegionDTO.class))).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).post().uri("/regions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void modify() {
        given(this.regionService.modify(anyLong(), any(RegionDTO.class))).willReturn(Mono.just(vo));

        webTestClient.mutateWith(csrf()).put().uri("/regions/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void modify_error() {
        given(this.regionService.modify(anyLong(), any(RegionDTO.class))).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).put().uri("/regions/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void remove() {
        given(this.regionService.remove(anyLong())).willReturn(Mono.empty());

        webTestClient.mutateWith(csrf()).delete().uri("/regions/{id}", 1)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void remove_error() {
        given(this.regionService.remove(anyLong())).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).delete().uri("/regions/{id}", 1)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}