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

import io.leafage.assets.dto.TagDTO;
import io.leafage.assets.service.TagService;
import io.leafage.assets.vo.TagVO;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

/**
 * tag controller test
 *
 * @author wq li
 */
@WithMockUser
@ExtendWith(SpringExtension.class)
@WebFluxTest(TagController.class)
class TagControllerTest {

    @MockitoBean
    private TagService tagService;

    @Autowired
    private WebTestClient webTestClient;

    private TagDTO dto;
    private TagVO vo;

    @BeforeEach
    void setUp() {
        // 构造请求对象
        dto = new TagDTO();
        dto.setName("test");

        vo = new TagVO();
        vo.setId(1L);
        vo.setName(dto.getName());
    }

    @Test
    void retrieve() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<TagVO> page = new PageImpl<>(List.of(vo), pageable, 1L);
        given(this.tagService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyBoolean(), Mockito.anyString())).willReturn(Mono.just(page));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/tags")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TagVO.class);
    }

    @Test
    void retrieve_error() {
        given(this.tagService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyBoolean(), Mockito.anyString())).willThrow(new RuntimeException());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/tags")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                        .queryParam("filters", "name:like:a")
                        .build())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void fetch() {
        given(this.tagService.fetch(Mockito.anyLong())).willReturn(Mono.just(vo));

        webTestClient.get().uri("/tags/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void fetch_error() {
        given(this.tagService.fetch(Mockito.anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/tags/{id}", 1)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void exists() {
        given(this.tagService.exists(Mockito.anyString(), Mockito.anyLong())).willReturn(Mono.just(Boolean.TRUE));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/tags/exists")
                        .queryParam("name", "test")
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void exist_error() {
        given(this.tagService.exists(Mockito.anyString(), Mockito.anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/tags/exists")
                        .queryParam("name", "test")
                        .queryParam("id", 1L)
                        .build())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void create() {
        given(this.tagService.create(Mockito.any(TagDTO.class))).willReturn(Mono.just(vo));

        webTestClient.mutateWith(csrf()).post().uri("/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void create_error() {
        given(this.tagService.create(Mockito.any(TagDTO.class))).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).post().uri("/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void modify() {
        given(this.tagService.modify(Mockito.anyLong(), Mockito.any(TagDTO.class))).willReturn(Mono.just(vo));

        webTestClient.mutateWith(csrf()).put().uri("/tags/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void modify_error() {
        given(this.tagService.modify(Mockito.anyLong(), Mockito.any(TagDTO.class))).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).put().uri("/tags/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isNotModified();
    }

    @Test
    void remove() {
        given(this.tagService.remove(Mockito.anyLong())).willReturn(Mono.empty());

        webTestClient.mutateWith(csrf()).delete().uri("/tags/{id}", 1)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void remove_error() {
        given(this.tagService.remove(Mockito.anyLong())).willThrow(new RuntimeException());

        webTestClient.delete().uri("/tags/{id}", 1)
                .exchange()
                .expectStatus().is4xxClientError();
    }
}