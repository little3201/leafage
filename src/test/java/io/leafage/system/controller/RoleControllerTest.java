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
package io.leafage.system.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.leafage.system.domain.RolePrivileges;
import io.leafage.system.dto.RoleDTO;
import io.leafage.system.service.RoleMembersService;
import io.leafage.system.service.RolePrivilegesService;
import io.leafage.system.service.RoleService;
import io.leafage.system.vo.RoleVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
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
@ExtendWith(SpringExtension.class)
@WebMvcTest(RoleController.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mvc;

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
        Page<RoleVO> voPage = new PageImpl<>(List.of(vo), Mockito.mock(PageRequest.class), 2L);
        given(this.roleService.retrieve(Mockito.anyInt(), Mockito.anyInt(), eq("id"),
                Mockito.anyBoolean(), eq("test"))).willReturn(voPage);

        mvc.perform(get("/roles").queryParam("page", "0").queryParam("size", "2")
                        .queryParam("sortBy", "id").queryParam("name", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andDo(print())
                .andReturn();
    }

    @Test
    void retrieve_error() throws Exception {
        given(this.roleService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyBoolean(), Mockito.anyString())).willThrow(new RuntimeException());

        mvc.perform(get("/roles").queryParam("page", "0").queryParam("size", "2")
                        .queryParam("sortBy", "id").queryParam("name", "test"))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn();
    }

    @Test
    void fetch() throws Exception {
        given(this.roleService.fetch(Mockito.anyLong())).willReturn(vo);

        mvc.perform(get("/roles/{id}", Mockito.anyLong())).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test")).andDo(print()).andReturn();
    }

    @Test
    void fetch_error() throws Exception {
        given(this.roleService.fetch(Mockito.anyLong())).willThrow(new RuntimeException());

        mvc.perform(get("/roles/{id}", Mockito.anyLong())).andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void create() throws Exception {
        given(this.roleService.create(Mockito.any(RoleDTO.class))).willReturn(vo);

        mvc.perform(post("/roles").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader())).andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test"))
                .andDo(print()).andReturn();
    }

    @Test
    void create_error() throws Exception {
        given(this.roleService.create(Mockito.any(RoleDTO.class))).willThrow(new RuntimeException());

        mvc.perform(post("/roles").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

    @Test
    void modify() throws Exception {
        given(this.roleService.modify(Mockito.anyLong(), Mockito.any(RoleDTO.class))).willReturn(vo);

        mvc.perform(put("/roles/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isAccepted())
                .andDo(print()).andReturn();
    }

    @Test
    void modify_error() throws Exception {
        given(this.roleService.modify(Mockito.anyLong(), Mockito.any(RoleDTO.class))).willThrow(new RuntimeException());

        mvc.perform(put("/roles/{id}", Mockito.anyLong()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isNotModified())
                .andDo(print()).andReturn();
    }

    @Test
    void remove() throws Exception {
        this.roleService.remove(Mockito.anyLong());

        mvc.perform(delete("/roles/{id}", Mockito.anyLong()).with(csrf().asHeader())).andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void remove_error() throws Exception {
        doThrow(new RuntimeException()).when(this.roleService).remove(Mockito.anyLong());

        mvc.perform(delete("/roles/{id}", Mockito.anyLong()).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

    @Test
    void members() throws Exception {
        given(this.roleMembersService.members(Mockito.anyLong())).willReturn(Mockito.anyList());

        mvc.perform(get("/roles/{id}/members", 1L)).andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void members_error() throws Exception {
        doThrow(new RuntimeException()).when(this.roleMembersService).members(Mockito.anyLong());

        mvc.perform(get("/roles/{id}/members", Mockito.anyLong())).andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void privileges() throws Exception {
        given(this.rolePrivilegesService.privileges(Mockito.anyLong())).willReturn(Mockito.anyList());

        mvc.perform(get("/roles/{id}/privileges", 1L)).andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void authorities_error() throws Exception {
        doThrow(new RuntimeException()).when(this.rolePrivilegesService).privileges(Mockito.anyLong());

        mvc.perform(get("/roles/{id}/privileges", Mockito.anyLong())).andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void relation() throws Exception {
        given(this.rolePrivilegesService.relation(Mockito.anyLong(), Mockito.anySet()))
                .willReturn(List.of(Mockito.mock(RolePrivileges.class)));

        mvc.perform(patch("/roles/{id}/privileges", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(Set.of(1L))).with(csrf().asHeader()))
                .andExpect(status().isAccepted())
                .andDo(print()).andReturn();
    }

    @Test
    void relation_error() throws Exception {
        doThrow(new RuntimeException()).when(this.rolePrivilegesService).relation(Mockito.anyLong(), Mockito.anySet());

        mvc.perform(patch("/roles/{id}/privileges", Mockito.anyLong()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(Set.of(1L))).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

}