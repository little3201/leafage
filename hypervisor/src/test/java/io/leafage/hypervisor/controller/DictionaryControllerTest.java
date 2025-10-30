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

package io.leafage.hypervisor.controller;

import io.leafage.hypervisor.dto.DictionaryDTO;
import io.leafage.hypervisor.service.DictionaryService;
import io.leafage.hypervisor.vo.DictionaryVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

/**
 * dictionary api test
 *
 * @author wq li
 **/
@WithMockUser
@ExtendWith(SpringExtension.class)
@WebFluxTest(DictionaryController.class)
class DictionaryControllerTest {

    @MockitoBean
    private DictionaryService dictionaryService;

    @Autowired
    private WebTestClient webTestClient;

    private DictionaryDTO dto;
    private DictionaryVO vo;

    @BeforeEach
    void setUp() {
        dto = new DictionaryDTO();
        dto.setName("Gender");
        dto.setDescription("描述");

        vo = new DictionaryVO();
        vo.setId(1L);
        vo.setName("test");
        vo.setDescription("性别-男");
    }

    @Test
    void retrieve() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<DictionaryVO> voPage = new PageImpl<>(List.of(vo), pageable, 1L);
        given(this.dictionaryService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willReturn(Mono.just(voPage));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/dictionaries")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .queryParam("descending", true)
                        .queryParam("filters", "")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DictionaryVO.class);
    }

    @Test
    void retrieve_error() {
        given(this.dictionaryService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willThrow(new RuntimeException());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/dictionaries")
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
        given(this.dictionaryService.fetch(anyLong())).willReturn(Mono.just(vo));

        webTestClient.get().uri("/dictionaries/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void fetch_error() {
        given(this.dictionaryService.fetch(anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/dictionaries/{id}", 1L)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void subset() {
        given(this.dictionaryService.subset(anyLong())).willReturn(Flux.just(vo));

        webTestClient.get().uri("/dictionaries/{id}/subset", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DictionaryVO.class);
    }

    @Test
    void subset_error() {
        given(this.dictionaryService.subset(anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/dictionaries/{id}/subset", 1L)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void create() {
        given(this.dictionaryService.create(any(DictionaryDTO.class))).willReturn(Mono.just(vo));

        webTestClient.mutateWith(csrf()).post().uri("/dictionaries").bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void create_error() {
        given(this.dictionaryService.create(any(DictionaryDTO.class))).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).post().uri("/dictionaries").bodyValue(dto)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}