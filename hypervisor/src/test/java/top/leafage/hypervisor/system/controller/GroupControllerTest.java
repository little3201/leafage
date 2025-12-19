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
import top.leafage.common.data.domain.TreeNode;
import top.leafage.hypervisor.system.controller.GroupController;
import top.leafage.hypervisor.system.domain.GroupMembers;
import top.leafage.hypervisor.system.domain.GroupPrivileges;
import top.leafage.hypervisor.system.domain.GroupRoles;
import top.leafage.hypervisor.system.domain.dto.GroupDTO;
import top.leafage.hypervisor.system.domain.vo.GroupVO;
import top.leafage.hypervisor.system.service.GroupMembersService;
import top.leafage.hypervisor.system.service.GroupPrivilegesService;
import top.leafage.hypervisor.system.service.GroupRolesService;
import top.leafage.hypervisor.system.service.GroupService;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.*;
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
        Page<@NonNull GroupVO> voPage = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        when(groupService.retrieve(anyInt(), anyInt(), eq("id"),
                anyBoolean(), anyString())).thenReturn(voPage);

        assertThat(mvc.get().uri("/groups")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "false")
                .queryParam("filters", "")
        )
                .hasStatusOk()
                .bodyJson().extractingPath("$.content")
                .convertTo(InstanceOfAssertFactories.list(GroupVO.class))
                .hasSize(1)
                .element(0).satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));

        verify(groupService).retrieve(anyInt(), anyInt(), anyString(), anyBoolean(), anyString());
    }

    @Test
    void retrieve_error() {
        when(groupService.retrieve(anyInt(), anyInt(), eq("id"), anyBoolean(),
                anyString())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/groups")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "true")
                .queryParam("filters", "")
        )
                .hasStatus5xxServerError();
    }

    @Test
    void tree() {
        TreeNode<Long> treeNode = TreeNode.withId(1L).name("test").build();
        when(groupService.tree()).thenReturn(Collections.singletonList(treeNode));

        assertThat(mvc.get().uri("/groups/tree"))
                .hasStatusOk()
                .bodyJson().extractingPath("$[0].name").isEqualTo("test");
    }

    @Test
    void fetch() {
        when(groupService.fetch(anyLong())).thenReturn(vo);

        assertThat(mvc.get().uri("/groups/{id}", anyLong()))
                .hasStatusOk()
                .bodyJson()
                .convertTo(GroupVO.class)
                .satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

    @Test
    void fetch_error() {
        when(groupService.fetch(anyLong())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/groups/{id}", anyLong()))
                .hasStatus5xxServerError();
    }

    @Test
    void create() {
        when(groupService.create(any(GroupDTO.class))).thenReturn(vo);

        assertThat(mvc.post().uri("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader())
        )
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(GroupVO.class)
                .satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

    @Test
    void create_error() {
        when(groupService.create(any(GroupDTO.class))).thenThrow(new RuntimeException());

        assertThat(mvc.post().uri("/groups").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader())
        )
                .hasStatus5xxServerError();
    }

    @Test
    void modify() {
        when(groupService.modify(anyLong(), any(GroupDTO.class))).thenReturn(vo);

        assertThat(mvc.put().uri("/groups/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader())
        )
                .hasStatus(HttpStatus.ACCEPTED)
                .bodyJson()
                .convertTo(GroupVO.class)
                .satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

    @Test
    void modify_error() {
        when(groupService.modify(anyLong(), any(GroupDTO.class))).thenThrow(new RuntimeException());

        assertThat(mvc.put().uri("/groups/{id}", anyLong()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader())
        )
                .hasStatus5xxServerError();
    }

    @Test
    void remove() {
        this.groupService.remove(anyLong());

        assertThat(mvc.delete().uri("/groups/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void remove_error() {
        doThrow(new RuntimeException()).when(groupService).remove(anyLong());

        assertThat(mvc.delete().uri("/groups/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

    @Test
    void enable() {
        when(groupService.enable(anyLong())).thenReturn(true);

        assertThat(mvc.patch().uri("/groups/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void members() {
        when(groupMembersService.members(anyLong())).thenReturn(anyList());

        assertThat(mvc.get().uri("/groups/{id}/members", 1L))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void members_error() {
        doThrow(new RuntimeException()).when(groupMembersService).members(anyLong());

        assertThat(mvc.get().uri("/groups/{id}/members", anyLong()))
                .hasStatus5xxServerError();
    }

    @Test
    void relationMembers() {
        when(groupMembersService.relation(anyLong(), anySet())).thenReturn(List.of(mock(GroupMembers.class)));

        assertThat(mvc.patch().uri("/groups/{id}/members", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Set.of("test")))
                .with(csrf().asHeader())
        )
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(GroupMembers.class))
                .hasSize(1);
    }

    @Test
    void relationMembers_error() {
        doThrow(new RuntimeException()).when(groupMembersService).relation(anyLong(), anySet());

        assertThat(mvc.patch().uri("/groups/{id}/members", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Set.of("test")))
                .with(csrf().asHeader())
        )
                .hasStatus5xxServerError();
    }

    @Test
    void removeMembers() {
        groupMembersService.removeRelation(anyLong(), anySet());

        assertThat(mvc.delete().uri("/groups/{id}/members", 1L)
                .queryParam("usernames", "test")
                .with(csrf().asHeader())
        )
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void relationPrivileges() {
        when(groupPrivilegesService.relation(anyLong(), anyLong(), anyString()))
                .thenReturn(mock(GroupPrivileges.class));

        assertThat(mvc.patch().uri("/groups/{id}/privileges/{privilegeId}", 1L, 1L)
                .queryParam("action", "create")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
        )
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void relationPrivileges_error() {
        doThrow(new RuntimeException()).when(groupPrivilegesService).relation(anyLong(), anyLong(), anyString());

        assertThat(mvc.patch().uri("/groups/{id}/privileges/{privilegeId}", 1L, 1L)
                .queryParam("action", "create")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
        )
                .hasStatus5xxServerError();
    }

    @Test
    void privileges() {
        when(groupPrivilegesService.privileges(anyLong())).thenReturn(List.of(mock(GroupPrivileges.class)));

        assertThat(mvc.get().uri("/groups/{id}/privileges", 1L))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(GroupPrivileges.class))
                .hasSize(1);
    }

    @Test
    void privileges_error() {
        doThrow(new RuntimeException()).when(groupPrivilegesService).privileges(anyLong());

        assertThat(mvc.get().uri("/groups/{id}/privileges", anyLong()))
                .hasStatus5xxServerError();
    }

    @Test
    void removePrivileges() {
        groupPrivilegesService.removeRelation(anyLong(), anyLong(), anyString());

        assertThat(mvc.delete().uri("/groups/{id}/privileges/{privilegeId}", 1L, 1L)
                .queryParam("action", "create")
                .with(csrf().asHeader())
        )
                .hasStatus(HttpStatus.NO_CONTENT);
    }


    @Test
    void relationRoles() {
        when(groupRolesService.relation(anyLong(), anySet())).thenReturn(List.of(mock(GroupRoles.class)));

        assertThat(mvc.patch().uri("/groups/{id}/roles", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Set.of(1L)))
                .with(csrf().asHeader())
        )
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(GroupRoles.class))
                .hasSize(1);
    }

    @Test
    void relationRoles_error() {
        doThrow(new RuntimeException()).when(groupPrivilegesService).relation(anyLong(), anyLong(), anyString());

        assertThat(mvc.patch().uri("/groups/{id}/roles", 1L)
                .queryParam("action", "create")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
        )
                .hasStatus5xxServerError();
    }

    @Test
    void roles() {
        when(groupRolesService.roles(anyLong())).thenReturn(List.of(mock(GroupRoles.class)));

        assertThat(mvc.get().uri("/groups/{id}/roles", 1L))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(GroupRoles.class))
                .hasSize(1);
    }

    @Test
    void roles_error() {
        doThrow(new RuntimeException()).when(groupRolesService).roles(anyLong());

        assertThat(mvc.get().uri("/groups/{id}/roles", anyLong()))
                .hasStatus5xxServerError();
    }

    @Test
    void removeRoles() {
        groupRolesService.removeRelation(anyLong(), anySet());

        assertThat(mvc.delete().uri("/groups/{id}/roles", 1L)
                .queryParam("roleIds", "1,2,3")
                .with(csrf().asHeader())
        )
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void importFromFile() {
        when(groupService.createAll(anyList())).thenReturn(List.of(vo));

        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[1]);
        assertThat(mvc.post().uri("/groups/import").multipart().file(file).with(csrf().asHeader()))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(GroupVO.class))
                .hasSize(1)
                .element(0).satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

}