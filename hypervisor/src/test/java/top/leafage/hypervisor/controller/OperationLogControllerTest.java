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

import top.leafage.hypervisor.service.OperationLogService;
import top.leafage.hypervisor.domain.vo.OperationLogVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
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
@ExtendWith(SpringExtension.class)
@WebFluxTest(OperationLogController.class)
class OperationLogControllerTest {

    @MockitoBean
    private OperationLogService operationLogService;

    @Autowired
    private WebTestClient webTestClient;

    private OperationLogVO vo;

    @BeforeEach
    void setUp() throws UnknownHostException {
        vo = new OperationLogVO();
        vo.setId(1L);
        vo.setIp(InetAddress.getByName("12.1.2.1"));
        vo.setLocation("某国某城市");
        vo.setBody("更新个人资料");
        vo.setOperation("test");
    }

    @Test
    void retrieve() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<OperationLogVO> page = new PageImpl<>(List.of(vo), pageable, 1L);
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