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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;
import top.leafage.assets.domain.dto.CommentDTO;
import top.leafage.assets.domain.vo.CommentVO;
import top.leafage.assets.service.CommentService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/**
 * Comment 接口测试
 *
 * @author wq li
 **/
@WithMockUser(roles = "ADMIN")
@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private CommentService commentService;

    private CommentVO vo;

    private CommentDTO dto;

    @BeforeEach
    void setUp() {
        vo = new CommentVO(1L, 2L, "body", 1L, 0);

        dto = new CommentDTO();
        dto.setPostId(1L);
        dto.setBody("content");
        dto.setReplier(1L);
    }

    @Test
    void retrieve() {
        Page<@NonNull CommentVO> page = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        given(commentService.retrieve(anyInt(), anyInt(), eq("id"), anyBoolean(), anyString())).willReturn(page);

        assertThat(this.mvc.get().uri("/comments")
                .queryParam("page", "0")
                .queryParam("size", "2")
        )
                .hasStatusOk()
                .body().isNotNull()
                .hasSize(1);
    }

    @Test
    void retrieve_error() {
        given(commentService.retrieve(anyInt(), anyInt(), anyString(), anyBoolean(), anyString())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/comments")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "true")
                .queryParam("filters", "content:like:a")
        )
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void relation() {
        given(commentService.relation(anyLong())).willReturn(List.of(vo));

        assertThat(this.mvc.get().uri("/comments/{id}", anyLong()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void relation_error() {
        given(commentService.relation(anyLong())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/comments/{id}", anyLong()))
                .hasStatus5xxServerError();
    }

    @Test
    void replies() {
        given(commentService.replies(anyLong())).willReturn(List.of(vo));

        assertThat(this.mvc.get().uri("/comments/{id}/replies", 1L))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void replies_error() {
        given(commentService.replies(anyLong())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/comments/{id}/replies", 1L))
                .hasStatus5xxServerError();
    }

    @Test
    void create() {
        given(commentService.create(any(CommentDTO.class))).willReturn(vo);

        assertThat(this.mvc.post().uri("/comments").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void create_error() {
        given(commentService.create(any(CommentDTO.class))).willThrow(new RuntimeException());

        assertThat(this.mvc.post().uri("/comments").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }
}