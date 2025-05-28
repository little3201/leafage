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

import io.leafage.hypervisor.dto.UserDTO;
import io.leafage.hypervisor.service.UserService;
import io.leafage.hypervisor.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

/**
 * user接口测试类
 *
 * @author wq li
 */
@WithMockUser
@ExtendWith(SpringExtension.class)
@WebFluxTest(UserController.class)
class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private WebTestClient webTestClient;

    private UserDTO userDTO;
    private UserVO userVO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setUsername("test");
        userDTO.setAvatar("avatar.jpg");
        userDTO.setGivenName("john");
        userDTO.setFamilyName("steven");
        userDTO.setAccountExpiresAt(Instant.now());
        userDTO.setCredentialsExpiresAt(Instant.now());

        userVO = new UserVO(1L, true, Instant.now());
        userVO.setUsername("test");
        userVO.setAccountExpiresAt(Instant.now());
        userVO.setGivenName("john");
        userVO.setMiddleName("steven");
        userVO.setFamilyName("steven");
    }

    @Test
    void fetch() {
        given(this.userService.fetch(Mockito.anyLong())).willReturn(Mono.just(userVO));

        webTestClient.get().uri("/users/{id}", 1L).exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.username").isEqualTo("test");
    }

    @Test
    void fetch_error() {
        given(this.userService.fetch(Mockito.anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/users/{id}", 1L).exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void exists() {
        given(this.userService.exists(Mockito.anyString(), Mockito.anyLong())).willReturn(Mono.just(Boolean.TRUE));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/users/exists")
                        .queryParam("username", "test")
                        .queryParam("id", 1L)
                        .build())
                .exchange().expectStatus().isOk();
    }

    @Test
    void exist_error() {
        given(this.userService.exists(Mockito.anyString(), Mockito.anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/users/exists")
                        .queryParam("username", "test")
                        .queryParam("id", 1L)
                        .build())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void created() {
        given(this.userService.create(Mockito.any(UserDTO.class))).willReturn(Mono.just(userVO));

        webTestClient.mutateWith(csrf()).post().uri("/users").bodyValue(userDTO).exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.username").isEqualTo("test");
    }

    @Test
    void modify() {
        given(this.userService.modify(Mockito.anyLong(), Mockito.any(UserDTO.class))).willReturn(Mono.just(userVO));

        webTestClient.mutateWith(csrf()).put().uri("/users/{id}", 1L).bodyValue(userDTO).exchange()
                .expectStatus().isAccepted()
                .expectBody().jsonPath("$.username").isEqualTo("test");
    }

    @Test
    void modify_error() {
        given(this.userService.modify(Mockito.anyLong(), Mockito.any(UserDTO.class))).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).put().uri("/users/{id}", 1L).bodyValue(userDTO).exchange()
                .expectStatus().isNotModified();
    }

    @Test
    void remove() {
        given(this.userService.remove(Mockito.anyLong())).willReturn(Mono.empty());

        webTestClient.mutateWith(csrf()).delete().uri("/users/{id}", 1L).exchange()
                .expectStatus().isOk();
    }

    @Test
    void remove_error() {
        given(this.userService.remove(Mockito.anyLong())).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).delete().uri("/users/{id}", 1L).exchange()
                .expectStatus().is4xxClientError();
    }

}