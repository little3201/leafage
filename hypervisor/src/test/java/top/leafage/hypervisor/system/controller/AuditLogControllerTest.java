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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.system.controller.AuditLogController;
import top.leafage.hypervisor.system.domain.vo.AuditLogVO;
import top.leafage.hypervisor.system.service.AuditLogService;

import java.net.UnknownHostException;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

/**
 * audit log controller test
 *
 * @author wq li
 */
@WithMockUser
@WebFluxTest(AuditLogController.class)
class AuditLogControllerTest {

    @MockitoBean
    private AuditLogService auditLogService;

    @Autowired
    private WebTestClient webTestClient;

    private AuditLogVO vo;

    @BeforeEach
    void setUp() throws UnknownHostException {
        vo = new AuditLogVO(1L, "test", "test", "test", "test", "127.0.0.1", 200, 2132L);
    }

    @Test
    void retrieve() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<@NonNull AuditLogVO> page = new PageImpl<>(List.of(vo), pageable, 1L);
        given(this.auditLogService.retrieve(anyInt(), anyInt(), anyString(), anyBoolean(), anyString())).willReturn(Mono.just(page));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/audit-logs")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .queryParam("descending", true)
                        .queryParam("filters", "")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(1)
                .jsonPath("$.content[0].id").isEqualTo(vo.id())  // 根据 GroupVO 的字段调整
                // 其他分页字段断言
                .jsonPath("$.totalElements").isEqualTo(1)
                .jsonPath("$.totalPages").isEqualTo(1)
                .jsonPath("$.number").isEqualTo(0)
                .jsonPath("$.size").isEqualTo(2);
    }

    @Test
    void retrieve_error() {
        given(this.auditLogService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willThrow(new RuntimeException());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/audit-logs")
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
        given(this.auditLogService.fetch(anyLong())).willReturn(Mono.just(vo));

        webTestClient.get().uri("/audit-logs/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.operation").isEqualTo("test");
    }

    @Test
    void fetch_error() {
        given(this.auditLogService.fetch(anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/audit-logs/{id}", 1L)
                .exchange()
                .expectStatus().is5xxServerError();
    }

}