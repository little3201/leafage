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
import top.leafage.common.data.domain.TreeNode;
import top.leafage.hypervisor.domain.dto.PrivilegeDTO;
import top.leafage.hypervisor.domain.vo.PrivilegeVO;
import top.leafage.hypervisor.service.PrivilegeService;
import top.leafage.hypervisor.service.RolePrivilegesService;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

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
        vo = new PrivilegeVO(1L, "test", null, "test", "test", "#", "icon", Set.of("create,modify"), "description", 0);

        dto = new PrivilegeDTO();
        dto.setName("test");
        dto.setRedirect("redirect");
        dto.setDescription("description");
        dto.setPath("/test");
        dto.setIcon("icon");
        dto.setSuperiorId(1L);
    }

    @Test
    void retrieve() {
        Page<@NonNull PrivilegeVO> voPage = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        given(this.privilegeService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willReturn(voPage);

        assertThat(this.mvc.get().uri("/privileges")
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
        given(this.privilegeService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/privileges")
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
        given(this.privilegeService.fetch(anyLong())).willReturn(vo);

        assertThat(this.mvc.get().uri("/privileges/{id}", anyLong()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void fetch_error() {
        given(this.privilegeService.fetch(anyLong())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/privileges/{id}", anyLong()))
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void modify() {
        given(this.privilegeService.modify(anyLong(), any(PrivilegeDTO.class))).willReturn(vo);

        assertThat(this.mvc.put().uri("/privileges/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void modify_error() {
        given(this.privilegeService.modify(anyLong(), any(PrivilegeDTO.class))).willThrow(new RuntimeException());

        assertThat(this.mvc.put().uri("/privileges/{id}", anyLong()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus4xxClientError();
    }

    @Test
    void tree() {
        TreeNode<Long> treeNode = TreeNode.withId(1L).name("test").build();
        given(this.privilegeService.tree(anyString())).willReturn(Collections.singletonList(treeNode));

        assertThat(this.mvc.get().uri("/privileges/tree"))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void tree_error() {
        given(this.privilegeService.tree(anyString())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/privileges/tree"))
                .hasStatus4xxClientError();
    }

    @Test
    void enable() {
        given(this.privilegeService.enable(anyLong())).willReturn(true);

        assertThat(this.mvc.patch().uri("/privileges/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatusOk()
                .body().isNotNull();
    }

}