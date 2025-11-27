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
import top.leafage.hypervisor.domain.RolePrivileges;
import top.leafage.hypervisor.domain.dto.RoleDTO;
import top.leafage.hypervisor.service.RoleMembersService;
import top.leafage.hypervisor.service.RolePrivilegesService;
import top.leafage.hypervisor.service.RoleService;
import top.leafage.hypervisor.domain.vo.RoleVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import java.util.List;

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
 * role controller test
 *
 * @author wq li
 **/
@WithMockUser
@WebMvcTest(RoleController.class)
class RoleControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private RoleMembersService roleMembersService;

    @MockitoBean
    private RolePrivilegesService rolePrivilegesService;

    private RoleVO vo;

    private RoleDTO dto;

    @BeforeEach
    void setUp() {
        vo = new RoleVO();
        vo.setId(1L);
        vo.setName("test");
        vo.setDescription("description");

        dto = new RoleDTO();
        dto.setName("test");
        dto.setDescription("description");
    }

    @Test
    void retrieve() throws Exception {
        Page<RoleVO> voPage = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        given(this.roleService.retrieve(anyInt(), anyInt(), eq("id"),
                anyBoolean(), anyString())).willReturn(voPage);

        assertThat(this.mvc.get().uri("/roles")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                        .queryParam("filters", "name:like:a")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andDo(print())
                .andReturn();
    }

    @Test
    void retrieve_error() throws Exception {
        given(this.roleService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/roles")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "true")
                        .queryParam("filters", "name:like:a")
                )
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn();
    }

    @Test
    void fetch() throws Exception {
        given(this.roleService.fetch(anyLong())).willReturn(vo);

        assertThat(this.mvc.get().uri("/roles/{id}", anyLong())).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test")).andDo(print()).andReturn();
    }

    @Test
    void fetch_error() throws Exception {
        given(this.roleService.fetch(anyLong())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/roles/{id}", anyLong())).andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void create() throws Exception {
        given(this.roleService.create(any(RoleDTO.class))).willReturn(vo);

        assertThat(this.mvc.post().uri("/roles").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader())).andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test"))
                .andDo(print()).andReturn();
    }

    @Test
    void create_error() throws Exception {
        given(this.roleService.create(any(RoleDTO.class))).willThrow(new RuntimeException());

        assertThat(this.mvc.post().uri("/roles").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

    @Test
    void modify() throws Exception {
        given(this.roleService.modify(anyLong(), any(RoleDTO.class))).willReturn(vo);

        assertThat(this.mvc.put().uri("/roles/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isAccepted())
                .andDo(print()).andReturn();
    }

    @Test
    void modify_error() throws Exception {
        given(this.roleService.modify(anyLong(), any(RoleDTO.class))).willThrow(new RuntimeException());

        assertThat(this.mvc.put().uri("/roles/{id}", anyLong()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isNotModified())
                .andDo(print()).andReturn();
    }

    @Test
    void remove() throws Exception {
        this.roleService.remove(anyLong());

        assertThat(this.mvc.delete().uri("/roles/{id}", anyLong()).with(csrf().asHeader())).andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void remove_error() throws Exception {
        doThrow(new RuntimeException()).when(this.roleService).remove(anyLong());

        assertThat(this.mvc.delete().uri("/roles/{id}", anyLong()).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

    @Test
    void enable() throws Exception {
        given(this.roleService.enable(anyLong())).willReturn(true);

        assertThat(patch("/roles/{id}", anyLong()).with(csrf().asHeader()))
                .andExpect(status().isAccepted())
                .andDo(print()).andReturn();
    }

    @Test
    void members() throws Exception {
        given(this.roleMembersService.members(anyLong())).willReturn(anyList());

        assertThat(this.mvc.get().uri("/roles/{id}/members", 1L)).andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void members_error() throws Exception {
        doThrow(new RuntimeException()).when(this.roleMembersService).members(anyLong());

        assertThat(this.mvc.get().uri("/roles/{id}/members", anyLong())).andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void privileges() throws Exception {
        given(this.rolePrivilegesService.privileges(anyLong())).willReturn(anyList());

        assertThat(this.mvc.get().uri("/roles/{id}/privileges", 1L)).andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void authorities_error() throws Exception {
        doThrow(new RuntimeException()).when(this.rolePrivilegesService).privileges(anyLong());

        assertThat(this.mvc.get().uri("/roles/{id}/privileges", anyLong())).andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void relation() throws Exception {
        given(this.rolePrivilegesService.relation(anyLong(), anyLong(), anyString()))
                .willReturn(mock(RolePrivileges.class));

        assertThat(patch("/roles/{id}/privileges/{privilegeId}", 1L, 1L)
                        .queryParam("action", "create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader()))
                .andExpect(status().isAccepted())
                .andDo(print()).andReturn();
    }

    @Test
    void relation_error() throws Exception {
        doThrow(new RuntimeException()).when(this.rolePrivilegesService).relation(anyLong(), anyLong(), anyString());

        assertThat(patch("/roles/{id}/privileges/{privilegeId}", 1L, 1L)
                        .queryParam("action", "create")
                        .contentType(MediaType.APPLICATION_JSON).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

}