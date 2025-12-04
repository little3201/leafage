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

package top.leafage.assets.controller;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import top.leafage.assets.domain.dto.PostDTO;
import top.leafage.assets.domain.vo.PostVO;
import top.leafage.assets.service.PostService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

/**
 * posts controller test
 *
 * @author wq li
 */
@WithMockUser
@WebFluxTest(PostController.class)
class PostControllerTest {

    @MockitoBean
    private PostService postService;

    @Autowired
    private WebTestClient webTestClient;

    private PostDTO dto;
    private PostVO vo;

    @BeforeEach
    void setUp() {
        dto = new PostDTO();
        dto.setTitle("test");
        dto.setBody("body");
        dto.setSummary("summary");
        dto.setTags(Set.of("Code"));

        vo = new PostVO(1L, "test", "summary", "body", Set.of("Code"), LocalDateTime.now());
    }

    @Test
    void retrieve() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<@NonNull PostVO> page = new PageImpl<>(List.of(vo), pageable, 1L);
        given(this.postService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString()))
                .willReturn(Mono.just(page));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/posts")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                        .queryParam("filters", "")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PostVO.class);
    }

    @Test
    void retrieve_error() {
        given(this.postService.retrieve(anyInt(), anyInt(), anyString(), anyBoolean(), anyString()))
                .willThrow(new RuntimeException());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/posts")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                        .queryParam("filters", "title:like:test")
                        .build())
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void fetch() {
        given(this.postService.fetch(anyLong())).willReturn(Mono.just(vo));

        webTestClient.get().uri("/posts/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.title").isEqualTo("test");
    }

    @Test
    void fetch_error() {
        given(this.postService.fetch(anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/posts/{id}", 1L)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void create() {
        given(this.postService.create(any(PostDTO.class))).willReturn(Mono.just(vo));

        webTestClient.mutateWith(csrf()).post().uri("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.title").isEqualTo("test");
    }

    @Test
    void create_error() {
        given(this.postService.create(any(PostDTO.class))).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).post().uri("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void modify() {
        given(this.postService.modify(anyLong(), any(PostDTO.class))).willReturn(Mono.just(vo));

        webTestClient.mutateWith(csrf()).put().uri("/posts/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.title").isEqualTo("test");
    }

    @Test
    void modify_error() {
        given(this.postService.modify(anyLong(), any(PostDTO.class))).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).put().uri("/posts/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void remove() {
        given(this.postService.remove(anyLong())).willReturn(Mono.empty());

        webTestClient.mutateWith(csrf()).delete().uri("/posts/{id}", 1L)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void remove_error() {
        given(this.postService.remove(anyLong())).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).delete().uri("/posts/{id}", 1L)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}