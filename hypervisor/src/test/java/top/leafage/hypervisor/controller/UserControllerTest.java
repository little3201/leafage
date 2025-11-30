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
import top.leafage.hypervisor.domain.dto.UserDTO;
import top.leafage.hypervisor.domain.vo.UserVO;
import top.leafage.hypervisor.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/**
 * user controller test
 *
 * @author wq li
 **/
@WithMockUser
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private UserService userService;

    private UserVO vo;
    private UserDTO dto;

    @BeforeEach
    void setUp() {
        vo = new UserVO(1L, "test", true, true, true, true);

        dto = new UserDTO();
        dto.setUsername("test");
        dto.setName("John");
        dto.setEmail("steven@demo.com");
    }

    @Test
    void retrieve() {
        Page<@NonNull UserVO> voPage = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        when(userService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).thenReturn(voPage);

        assertThat(mvc.get().uri("/users")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "true")
                .queryParam("filters", "username:like:test")
        )
                .hasStatusOk()
                .bodyJson().extractingPath("$.content")
                .convertTo(InstanceOfAssertFactories.list(UserVO.class))
                .hasSize(1)
                .element(0).satisfies(vo -> assertThat(vo.username()).isEqualTo("test"));

        verify(userService).retrieve(anyInt(), anyInt(), anyString(), anyBoolean(), anyString());
    }

    @Test
    void retrieve_error() {
        when(userService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/users")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "true")
                .queryParam("filters", "scheduler:like:test")
        )
                .hasStatus5xxServerError();
    }

    @Test
    void fetch() {
        when(userService.fetch(anyLong())).thenReturn(vo);

        assertThat(mvc.get().uri("/users/{id}", anyLong()))
                .hasStatusOk()
                .bodyJson()
                .convertTo(UserVO.class)
                .satisfies(vo -> assertThat(vo.username()).isEqualTo("test"));
    }

    @Test
    void fetch_error() {
        when(userService.fetch(anyLong())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/users/{id}", anyLong()))
                .hasStatus5xxServerError();
    }

    @Test
    void create() {
        when(userService.create(any(UserDTO.class))).thenReturn(vo);

        assertThat(mvc.post().uri("/users").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader())
        )
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(UserVO.class)
                .satisfies(vo -> assertThat(vo.username()).isEqualTo("test"));
    }

    @Test
    void modify() {
        when(userService.modify(anyLong(), any(UserDTO.class))).thenReturn(vo);

        assertThat(mvc.put().uri("/users/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus(HttpStatus.ACCEPTED)
                .bodyJson()
                .convertTo(UserVO.class)
                .satisfies(vo -> assertThat(vo.username()).isEqualTo("test"));
    }

    @Test
    void modify_error() {
        when(userService.modify(anyLong(), any(UserDTO.class))).thenThrow(new RuntimeException());

        assertThat(mvc.put().uri("/users/{id}", anyLong()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

    @Test
    void remove() {
        this.userService.remove(anyLong());

        assertThat(mvc.delete().uri("/users/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void remove_error() {
        doThrow(new RuntimeException()).when(userService).remove(anyLong());

        assertThat(mvc.delete().uri("/users/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

    @Test
    void enable() {
        when(userService.enable(anyLong())).thenReturn(true);

        assertThat(mvc.patch().uri("/users/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void enable_error() {
        when(userService.enable(anyLong())).thenThrow(new RuntimeException());

        assertThat(mvc.patch().uri("/users/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

    @Test
    void unlock() {
        when(userService.unlock(anyLong())).thenReturn(true);

        assertThat(mvc.patch().uri("/users/{id}/unlock", anyLong()).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void unlock_error() {
        when(userService.unlock(anyLong())).thenThrow(new RuntimeException());

        assertThat(mvc.patch().uri("/users/{id}/unlock", anyLong()).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

    @Test
    void importFromFile() {
        when(userService.createAll(anyList())).thenReturn(List.of(vo));

        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[1]);
        assertThat(mvc.post().uri("/users/import").multipart().file(file).with(csrf().asHeader()))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(UserVO.class))
                .hasSize(1)
                .element(0).satisfies(vo -> assertThat(vo.username()).isEqualTo("test"));
    }
}