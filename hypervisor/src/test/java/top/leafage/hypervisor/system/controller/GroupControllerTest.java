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
import top.leafage.hypervisor.system.controller.GroupController;
import top.leafage.hypervisor.system.domain.GroupMembers;
import top.leafage.hypervisor.system.domain.GroupPrivileges;
import top.leafage.hypervisor.system.domain.dto.GroupDTO;
import top.leafage.hypervisor.system.domain.vo.GroupVO;
import top.leafage.hypervisor.system.domain.vo.UserVO;
import top.leafage.hypervisor.system.service.GroupMembersService;
import top.leafage.hypervisor.system.service.GroupPrivilegesService;
import top.leafage.hypervisor.system.service.GroupService;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

/**
 * group接口测试类
 *
 * @author wq li
 */
@WithMockUser
@WebFluxTest(GroupController.class)
class GroupControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private GroupMembersService groupMembersService;

    @MockitoBean
    private GroupPrivilegesService groupPrivilegesService;

    @MockitoBean
    private GroupService groupService;

    private GroupDTO dto;
    private GroupVO vo;
    private GroupMembers groupMembers;

    @BeforeEach
    void setUp() {
        vo = new GroupVO(1L, "test", "description", true);

        dto = new GroupDTO();
        dto.setName("test");
        dto.setDescription("group");

        groupMembers = new GroupMembers();
        groupMembers.setGroupId(1L);
        groupMembers.setUsername("test");
    }

    @Test
    void retrieve() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<@NonNull GroupVO> voPage = new PageImpl<>(List.of(vo), pageable, 1L);
        given(this.groupService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willReturn(Mono.just(voPage));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/groups")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .queryParam("descending", true)
                        .queryParam("filters", "")
                        .build()).exchange()
                .expectStatus().isOk().expectBodyList(GroupVO.class);
    }

    @Test
    void retrieve_error() {
        given(this.groupService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willThrow(new RuntimeException());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/groups")
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
    void fetch() {
        given(this.groupService.fetch(anyLong())).willReturn(Mono.just(vo));

        webTestClient.get().uri("/groups/{id}", 1L).exchange()
                .expectStatus().isOk().expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void fetch_error() {
        given(this.groupService.fetch(anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/groups/{id}", 1L).exchange().expectStatus().is5xxServerError();
    }

    @Test
    void create() {
        given(this.groupService.create(any(GroupDTO.class))).willReturn(Mono.just(vo));

        webTestClient.mutateWith(csrf()).post().uri("/groups").bodyValue(dto).exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void create_error() {
        given(this.groupService.create(any(GroupDTO.class))).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).post().uri("/groups").bodyValue(dto).exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void modify() {
        given(this.groupService.modify(anyLong(), any(GroupDTO.class))).willReturn(Mono.just(vo));

        webTestClient.mutateWith(csrf()).put().uri("/groups/{id}", 1L).bodyValue(dto).exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void modify_error() {
        given(this.groupService.modify(anyLong(), any(GroupDTO.class))).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).put().uri("/groups/{id}", 1L).bodyValue(dto).exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void remove() {
        given(this.groupService.remove(anyLong())).willReturn(Mono.empty());

        webTestClient.mutateWith(csrf()).delete().uri("/groups/{id}", 1L).exchange()
                .expectStatus().isOk();
    }

    @Test
    void remove_error() {
        given(this.groupService.remove(anyLong())).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).delete().uri("/groups/{id}", 1L).exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void members() {
        given(this.groupMembersService.members(anyLong())).willReturn(Flux.just(groupMembers));

        webTestClient.get().uri("/groups/{id}/members", 1L).exchange()
                .expectStatus().isOk()
                .expectBodyList(GroupMembers.class);
    }

    @Test
    void members_error() {
        given(this.groupMembersService.members(anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/groups/{id}/members", 1L).exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void relation() {
        given(this.groupPrivilegesService.relation(anyLong(), anyLong(), anySet()))
                .willReturn(Mono.just(mock(GroupPrivileges.class)));

        webTestClient.mutateWith(csrf()).patch().uri("/groups/{id}/privileges/{privilegeId}", 1L, 2L)
                .bodyValue(Set.of("test"))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void importFromFile() {
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[1]);
        given(this.groupService.createAll(anyIterable())).willReturn(Flux.just(vo));

        webTestClient.mutateWith(csrf()).post().uri("/groups/import")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", file.getResource()))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserVO.class);
    }
}