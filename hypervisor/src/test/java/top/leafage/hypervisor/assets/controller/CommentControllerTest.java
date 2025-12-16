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

package top.leafage.hypervisor.assets.controller;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;
import top.leafage.hypervisor.assets.domain.dto.CommentDTO;
import top.leafage.hypervisor.assets.domain.vo.CommentVO;
import top.leafage.hypervisor.assets.service.CommentService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/**
 * Comment 接口测试
 *
 * @author wq li
 **/
@WithMockUser
@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private CommentService commentService;

    private CommentDTO dto;
    private CommentVO vo;

    @BeforeEach
    void setUp() {
        dto = new CommentDTO();
        dto.setPostId(1L);
        dto.setBody("body");
        dto.setReplier(1L);

        vo = new CommentVO(1L, 2L, "test", 1L, 0);
    }

    @Test
    void retrieve() {
        Page<@NonNull CommentVO> page = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        when(commentService.retrieve(anyInt(), anyInt(), eq("id"), anyBoolean(), anyString())).thenReturn(page);

        assertThat(mvc.get().uri("/comments")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "false")
                .queryParam("filters", "name:like:test")
        )
                .hasStatusOk()
                .bodyJson().extractingPath("$.content")
                .convertTo(InstanceOfAssertFactories.list(CommentVO.class))
                .hasSize(1)
                .element(0).satisfies(vo -> assertThat(vo.body()).isEqualTo("test"));
    }

    @Test
    void retrieve_error() {
        when(commentService.retrieve(anyInt(), anyInt(), anyString(), anyBoolean(), anyString())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/comments")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "true")
                .queryParam("filters", "name:like:test")
        )
                .hasStatus5xxServerError();
    }

    @Test
    void relation() {
        when(commentService.relation(anyLong())).thenReturn(List.of(vo));

        assertThat(mvc.get().uri("/comments/{id}", anyLong()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void relation_error() {
        when(commentService.relation(anyLong())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/comments/{id}", anyLong()))
                .hasStatus5xxServerError();
    }

    @Test
    void replies() {
        when(commentService.replies(anyLong())).thenReturn(List.of(vo));

        assertThat(mvc.get().uri("/comments/{id}/replies", 1L))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void replies_error() {
        when(commentService.replies(anyLong())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/comments/{id}/replies", 1L))
                .hasStatus5xxServerError();
    }

    @Test
    void create() {
        when(commentService.create(any(CommentDTO.class))).thenReturn(vo);

        assertThat(mvc.post().uri("/comments").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(CommentVO.class)
                .satisfies(vo -> assertThat(vo.body()).isEqualTo("test"));
    }

    @Test
    void create_error() {
        when(commentService.create(any(CommentDTO.class))).thenThrow(new RuntimeException());

        assertThat(mvc.post().uri("/comments").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }
}