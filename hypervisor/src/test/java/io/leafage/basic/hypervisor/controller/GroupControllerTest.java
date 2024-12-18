/*
 *  Copyright 2018-2024 little3201.
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

package io.leafage.basic.hypervisor.controller;

import io.leafage.basic.hypervisor.domain.GroupMembers;
import io.leafage.basic.hypervisor.dto.GroupDTO;
import io.leafage.basic.hypervisor.service.GroupMembersService;
import io.leafage.basic.hypervisor.service.GroupService;
import io.leafage.basic.hypervisor.vo.GroupVO;
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
 * group接口测试类
 *
 * @author wq li
 */
@WithMockUser
@ExtendWith(SpringExtension.class)
@WebFluxTest(GroupController.class)
class GroupControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private GroupMembersService groupMembersService;

    @MockitoBean
    private GroupService groupService;

    private GroupDTO groupDTO;
    private GroupVO groupVO;
    private GroupMembers groupMembers;

    @BeforeEach
    void setUp() {
        groupVO = new GroupVO(1L, true, Instant.now());
        groupVO.setName("test");
        groupVO.setPrincipal("test");

        groupDTO = new GroupDTO();
        groupDTO.setName("test");
        groupDTO.setPrincipal("Test");
        groupDTO.setDescription("group");

        groupMembers = new GroupMembers();
        groupMembers.setGroupId(1L);
        groupMembers.setUsername("test");
    }

    @Test
    void retrieve() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<GroupVO> voPage = new PageImpl<>(List.of(groupVO), pageable, 1L);
        given(this.groupService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean())).willReturn(Mono.just(voPage));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/groups").queryParam("page", 0)
                        .queryParam("size", 2).build()).exchange()
                .expectStatus().isOk().expectBodyList(GroupVO.class);
    }

    @Test
    void retrieve_error() {
        given(this.groupService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean())).willThrow(new RuntimeException());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/groups")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .build())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void fetch() {
        given(this.groupService.fetch(Mockito.anyLong())).willReturn(Mono.just(groupVO));

        webTestClient.get().uri("/groups/{id}", 1L).exchange()
                .expectStatus().isOk().expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void fetch_error() {
        given(this.groupService.fetch(Mockito.anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/groups/{id}", 1L).exchange().expectStatus().isNoContent();
    }

    @Test
    void exists() {
        given(this.groupService.exists(Mockito.anyString(), Mockito.anyLong())).willReturn(Mono.just(Boolean.TRUE));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/groups/exists")
                        .queryParam("name", "test")
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void exist_error() {
        given(this.groupService.exists(Mockito.anyString(), Mockito.anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/groups/exists")
                        .queryParam("name", "test")
                        .queryParam("id", 1L)
                        .build())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void create() {
        given(this.groupService.create(Mockito.any(GroupDTO.class))).willReturn(Mono.just(groupVO));

        webTestClient.mutateWith(csrf()).post().uri("/groups").bodyValue(groupDTO).exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void create_error() {
        given(this.groupService.create(Mockito.any(GroupDTO.class))).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).post().uri("/groups").bodyValue(groupDTO).exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void modify() {
        given(this.groupService.modify(Mockito.anyLong(), Mockito.any(GroupDTO.class))).willReturn(Mono.just(groupVO));

        webTestClient.mutateWith(csrf()).put().uri("/groups/{id}", 1L).bodyValue(groupDTO).exchange()
                .expectStatus().isAccepted()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void modify_error() {
        given(this.groupService.modify(Mockito.anyLong(), Mockito.any(GroupDTO.class))).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).put().uri("/groups/{id}", 1L).bodyValue(groupDTO).exchange()
                .expectStatus().isNotModified();
    }

    @Test
    void remove() {
        given(this.groupService.remove(Mockito.anyLong())).willReturn(Mono.empty());

        webTestClient.mutateWith(csrf()).delete().uri("/groups/{id}", 1L).exchange()
                .expectStatus().isOk();
    }

    @Test
    void remove_error() {
        given(this.groupService.remove(Mockito.anyLong())).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).delete().uri("/groups/{id}", 1L).exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void members() {
        given(this.groupMembersService.members(Mockito.anyLong())).willReturn(Mono.just(List.of(groupMembers)));

        webTestClient.get().uri("/groups/{id}/members", 1L).exchange()
                .expectStatus().isOk()
                .expectBodyList(GroupMembers.class);
    }

    @Test
    void members_error() {
        given(this.groupMembersService.members(Mockito.anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/groups/{id}/members", 1L).exchange()
                .expectStatus().isNoContent();
    }
}