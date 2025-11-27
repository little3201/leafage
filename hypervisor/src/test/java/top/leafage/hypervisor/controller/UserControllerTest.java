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
import top.leafage.hypervisor.domain.dto.UserDTO;
import top.leafage.hypervisor.service.UserService;
import top.leafage.hypervisor.domain.vo.UserVO;
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
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        dto = new UserDTO();
        dto.setUsername("test");
        dto.setName("John");
        dto.setAvatar("steven.jpg");

        vo = new UserVO();
        vo.setId(1L);
        vo.setUsername("test");
        vo.setEmail("john@test.com");
    }

    @Test
    void retrieve() throws Exception {
        Page<UserVO> voPage = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        given(this.userService.retrieve(anyInt(), anyInt(), eq("id"),
                anyBoolean(), anyString())).willReturn(voPage);

        assertThat(this.mvc.get().uri("/users")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "true")
                        .queryParam("filters", "username:like:a")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andDo(print())
                .andReturn();
    }

    @Test
    void fetch() throws Exception {
        given(this.userService.fetch(anyLong())).willReturn(vo);

        assertThat(this.mvc.get().uri("/users/{id}", anyLong()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test"))
                .andDo(print())
                .andReturn();
    }

    @Test
    void fetch_error() throws Exception {
        given(this.userService.fetch(anyLong())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/users/{id}", anyLong())).andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void create() throws Exception {
        given(this.userService.create(any(UserDTO.class))).willReturn(vo);

        assertThat(this.mvc.post().uri("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader())).andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("test"))
                .andDo(print()).andReturn();
    }

    @Test
    void modify() throws Exception {
        given(this.userService.modify(anyLong(), any(UserDTO.class))).willReturn(vo);

        assertThat(this.mvc.put().uri("/users/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isAccepted())
                .andDo(print()).andReturn();
    }

    @Test
    void modify_error() throws Exception {
        given(this.userService.modify(anyLong(), any(UserDTO.class))).willThrow(new RuntimeException());

        assertThat(this.mvc.put().uri("/users/{id}", anyLong()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isNotModified())
                .andDo(print()).andReturn();
    }

    @Test
    void enable() throws Exception {
        given(this.userService.enable(anyLong())).willReturn(true);

        assertThat(patch("/users/{id}", anyLong()).with(csrf().asHeader()))
                .andExpect(status().isAccepted())
                .andDo(print()).andReturn();
    }

}