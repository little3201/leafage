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
import com.server.starter.domain.TreeNode;
import io.leafage.system.dto.PrivilegeDTO;
import io.leafage.system.service.PrivilegeService;
import io.leafage.system.service.RolePrivilegesService;
import io.leafage.system.vo.PrivilegeVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * privilege controller test
 *
 * @author wq li
 **/
@WithMockUser
@ExtendWith(SpringExtension.class)
@WebMvcTest(PrivilegeController.class)
class PrivilegeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private PrivilegeService privilegeService;

    @MockBean
    private RolePrivilegesService rolePrivilegesService;

    private PrivilegeVO privilegeVO;

    private PrivilegeDTO privilegeDTO;

    @BeforeEach
    void setUp() {
        privilegeVO = new PrivilegeVO(1L, true, Instant.now());
        privilegeVO.setName("test");
        privilegeVO.setIcon("icon");
        privilegeVO.setPath("path");
        privilegeVO.setDescription("description");
        privilegeVO.setComponent("component");

        privilegeDTO = new PrivilegeDTO();
        privilegeDTO.setName("test");
        privilegeDTO.setRedirect("redirect");
        privilegeVO.setDescription("description");
        privilegeDTO.setPath("/test");
        privilegeDTO.setIcon("icon");
        privilegeDTO.setSuperiorId(1L);
    }

    @Test
    void retrieve() throws Exception {
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"));
        Page<PrivilegeVO> voPage = new PageImpl<>(List.of(privilegeVO), pageable, 2L);
        given(this.privilegeService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyBoolean(), Mockito.anyString())).willReturn(voPage);

        mvc.perform(get("/privileges").queryParam("page", "0").queryParam("size", "2")
                        .queryParam("sortBy", "id").queryParam("name", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andDo(print())
                .andReturn();
    }

    @Test
    void retrieve_error() throws Exception {
        given(this.privilegeService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyBoolean(), Mockito.anyString())).willThrow(new RuntimeException());

        mvc.perform(get("/privileges").queryParam("page", "0").queryParam("size", "2")
                        .queryParam("sortBy", "id").queryParam("name", "test"))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn();
    }

    @Test
    void fetch() throws Exception {
        given(this.privilegeService.fetch(Mockito.anyLong())).willReturn(privilegeVO);

        mvc.perform(get("/privileges/{id}", Mockito.anyLong())).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test")).andDo(print()).andReturn();
    }

    @Test
    void fetch_error() throws Exception {
        given(this.privilegeService.fetch(Mockito.anyLong())).willThrow(new RuntimeException());

        mvc.perform(get("/privileges/{id}", Mockito.anyLong())).andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void modify() throws Exception {
        given(this.privilegeService.modify(Mockito.anyLong(), Mockito.any(PrivilegeDTO.class))).willReturn(privilegeVO);

        mvc.perform(put("/privileges/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(privilegeDTO)).with(csrf().asHeader()))
                .andExpect(status().isAccepted())
                .andDo(print()).andReturn();
    }

    @Test
    void modify_error() throws Exception {
        given(this.privilegeService.modify(Mockito.anyLong(), Mockito.any(PrivilegeDTO.class))).willThrow(new RuntimeException());

        mvc.perform(put("/privileges/{id}", Mockito.anyLong()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(privilegeDTO)).with(csrf().asHeader()))
                .andExpect(status().isNotModified())
                .andDo(print()).andReturn();
    }

    @Test
    void remove() throws Exception {
        this.privilegeService.remove(Mockito.anyLong());

        mvc.perform(delete("/privileges/{id}", Mockito.anyLong()).with(csrf().asHeader())).andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void remove_error() throws Exception {
        doThrow(new RuntimeException()).when(this.privilegeService).remove(Mockito.anyLong());

        mvc.perform(delete("/privileges/{id}", Mockito.anyLong()).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

    @Test
    void tree() throws Exception {
        TreeNode treeNode = TreeNode.withId(1L).name("test").build();
        given(this.privilegeService.tree(Mockito.anyString())).willReturn(Collections.singletonList(treeNode));

        mvc.perform(get("/privileges/tree"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }

    @Test
    void tree_error() throws Exception {
        given(this.privilegeService.tree(Mockito.anyString())).willThrow(new RuntimeException());

        mvc.perform(get("/privileges/tree"))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn();
    }

    @Test
    void roles() throws Exception {
        given(this.rolePrivilegesService.roles(Mockito.anyLong())).willReturn(Mockito.anyList());

        mvc.perform(get("/privileges/{id}/roles", 1L)).andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void roles_error() throws Exception {
        given(this.rolePrivilegesService.roles(Mockito.anyLong())).willThrow(new RuntimeException());

        mvc.perform(get("/privileges/{id}/roles", Mockito.anyLong())).andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }
}