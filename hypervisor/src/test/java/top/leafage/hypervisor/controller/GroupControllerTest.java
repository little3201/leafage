/*
 * Copyright (c) 2024-2025.  little3201.
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

package top.leafage.hypervisor.controller;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;
import top.leafage.common.data.domain.TreeNode;
import top.leafage.hypervisor.domain.GroupPrivileges;
import top.leafage.hypervisor.domain.dto.GroupDTO;
import top.leafage.hypervisor.domain.vo.GroupVO;
import top.leafage.hypervisor.service.GroupMembersService;
import top.leafage.hypervisor.service.GroupPrivilegesService;
import top.leafage.hypervisor.service.GroupRolesService;
import top.leafage.hypervisor.service.GroupService;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/**
 * group controller test
 *
 * @author wq li
 **/
@WithMockUser
@WebMvcTest(GroupController.class)
class GroupControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private GroupService groupService;

    @MockitoBean
    private GroupPrivilegesService groupPrivilegesService;

    @MockitoBean
    private GroupMembersService groupMembersService;

    @MockitoBean
    private GroupRolesService groupRolesService;

    private GroupVO vo;

    private GroupDTO dto;

    @BeforeEach
    void setUp() {
        vo = new GroupVO(1L, "test", "description", true);

        dto = new GroupDTO();
        dto.setName("test");
        dto.setSuperiorId(1L);
        dto.setDescription("description");
    }

    @Test
    void retrieve() {
        Page<@NonNull GroupVO> voPage = new PageImpl<>(List.of(Mockito.mock(GroupVO.class)), mock(PageRequest.class), 2L);

        given(this.groupService.retrieve(anyInt(), anyInt(), eq("id"),
                anyBoolean(), anyString())).willReturn(voPage);

        assertThat(this.mvc.get().uri("/groups")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "false")
                .queryParam("filters", "")
        )
                .hasStatusOk()
                .body().isNotNull().hasSize(1);
    }

    @Test
    void retrieve_error() {
        given(this.groupService.retrieve(anyInt(), anyInt(), eq("id"), anyBoolean(),
                anyString())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/groups")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "true")
                .queryParam("filters", "")
        )
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void tree() {
        TreeNode<Long> treeNode = TreeNode.withId(1L).name("test").build();
        given(this.groupService.tree()).willReturn(Collections.singletonList(treeNode));

        assertThat(this.mvc.get().uri("/groups/tree"))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void fetch() {
        given(this.groupService.fetch(anyLong())).willReturn(Mockito.mock(GroupVO.class));

        assertThat(this.mvc.get().uri("/groups/{id}", anyLong()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void fetch_error() {
        given(this.groupService.fetch(anyLong())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/groups/{id}", anyLong()))
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void create() {
        given(this.groupService.create(any(GroupDTO.class))).willReturn(Mockito.mock(GroupVO.class));

        assertThat(this.mvc.post().uri("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader())
        )
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void create_error() {
        given(this.groupService.create(any(GroupDTO.class))).willThrow(new RuntimeException());

        assertThat(this.mvc.post().uri("/groups").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader())
        )
                .hasStatus4xxClientError();
    }

    @Test
    void modify() {
        given(this.groupService.modify(anyLong(), any(GroupDTO.class))).willReturn(Mockito.mock(GroupVO.class));

        assertThat(this.mvc.put().uri("/groups/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader())
        )
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void modify_error() {
        given(this.groupService.modify(anyLong(), any(GroupDTO.class))).willThrow(new RuntimeException());

        assertThat(this.mvc.put().uri("/groups/{id}", anyLong()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader())
        )
                .hasStatus4xxClientError();
    }

    @Test
    void remove() {
        this.groupService.remove(anyLong());

        assertThat(this.mvc.delete().uri("/groups/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void remove_error() {
        doThrow(new RuntimeException()).when(this.groupService).remove(anyLong());

        assertThat(this.mvc.delete().uri("/groups/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatus4xxClientError();
    }

    @Test
    void enable() {
        given(this.groupService.enable(anyLong())).willReturn(true);

        assertThat(this.mvc.patch().uri("/groups/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void members() {
        given(this.groupMembersService.members(anyLong())).willReturn(anyList());

        assertThat(this.mvc.get().uri("/groups/{id}/members", 1L))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void members_error() {
        doThrow(new RuntimeException()).when(this.groupMembersService).members(anyLong());

        assertThat(this.mvc.get().uri("/groups/{id}/members", anyLong()))
                .hasStatus(HttpStatus.NO_CONTENT);
    }


    @Test
    void relation() {
        given(this.groupPrivilegesService.relation(anyLong(), anyLong(), anyString()))
                .willReturn(mock(GroupPrivileges.class));

        assertThat(this.mvc.patch().uri("/groups/{id}/privileges/{privilegeId}", 1L, 1L)
                .queryParam("action", "create")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
        )
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void relation_error() {
        doThrow(new RuntimeException()).when(this.groupPrivilegesService).relation(anyLong(), anyLong(), anyString());

        assertThat(this.mvc.patch().uri("/groups/{id}/privileges/{privilegeId}", 1L, 1L)
                .queryParam("action", "create")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
        )
                .hasStatus4xxClientError();
    }

}