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

package top.leafage.hypervisor.controller;

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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.domain.vo.AccessLogVO;
import top.leafage.hypervisor.service.AccessLogService;

import java.net.UnknownHostException;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

/**
 * record controller test
 *
 * @author wq li
 */
@WithMockUser
@WebFluxTest(AccessLogController.class)
class AccessLogControllerTest {

    @MockitoBean
    private AccessLogService accessLogService;

    @Autowired
    private WebTestClient webTestClient;

    private AccessLogVO vo;

    @BeforeEach
    void setUp() throws UnknownHostException {
        vo = new AccessLogVO(1L, "test", "POST", "127.0.0.1", "", "", 200, 230L, "");
    }

    @Test
    void retrieve() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<@NonNull AccessLogVO> page = new PageImpl<>(List.of(vo), pageable, 1L);
        given(this.accessLogService.retrieve(anyInt(), anyInt(), anyString(), anyBoolean(), anyString())).willReturn(Mono.just(page));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/access-logs")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .queryParam("descending", true)
                        .queryParam("filters", "")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AccessLogVO.class);
    }

    @Test
    void retrieve_error() {
        given(this.accessLogService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willThrow(new RuntimeException());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/access-logs")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                        .queryParam("filters", "url:like:test")
                        .build())
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void fetch() {
        given(this.accessLogService.fetch(anyLong())).willReturn(Mono.just(vo));

        webTestClient.get().uri("/access-logs/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.params").isEqualTo("test");
    }

    @Test
    void fetch_error() {
        given(this.accessLogService.fetch(anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/access-logs/{id}", 1L)
                .exchange()
                .expectStatus().is5xxServerError();
    }

}