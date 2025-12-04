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
import top.leafage.hypervisor.domain.vo.OperationLogVO;
import top.leafage.hypervisor.service.OperationLogService;

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
@WebFluxTest(OperationLogController.class)
class OperationLogControllerTest {

    @MockitoBean
    private OperationLogService operationLogService;

    @Autowired
    private WebTestClient webTestClient;

    private OperationLogVO vo;

    @BeforeEach
    void setUp() throws UnknownHostException {
        vo = new OperationLogVO(1L, "test", "create", "filters=test", "test", "127.0.0.1", "test", "test", 200);
    }

    @Test
    void retrieve() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<@NonNull OperationLogVO> page = new PageImpl<>(List.of(vo), pageable, 1L);
        given(this.operationLogService.retrieve(anyInt(), anyInt(), anyString(), anyBoolean(), anyString())).willReturn(Mono.just(page));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/operation-logs")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .queryParam("descending", true)
                        .queryParam("filters", "")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(OperationLogVO.class);
    }

    @Test
    void retrieve_error() {
        given(this.operationLogService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willThrow(new RuntimeException());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/operation-logs")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                        .queryParam("filters", "operation:like:test")
                        .build())
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void fetch() {
        given(this.operationLogService.fetch(anyLong())).willReturn(Mono.just(vo));

        webTestClient.get().uri("/operation-logs/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.operation").isEqualTo("test");
    }

    @Test
    void fetch_error() {
        given(this.operationLogService.fetch(anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/operation-logs/{id}", 1L)
                .exchange()
                .expectStatus().is5xxServerError();
    }

}