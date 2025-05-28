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

import io.leafage.assets.dto.CategoryDTO;
import io.leafage.assets.service.CategoryService;
import io.leafage.assets.vo.CategoryVO;
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

import java.time.Instant;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

/**
 * category controller test
 *
 * @author wq li
 */
@WithMockUser
@ExtendWith(SpringExtension.class)
@WebFluxTest(CategoryController.class)
class CategoryControllerTest {

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private WebTestClient webTestClient;

    private CategoryDTO categoryDTO;
    private CategoryVO categoryVO;

    @BeforeEach
    void setUp() {
        // 构造请求对象
        categoryDTO = new CategoryDTO();
        categoryDTO.setName("test");
        categoryDTO.setDescription("描述信息");

        categoryVO = new CategoryVO(1L, true, Instant.now());
        categoryVO.setName(categoryDTO.getName());
    }

    @Test
    void retrieve() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<CategoryVO> page = new PageImpl<>(List.of(categoryVO), pageable, 1L);
        given(this.categoryService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean())).willReturn(Mono.just(page));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/categories")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CategoryVO.class);
    }

    @Test
    void retrieve_error() {
        given(this.categoryService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean())).willThrow(new RuntimeException());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/categories")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .build())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void fetch() {
        given(this.categoryService.fetch(Mockito.anyLong())).willReturn(Mono.just(categoryVO));

        webTestClient.get().uri("/categories/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void fetch_error() {
        given(this.categoryService.fetch(Mockito.anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/categories/{id}", 1)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void exists() {
        given(this.categoryService.exists(Mockito.anyString(), Mockito.anyLong())).willReturn(Mono.just(Boolean.TRUE));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/categories/exists")
                        .queryParam("name", "test")
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void exist_error() {
        given(this.categoryService.exists(Mockito.anyString(), Mockito.anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/categories/exists")
                        .queryParam("name", "test")
                        .queryParam("id", 1L)
                        .build())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void create() {
        given(this.categoryService.create(Mockito.any(CategoryDTO.class))).willReturn(Mono.just(categoryVO));

        webTestClient.mutateWith(csrf()).post().uri("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(categoryDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void create_error() {
        given(this.categoryService.create(Mockito.any(CategoryDTO.class))).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).post().uri("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(categoryDTO)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void modify() {
        given(this.categoryService.modify(Mockito.anyLong(), Mockito.any(CategoryDTO.class))).willReturn(Mono.just(categoryVO));

        webTestClient.mutateWith(csrf()).put().uri("/categories/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(categoryDTO)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void modify_error() {
        given(this.categoryService.modify(Mockito.anyLong(), Mockito.any(CategoryDTO.class))).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).put().uri("/categories/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(categoryDTO)
                .exchange()
                .expectStatus().isNotModified();
    }

    @Test
    void remove() {
        given(this.categoryService.remove(Mockito.anyLong())).willReturn(Mono.empty());

        webTestClient.mutateWith(csrf()).delete().uri("/categories/{id}", 1)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void remove_error() {
        given(this.categoryService.remove(Mockito.anyLong())).willThrow(new RuntimeException());

        webTestClient.delete().uri("/categories/{id}", 1)
                .exchange()
                .expectStatus().is4xxClientError();
    }
}