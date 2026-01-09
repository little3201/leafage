/*
 * Copyright (c) 2026.  little3201.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.leafage.hypervisor.system.controller;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.system.controller.DictionaryController;
import top.leafage.hypervisor.system.domain.dto.DictionaryDTO;
import top.leafage.hypervisor.system.domain.vo.DictionaryVO;
import top.leafage.hypervisor.system.domain.vo.UserVO;
import top.leafage.hypervisor.system.service.DictionaryService;

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
        dto.setName("test");
        dto.setDescription("描述");

        vo = new DictionaryVO(1L, "test", null, "description", true);
    }

    @Test
    void retrieve() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<@NonNull DictionaryVO> voPage = new PageImpl<>(List.of(vo), pageable, 1L);
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
                .expectBodyList(DictionaryVO.class).hasSize(1).contains(vo);
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
                        .queryParam("filters", "name:like:test")
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

    @Test
    void modify() {
        given(this.dictionaryService.modify(anyLong(), any(DictionaryDTO.class))).willReturn(Mono.just(vo));

        webTestClient.mutateWith(csrf()).put().uri("/dictionaries/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void modify_error() {
        given(this.dictionaryService.modify(anyLong(), any(DictionaryDTO.class))).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).put().uri("/dictionaries/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void remove() {
        given(this.dictionaryService.remove(anyLong())).willReturn(Mono.empty());

        webTestClient.mutateWith(csrf()).delete().uri("/dictionaries/{id}", 1L)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void remove_error() {
        given(this.dictionaryService.remove(anyLong())).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).delete().uri("/dictionaries/{id}", 1L)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void importFromFile() {
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[1]);
        given(this.dictionaryService.createAll(anyIterable())).willReturn(Flux.just(vo));

        webTestClient.mutateWith(csrf()).post().uri("/dictionaries/import")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", file.getResource()))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserVO.class);
    }
}