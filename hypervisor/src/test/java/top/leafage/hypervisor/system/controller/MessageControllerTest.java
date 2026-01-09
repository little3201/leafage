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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.system.controller.MessageController;
import top.leafage.hypervisor.system.domain.dto.MessageDTO;
import top.leafage.hypervisor.system.domain.vo.MessageVO;
import top.leafage.hypervisor.system.service.MessageService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

/**
 * message controller test
 *
 * @author wq li
 **/
@WithMockUser
@WebFluxTest(MessageController.class)
class MessageControllerTest {

    @MockitoBean
    private MessageService messageService;

    @Autowired
    private WebTestClient webTestClient;

    private MessageVO vo;
    private MessageDTO dto;

    @BeforeEach
    void setUp() {
        vo = new MessageVO(1L, "test", "test", "admin", false);

        dto = new MessageDTO();
        dto.setTitle("标题");
        dto.setBody("这个是正文内容");
        dto.setReceiver("test");
    }

    @Test
    void retrieve() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<@NonNull MessageVO> page = new PageImpl<>(List.of(vo), pageable, 1L);
        given(this.messageService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willReturn(Mono.just(page));

        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser())
                .get().uri(uriBuilder -> uriBuilder.path("/messages")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .queryParam("descending", true)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessageVO.class);
    }

    @Test
    void retrieve_error() {
        given(this.messageService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willThrow(new RuntimeException());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/messages")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .queryParam("receiver", "test")
                        .build())
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void fetch() {
        given(this.messageService.fetch(anyLong())).willReturn(Mono.just(vo));

        webTestClient.get().uri("/messages/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.title").isEqualTo("标题");
    }

    @Test
    void fetch_error() {
        given(this.messageService.fetch(anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/messages/{id}", 1L).exchange().expectStatus().is5xxServerError();
    }

    @Test
    void create() {
        given(this.messageService.create(any(MessageDTO.class))).willReturn(Mono.just(vo));

        webTestClient.mutateWith(csrf()).post().uri("/messages").bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.title").isEqualTo("标题");
    }

    @Test
    void create_error() {
        given(this.messageService.create(any(MessageDTO.class))).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).post().uri("/messages").bodyValue(dto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void remove() {
        given(this.messageService.remove(anyLong())).willReturn(Mono.empty());

        webTestClient.mutateWith(csrf()).delete().uri("/messages/{id}", 1L)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void remove_error() {
        given(this.messageService.remove(anyLong())).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).delete().uri("/messages/{id}", 1L)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}