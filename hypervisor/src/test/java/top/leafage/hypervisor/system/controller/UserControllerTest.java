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
import top.leafage.hypervisor.system.controller.UserController;
import top.leafage.hypervisor.system.domain.dto.UserDTO;
import top.leafage.hypervisor.system.domain.vo.PrivilegeVO;
import top.leafage.hypervisor.system.domain.vo.UserVO;
import top.leafage.hypervisor.system.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

/**
 * user接口测试类
 *
 * @author wq li
 */
@WithMockUser
@WebFluxTest(UserController.class)
class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private WebTestClient webTestClient;

    private UserDTO dto;
    private UserVO vo;

    @BeforeEach
    void setUp() {
        dto = new UserDTO();
        dto.setUsername("test");
        dto.setFullName("Tom");
        dto.setEmail("test@example.com");

        vo = new UserVO(1L, "test", "test", "test@example.com", "ACTIVE", true);
    }


    @Test
    void retrieve() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<@NonNull UserVO> voPage = new PageImpl<>(List.of(vo), pageable, 1L);
        given(this.userService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willReturn(Mono.just(voPage));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/users")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .queryParam("descending", true)
                        .queryParam("filters", "")
                        .build()).exchange()
                .expectStatus().isOk()
                .expectBodyList(PrivilegeVO.class);
    }

    @Test
    void retrieve_error() {
        given(this.userService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willThrow(new RuntimeException());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/users")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                        .queryParam("filters", "username:like:test")
                        .build())
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void fetch() {
        given(this.userService.fetch(anyLong())).willReturn(Mono.just(vo));

        webTestClient.get().uri("/users/{id}", 1L).exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.username").isEqualTo("test");
    }

    @Test
    void fetch_error() {
        given(this.userService.fetch(anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/users/{id}", 1L).exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void enable() {
        given(this.userService.enable(anyLong())).willReturn(Mono.just(Boolean.TRUE));

        webTestClient.mutateWith(csrf()).patch().uri("/users/{id}", 1L).exchange()
                .expectStatus().isOk();
    }

    @Test
    void unlock() {
        given(this.userService.unlock(anyLong())).willReturn(Mono.just(Boolean.TRUE));

        webTestClient.mutateWith(csrf()).patch().uri("/users/{id}/unlock", 1L).exchange()
                .expectStatus().isOk();
    }

    @Test
    void created() {
        given(this.userService.create(any(UserDTO.class))).willReturn(Mono.just(vo));

        webTestClient.mutateWith(csrf()).post().uri("/users").bodyValue(dto).exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.username").isEqualTo("test");
    }

    @Test
    void modify() {
        given(this.userService.modify(anyLong(), any(UserDTO.class))).willReturn(Mono.just(vo));

        webTestClient.mutateWith(csrf()).put().uri("/users/{id}", 1L).bodyValue(dto).exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.username").isEqualTo("test");
    }

    @Test
    void modify_error() {
        given(this.userService.modify(anyLong(), any(UserDTO.class))).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).put().uri("/users/{id}", 1L).bodyValue(dto).exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void remove() {
        given(this.userService.remove(anyLong())).willReturn(Mono.empty());

        webTestClient.mutateWith(csrf()).delete().uri("/users/{id}", 1L).exchange()
                .expectStatus().isOk();
    }

    @Test
    void remove_error() {
        given(this.userService.remove(anyLong())).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).delete().uri("/users/{id}", 1L).exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void importFromFile() {
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[1]);
        given(this.userService.createAll(anyIterable())).willReturn(Flux.just(vo));

        webTestClient.mutateWith(csrf()).post().uri("/users/import")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", file.getResource()))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserVO.class);
    }

}