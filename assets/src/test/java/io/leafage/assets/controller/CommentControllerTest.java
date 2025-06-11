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

package io.leafage.assets.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.leafage.assets.dto.CommentDTO;
import io.leafage.assets.service.CommentService;
import io.leafage.assets.vo.CommentVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Comment 接口测试
 *
 * @author wq li
 **/
@WithMockUser
@ExtendWith(SpringExtension.class)
@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private CommentService commentService;

    private CommentVO vo;

    private CommentDTO dto;

    @BeforeEach
    void setUp() {
        vo = new CommentVO();
        vo.setId(1L);
        vo.setContent("content");
        vo.setPostId(1L);

        dto = new CommentDTO();
        dto.setPostId(1L);
        dto.setContent("content");
        dto.setReplier(1L);
    }

    @Test
    void retrieve() throws Exception {
        Page<CommentVO> page = new PageImpl<>(List.of(vo), Mockito.mock(PageRequest.class), 2L);

        given(commentService.retrieve(Mockito.anyInt(), Mockito.anyInt(), eq("id"), Mockito.anyBoolean(), Mockito.anyString())).willReturn(page);

        mvc.perform(get("/comments")
                        .queryParam("page", "0")
                        .queryParam("size", "2"))
                .andExpect(status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void retrieve_error() throws Exception {
        given(commentService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyString())).willThrow(new NoSuchElementException());

        mvc.perform(get("/comments")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "true")
                        .queryParam("filters", "content:like:a")
                )
                .andExpect(status().isNoContent()).andDo(print()).andReturn();
    }

    @Test
    void relation() throws Exception {
        given(commentService.relation(Mockito.anyLong())).willReturn(List.of(vo));

        mvc.perform(get("/comments/{id}", Mockito.anyLong())).andExpect(status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void relation_error() throws Exception {
        given(commentService.relation(Mockito.anyLong())).willThrow(new NoSuchElementException());

        mvc.perform(get("/comments/{id}", Mockito.anyLong())).andExpect(status().isNoContent()).andDo(print()).andReturn();
    }

    @Test
    void replies() throws Exception {
        given(commentService.replies(Mockito.anyLong())).willReturn(List.of(vo));

        mvc.perform(get("/comments/{id}/replies", 1L)).andExpect(status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void replies_error() throws Exception {
        given(commentService.replies(Mockito.anyLong())).willThrow(new NoSuchElementException());

        mvc.perform(get("/comments/{id}/replies", 1L)).andExpect(status().isNoContent()).andDo(print()).andReturn();
    }

    @Test
    void create() throws Exception {
        given(commentService.create(Mockito.any(CommentDTO.class))).willReturn(vo);

        mvc.perform(post("/comments").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader())).andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("content")).andDo(print()).andReturn();
    }

    @Test
    void create_error() throws Exception {
        given(commentService.create(Mockito.any(CommentDTO.class))).willThrow(new NoSuchElementException());

        mvc.perform(post("/comments").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader())).andExpect(status()
                .isExpectationFailed()).andDo(print()).andReturn();
    }
}