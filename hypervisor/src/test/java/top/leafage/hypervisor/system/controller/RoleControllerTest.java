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
package top.leafage.hypervisor.system.controller;


import org.assertj.core.api.InstanceOfAssertFactories;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;
import top.leafage.hypervisor.system.controller.RoleController;
import top.leafage.hypervisor.system.domain.RoleMembers;
import top.leafage.hypervisor.system.domain.RolePrivileges;
import top.leafage.hypervisor.system.domain.dto.RoleDTO;
import top.leafage.hypervisor.system.domain.vo.RoleVO;
import top.leafage.hypervisor.system.service.RoleMembersService;
import top.leafage.hypervisor.system.service.RolePrivilegesService;
import top.leafage.hypervisor.system.service.RoleService;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.*;
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

        when(roleService.retrieve(anyInt(), anyInt(), eq("id"),
                anyBoolean(), anyString())).thenReturn(voPage);

        assertThat(mvc.get().uri("/roles")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "false")
                .queryParam("filters", "name:like:test")
        )
                .hasStatusOk()
                .bodyJson().extractingPath("$.content")
                .convertTo(InstanceOfAssertFactories.list(RoleVO.class))
                .hasSize(1)
                .element(0).satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));

        verify(roleService).retrieve(anyInt(), anyInt(), anyString(), anyBoolean(), anyString());
    }

    @Test
    void retrieve_error() {
        when(roleService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/roles")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "true")
                .queryParam("filters", "name:like:test")
        )
                .hasStatus5xxServerError();
    }

    @Test
    void fetch() {
        when(roleService.fetch(anyLong())).thenReturn(vo);

        assertThat(mvc.get().uri("/roles/{id}", anyLong()))
                .hasStatusOk()
                .bodyJson()
                .convertTo(RoleVO.class)
                .satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

    @Test
    void fetch_error() {
        when(roleService.fetch(anyLong())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/roles/{id}", anyLong()))
                .hasStatus5xxServerError();
    }

    @Test
    void create() {
        when(roleService.create(any(RoleDTO.class))).thenReturn(vo);

        assertThat(mvc.post().uri("/roles").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader())
        )
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(RoleVO.class)
                .satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

    @Test
    void create_error() {
        when(roleService.create(any(RoleDTO.class))).thenThrow(new RuntimeException());

        assertThat(mvc.post().uri("/roles").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

    @Test
    void modify() {
        when(roleService.modify(anyLong(), any(RoleDTO.class))).thenReturn(vo);

        assertThat(mvc.put().uri("/roles/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus(HttpStatus.ACCEPTED)
                .bodyJson()
                .convertTo(RoleVO.class)
                .satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

    @Test
    void modify_error() {
        when(roleService.modify(anyLong(), any(RoleDTO.class))).thenThrow(new RuntimeException());

        assertThat(mvc.put().uri("/roles/{id}", anyLong()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

    @Test
    void remove() {
        this.roleService.remove(anyLong());

        assertThat(mvc.delete().uri("/roles/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void remove_error() {
        doThrow(new RuntimeException()).when(roleService).remove(anyLong());

        assertThat(mvc.delete().uri("/roles/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

    @Test
    void enable() {
        when(roleService.enable(anyLong())).thenReturn(true);

        assertThat(mvc.patch().uri("/roles/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void importFromFile() {
        when(roleService.createAll(anyList())).thenReturn(List.of(vo));

        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[1]);
        assertThat(mvc.post().uri("/roles/import").multipart().file(file).with(csrf().asHeader()))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(RoleVO.class))
                .hasSize(1)
                .element(0).satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

    @Test
    void members() {
        when(roleMembersService.members(anyLong())).thenReturn(List.of(mock(RoleMembers.class)));

        assertThat(mvc.get().uri("/roles/{id}/members", 1L))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(RoleMembers.class))
                .hasSize(1);
    }

    @Test
    void members_error() {
        doThrow(new RuntimeException()).when(roleMembersService).members(anyLong());

        assertThat(mvc.get().uri("/roles/{id}/members", anyLong()))
                .hasStatus5xxServerError();
    }

    @Test
    void relationMembers() {
        when(roleMembersService.relation(anyLong(), anySet())).thenReturn(List.of(mock(RoleMembers.class)));

        assertThat(mvc.patch().uri("/roles/{id}/members", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Set.of("test")))
                .with(csrf().asHeader())
        )
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(RoleMembers.class))
                .hasSize(1);
    }

    @Test
    void relationMembers_error() {
        doThrow(new RuntimeException()).when(roleMembersService).relation(anyLong(), anySet());

        assertThat(mvc.patch().uri("/roles/{id}/members/{privilegeId}", 1L, 1L)
                .queryParam("action", "create")
                .contentType(MediaType.APPLICATION_JSON).with(csrf().asHeader())
        )
                .hasStatus5xxServerError();
    }

    @Test
    void removeMembers() {
        roleMembersService.removeRelation(anyLong(), anySet());

        assertThat(mvc.delete().uri("/roles/{id}/members", 1L)
                .queryParam("usernames", "test")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
        )
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void privileges() {
        when(rolePrivilegesService.privileges(anyLong())).thenReturn(List.of(mock(RolePrivileges.class)));

        assertThat(mvc.get().uri("/roles/{id}/privileges", 1L))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(RolePrivileges.class))
                .hasSize(1);
    }

    @Test
    void privileges_error() {
        doThrow(new RuntimeException()).when(rolePrivilegesService).privileges(anyLong());

        assertThat(mvc.get().uri("/roles/{id}/privileges", anyLong()))
                .hasStatus5xxServerError();
    }

    @Test
    void relationPrivileges() {
        when(rolePrivilegesService.relation(anyLong(), anyLong(), anyString())).thenReturn(mock(RolePrivileges.class));

        assertThat(mvc.patch().uri("/roles/{id}/privileges/{privilegeId}", 1L, 1L)
                .queryParam("action", "create")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
        )
                .hasStatusOk()
                .bodyJson()
                .convertTo(RolePrivileges.class)
                .isNotNull();
    }

    @Test
    void relationPrivileges_error() {
        doThrow(new RuntimeException()).when(rolePrivilegesService).relation(anyLong(), anyLong(), anyString());

        assertThat(mvc.patch().uri("/roles/{id}/privileges/{privilegeId}", 1L, 1L)
                .queryParam("action", "create")
                .contentType(MediaType.APPLICATION_JSON).with(csrf().asHeader())
        )
                .hasStatus5xxServerError();
    }

    @Test
    void removePrivileges() {
        rolePrivilegesService.removeRelation(anyLong(), anyLong(), anyString());

        assertThat(mvc.delete().uri("/roles/{id}/privileges/{privilegeId}", 1L, 1L)
                .queryParam("action", "create")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
        )
                .hasStatus(HttpStatus.NO_CONTENT);
    }
}