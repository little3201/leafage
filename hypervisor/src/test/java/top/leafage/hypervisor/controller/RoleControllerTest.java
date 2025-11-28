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
import top.leafage.hypervisor.domain.RolePrivileges;
import top.leafage.hypervisor.domain.dto.RoleDTO;
import top.leafage.hypervisor.domain.vo.RoleVO;
import top.leafage.hypervisor.service.RoleMembersService;
import top.leafage.hypervisor.service.RolePrivilegesService;
import top.leafage.hypervisor.service.RoleService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

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
        vo = new RoleVO(1L, "test", "description", true);

        dto = new RoleDTO();
        dto.setName("test");
        dto.setDescription("description");
    }

    @Test
    void retrieve() {
        Page<@NonNull RoleVO> voPage = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        given(this.roleService.retrieve(anyInt(), anyInt(), eq("id"),
                anyBoolean(), anyString())).willReturn(voPage);

        assertThat(this.mvc.get().uri("/roles")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "false")
                .queryParam("filters", "name:like:a")
        )
                .hasStatusOk()
                .body().isNotNull().hasSize(1);
    }

    @Test
    void retrieve_error() {
        given(this.roleService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/roles")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "true")
                .queryParam("filters", "name:like:a")
        )
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void fetch() {
        given(this.roleService.fetch(anyLong())).willReturn(vo);

        assertThat(this.mvc.get().uri("/roles/{id}", anyLong()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void fetch_error() {
        given(this.roleService.fetch(anyLong())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/roles/{id}", anyLong()))
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void create() {
        given(this.roleService.create(any(RoleDTO.class))).willReturn(vo);

        assertThat(this.mvc.post().uri("/roles").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader())
        )
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void create_error() {
        given(this.roleService.create(any(RoleDTO.class))).willThrow(new RuntimeException());

        assertThat(this.mvc.post().uri("/roles").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus4xxClientError();
    }

    @Test
    void modify() {
        given(this.roleService.modify(anyLong(), any(RoleDTO.class))).willReturn(vo);

        assertThat(this.mvc.put().uri("/roles/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void modify_error() {
        given(this.roleService.modify(anyLong(), any(RoleDTO.class))).willThrow(new RuntimeException());

        assertThat(this.mvc.put().uri("/roles/{id}", anyLong()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus4xxClientError();
    }

    @Test
    void remove() {
        this.roleService.remove(anyLong());

        assertThat(this.mvc.delete().uri("/roles/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void remove_error() {
        doThrow(new RuntimeException()).when(this.roleService).remove(anyLong());

        assertThat(this.mvc.delete().uri("/roles/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatus4xxClientError();
    }

    @Test
    void enable() {
        given(this.roleService.enable(anyLong())).willReturn(true);

        assertThat(this.mvc.patch().uri("/roles/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void members() {
        given(this.roleMembersService.members(anyLong())).willReturn(anyList());

        assertThat(this.mvc.get().uri("/roles/{id}/members", 1L))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void members_error() {
        doThrow(new RuntimeException()).when(this.roleMembersService).members(anyLong());

        assertThat(this.mvc.get().uri("/roles/{id}/members", anyLong()))
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void privileges() {
        given(this.rolePrivilegesService.privileges(anyLong())).willReturn(anyList());

        assertThat(this.mvc.get().uri("/roles/{id}/privileges", 1L))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void authorities_error() {
        doThrow(new RuntimeException()).when(this.rolePrivilegesService).privileges(anyLong());

        assertThat(this.mvc.get().uri("/roles/{id}/privileges", anyLong()))
                .hasStatus4xxClientError();
    }

    @Test
    void relation() {
        given(this.rolePrivilegesService.relation(anyLong(), anyLong(), anyString()))
                .willReturn(mock(RolePrivileges.class));

        assertThat(this.mvc.patch().uri("/roles/{id}/privileges/{privilegeId}", 1L, 1L)
                .queryParam("action", "create")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
        )
                .hasStatusOk();
    }

    @Test
    void relation_error() {
        doThrow(new RuntimeException()).when(this.rolePrivilegesService).relation(anyLong(), anyLong(), anyString());

        assertThat(this.mvc.patch().uri("/roles/{id}/privileges/{privilegeId}", 1L, 1L)
                .queryParam("action", "create")
                .contentType(MediaType.APPLICATION_JSON).with(csrf().asHeader())
        )
                .hasStatus4xxClientError();
    }

}