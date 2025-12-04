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

import top.leafage.hypervisor.domain.dto.PrivilegeDTO;
import top.leafage.hypervisor.service.PrivilegeService;
import top.leafage.hypervisor.domain.vo.PrivilegeVO;
import top.leafage.hypervisor.domain.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.TreeNode;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

/**
 * privilege 接口测试类
 *
 * @author wq li
 */
@WithMockUser
@ExtendWith(SpringExtension.class)
@WebFluxTest(PrivilegeController.class)
class PrivilegeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private PrivilegeService privilegeService;

    private PrivilegeDTO dto;
    private PrivilegeVO vo;

    @BeforeEach
    void setUp() {
        dto = new PrivilegeDTO();
        dto.setName("test");
        dto.setIcon("add");
        dto.setSuperiorId(1L);
        dto.setDescription("privilege");

        vo = new PrivilegeVO();
        vo.setId(1L);
        vo.setName("test");
        vo.setIcon("add");
        vo.setPath("/test");
    }

    @Test
    void retrieve() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<PrivilegeVO> voPage = new PageImpl<>(List.of(vo), pageable, 1L);
        given(this.privilegeService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willReturn(Mono.just(voPage));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/privileges")
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
        given(this.privilegeService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willThrow(new RuntimeException());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/privileges")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                        .queryParam("filters", "name:like:test")
                        .build())
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void tree() {
        TreeNode<Long> treeNode = TreeNode.withId(1L).name("test").build();
        given(this.privilegeService.tree(anyString())).willReturn(Mono.just(List.of(treeNode)));

        webTestClient.get().uri("/privileges/tree")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TreeNode.class);
    }

    @Test
    void tree_error() {
        given(this.privilegeService.tree(anyString())).willThrow(new RuntimeException());

        webTestClient.get().uri("/privileges/tree")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void fetch() {
        given(this.privilegeService.fetch(anyLong())).willReturn(Mono.just(vo));

        webTestClient.get().uri("/privileges/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void fetch_error() {
        given(this.privilegeService.fetch(anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/privileges/{id}", 1L)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void subset() {
        given(this.privilegeService.subset(anyLong())).willReturn(Flux.just(vo));

        webTestClient.get().uri("/privileges/{id}/subset", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PrivilegeVO.class);
    }

    @Test
    void create() {
        given(this.privilegeService.exists(anyString(), isNull())).willReturn(Mono.just(false));
        given(this.privilegeService.create(any(PrivilegeDTO.class))).willReturn(Mono.just(vo));

        webTestClient.mutateWith(csrf()).post().uri("/privileges").bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void create_error() {
        given(this.privilegeService.exists(anyString(), isNull())).willReturn(Mono.just(false));
        given(this.privilegeService.create(any(PrivilegeDTO.class))).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).post().uri("/privileges").bodyValue(dto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void modify() {
        given(this.privilegeService.exists(anyString(), anyLong())).willReturn(Mono.just(false));
        given(this.privilegeService.modify(anyLong(), any(PrivilegeDTO.class))).willReturn(Mono.just(vo));

        webTestClient.mutateWith(csrf()).put().uri("/privileges/{id}", 1L).bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void modify_error() {
        given(this.privilegeService.exists(anyString(), anyLong())).willReturn(Mono.just(false));
        given(this.privilegeService.modify(anyLong(), any(PrivilegeDTO.class))).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).put().uri("/privileges/{id}", 1L).bodyValue(dto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void importFromFile() {
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[1]);
        given(this.privilegeService.createAll(anyIterable())).willReturn(Flux.just(vo));

        webTestClient.mutateWith(csrf()).post().uri("/privileges/import")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", file.getResource()))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserVO.class);
    }
}