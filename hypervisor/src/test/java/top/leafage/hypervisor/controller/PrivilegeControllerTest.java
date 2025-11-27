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
import top.leafage.hypervisor.domain.dto.PrivilegeDTO;
import top.leafage.hypervisor.service.PrivilegeService;
import top.leafage.hypervisor.service.RolePrivilegesService;
import top.leafage.hypervisor.domain.vo.PrivilegeVO;
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
import top.leafage.common.TreeNode;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
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
@WebMvcTest(PrivilegeController.class)
class PrivilegeControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private PrivilegeService privilegeService;

    @MockitoBean
    private RolePrivilegesService rolePrivilegesService;

    private PrivilegeVO vo;

    private PrivilegeDTO dto;

    @BeforeEach
    void setUp() {
        vo = new PrivilegeVO();
        vo.setId(1L);
        vo.setName("test");
        vo.setIcon("icon");
        vo.setPath("path");
        vo.setDescription("description");
        vo.setComponent("component");

        dto = new PrivilegeDTO();
        dto.setName("test");
        dto.setRedirect("redirect");
        vo.setDescription("description");
        dto.setPath("/test");
        dto.setIcon("icon");
        dto.setSuperiorId(1L);
    }

    @Test
    void retrieve() throws Exception {
        Page<PrivilegeVO> voPage = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        given(this.privilegeService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willReturn(voPage);

        assertThat(this.mvc.get().uri("/privileges")
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
        given(this.privilegeService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/privileges")
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
        given(this.privilegeService.fetch(anyLong())).willReturn(vo);

        assertThat(this.mvc.get().uri("/privileges/{id}", anyLong())).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test")).andDo(print()).andReturn();
    }

    @Test
    void fetch_error() throws Exception {
        given(this.privilegeService.fetch(anyLong())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/privileges/{id}", anyLong())).andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void modify() throws Exception {
        given(this.privilegeService.modify(anyLong(), any(PrivilegeDTO.class))).willReturn(vo);

        assertThat(this.mvc.put().uri("/privileges/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isAccepted())
                .andDo(print()).andReturn();
    }

    @Test
    void modify_error() throws Exception {
        given(this.privilegeService.modify(anyLong(), any(PrivilegeDTO.class))).willThrow(new RuntimeException());

        assertThat(this.mvc.put().uri("/privileges/{id}", anyLong()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isNotModified())
                .andDo(print()).andReturn();
    }

    @Test
    void tree() throws Exception {
        TreeNode<Long> treeNode = TreeNode.withId(1L).name("test").build();
        given(this.privilegeService.tree(anyString())).willReturn(Collections.singletonList(treeNode));

        assertThat(this.mvc.get().uri("/privileges/tree"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }

    @Test
    void tree_error() throws Exception {
        given(this.privilegeService.tree(anyString())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/privileges/tree"))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn();
    }

    @Test
    void enable() throws Exception {
        given(this.privilegeService.enable(anyLong())).willReturn(true);

        assertThat(patch("/privileges/{id}", anyLong()).with(csrf().asHeader()))
                .andExpect(status().isAccepted())
                .andDo(print()).andReturn();
    }

}