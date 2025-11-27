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

import tools.jackson.databind.ObjectMapper;
import top.leafage.hypervisor.domain.Group;
import top.leafage.hypervisor.domain.GroupPrivileges;
import top.leafage.hypervisor.domain.dto.GroupDTO;
import top.leafage.hypervisor.domain.vo.GroupVO;
import top.leafage.hypervisor.service.GroupMembersService;
import top.leafage.hypervisor.service.GroupPrivilegesService;
import top.leafage.hypervisor.service.GroupRolesService;
import top.leafage.hypervisor.service.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import top.leafage.common.data.TreeNode;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void retrieve() throws Exception {
        Page<Group> voPage = new PageImpl<>(List.of(Mockito.mock(Group.class)), mock(PageRequest.class), 2L);

        given(this.groupService.retrieve(anyInt(), anyInt(), eq("id"),
                anyBoolean(), anyString())).willReturn(voPage);

        assertThat(this.mvc.get().uri("/groups")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                        .queryParam("filters", "")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andDo(print()).andReturn();
    }

    @Test
    void retrieve_error() throws Exception {
        given(this.groupService.retrieve(anyInt(), anyInt(), eq("id"), anyBoolean(),
                anyString())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/groups")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "true")
                        .queryParam("filters", "")
                )
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn();
    }

    @Test
    void tree() throws Exception {
        TreeNode<Long> treeNode = TreeNode.withId(1L).name("test").build();
        given(this.groupService.tree()).willReturn(Collections.singletonList(treeNode));

        assertThat(this.mvc.get().uri("/groups/tree")).andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void fetch() throws Exception {
        given(this.groupService.fetch(anyLong())).willReturn(Optional.ofNullable(Mockito.mock(Group.class)));

        assertThat(this.mvc.get().uri("/groups/{id}", anyLong())).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test")).andDo(print()).andReturn();
    }

    @Test
    void fetch_error() throws Exception {
        given(this.groupService.fetch(anyLong())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/groups/{id}", anyLong())).andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void create() throws Exception {
        given(this.groupService.create(any(Group.class))).willReturn(Mockito.mock(Group.class));

        assertThat(this.mvc.post().uri("/groups").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test"))
                .andDo(print()).andReturn();
    }

    @Test
    void create_error() throws Exception {
        given(this.groupService.create(any(Group.class))).willThrow(new RuntimeException());

        assertThat(this.mvc.post().uri("/groups").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

    @Test
    void modify() throws Exception {
        given(this.groupService.modify(anyLong(), any(Group.class))).willReturn(Mockito.mock(Group.class));

        assertThat(this.mvc.put().uri("/groups/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isAccepted())
                .andDo(print()).andReturn();
    }

    @Test
    void modify_error() throws Exception {
        given(this.groupService.modify(anyLong(), any(Group.class))).willThrow(new RuntimeException());

        assertThat(this.mvc.put().uri("/groups/{id}", anyLong()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isNotModified())
                .andDo(print()).andReturn();
    }

    @Test
    void remove() throws Exception {
        this.groupService.remove(anyLong());

        assertThat(this.mvc.delete().uri("/groups/{id}", anyLong()).with(csrf().asHeader())).andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void remove_error() throws Exception {
        doThrow(new RuntimeException()).when(this.groupService).remove(anyLong());

        assertThat(this.mvc.delete().uri("/groups/{id}", anyLong()).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

    @Test
    void enable() throws Exception {
        given(this.groupService.enable(anyLong())).willReturn(true);

        assertThat(patch("/groups/{id}", anyLong()).with(csrf().asHeader()))
                .andExpect(status().isAccepted())
                .andDo(print()).andReturn();
    }

    @Test
    void members() throws Exception {
        given(this.groupMembersService.members(anyLong())).willReturn(anyList());

        assertThat(this.mvc.get().uri("/groups/{id}/members", 1L)).andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void members_error() throws Exception {
        doThrow(new RuntimeException()).when(this.groupMembersService).members(anyLong());

        assertThat(this.mvc.get().uri("/groups/{id}/members", anyLong())).andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }


    @Test
    void relation() throws Exception {
        given(this.groupPrivilegesService.relation(anyLong(), anyLong(), anyString()))
                .willReturn(mock(GroupPrivileges.class));

        assertThat(patch("/groups/{id}/privileges/{privilegeId}", 1L, 1L)
                        .queryParam("action", "create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader()))
                .andExpect(status().isAccepted())
                .andDo(print()).andReturn();
    }

    @Test
    void relation_error() throws Exception {
        doThrow(new RuntimeException()).when(this.groupPrivilegesService).relation(anyLong(), anyLong(), anyString());

        assertThat(patch("/groups/{id}/privileges/{privilegeId}", 1L, 1L)
                        .queryParam("action", "create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

}