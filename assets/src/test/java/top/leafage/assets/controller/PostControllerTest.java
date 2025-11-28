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
package top.leafage.assets.controller;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;
import top.leafage.assets.domain.dto.PostDTO;
import top.leafage.assets.domain.vo.PostVO;
import top.leafage.assets.service.PostService;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/**
 * posts 接口测试
 *
 * @author wq li
 **/
@WithMockUser
@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private PostService postService;

    private PostDTO dto;
    private PostVO vo;

    @BeforeEach
    void setUp() {
        // 构造请求对象
        dto = new PostDTO();
        dto.setTitle("test");
        dto.setBody("body");
        dto.setSummary("summary");
        dto.setTags(Set.of("Code"));

        vo = new PostVO(1L, "test", "summary", "body", Set.of("Code"), Instant.now());
    }

    @Test
    void retrieve() {
        Page<@NonNull PostVO> page = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        when(postService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).thenReturn(page);

        assertThat(mvc.get().uri("/posts")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "false")
                .queryParam("filters", "title:like:a")
        )
                .hasStatusOk()
                .body().isNotNull().hasSize(1);
    }

    @Test
    void retrieve_error() {
        when(postService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/posts")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "false")
                .queryParam("filters", "title:like:a")
        )
                .hasStatus5xxServerError();
    }

    @Test
    void fetch() {
        when(postService.fetch(anyLong())).thenReturn(vo);

        assertThat(mvc.get().uri("/posts/{id}", anyLong()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void fetch_error() {
        when(postService.fetch(anyLong())).thenThrow(new RuntimeException());
        assertThat(mvc.get().uri("/posts/{id}", anyLong()))
                .hasStatus5xxServerError();
    }

    @Test
    void create() {
        when(postService.create(any(PostDTO.class))).thenReturn(vo);

        assertThat(mvc.post().uri("/posts").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void create_error() {
        when(postService.create(any(PostDTO.class))).thenThrow(new RuntimeException());

        assertThat(mvc.post().uri("/posts").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

    @Test
    void modify() {
        when(postService.modify(anyLong(), any(PostDTO.class))).thenReturn(vo);

        assertThat(mvc.put().uri("/posts/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void modify_error() {
        when(postService.modify(anyLong(), any(PostDTO.class))).thenThrow(new RuntimeException());

        assertThat(mvc.put().uri("/posts/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

    @Test
    void remove() {
        postService.remove(anyLong());
        assertThat(mvc.delete().uri("/posts/{id}", 1L).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void remove_error() {
        doThrow(new RuntimeException()).when(postService).remove(anyLong());
        assertThat(mvc.delete().uri("/posts/{id}", 1L).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

}
