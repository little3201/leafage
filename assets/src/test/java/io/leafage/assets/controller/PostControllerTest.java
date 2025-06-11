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
import io.leafage.assets.dto.PostDTO;
import io.leafage.assets.service.PostService;
import io.leafage.assets.vo.PostVO;
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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * posts 接口测试
 *
 * @author wq li
 **/
@WithMockUser
@ExtendWith(SpringExtension.class)
@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mvc;

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
        dto.setTags(Set.of("Code"));
        dto.setTags(Collections.singleton("java"));
        dto.setContent("content");

        vo = new PostVO();
        vo.setId(1L);
        vo.setSummary("excerpt");
        vo.setTitle(dto.getTitle());
        vo.setTags(dto.getTags());

    }

    @Test
    void retrieve() throws Exception {
        Page<PostVO> page = new PageImpl<>(List.of(vo), Mockito.mock(PageRequest.class), 2L);

        given(postService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyBoolean(), Mockito.anyString())).willReturn(page);

        mvc.perform(get("/posts")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                        .queryParam("filters", "title:like:a")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andDo(print()).andReturn();
    }

    @Test
    void retrieve_error() throws Exception {
        given(postService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyBoolean(), Mockito.anyString())).willThrow(new RuntimeException());

        mvc.perform(get("/posts")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                        .queryParam("filters", "title:like:a")
                )
                .andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void fetch() throws Exception {
        given(postService.fetch(Mockito.anyLong())).willReturn(vo);

        mvc.perform(get("/posts/{id}", Mockito.anyLong())).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("test"))
                .andDo(print()).andReturn();
    }

    @Test
    void fetch_error() throws Exception {
        given(postService.fetch(Mockito.anyLong())).willThrow(new RuntimeException());
        mvc.perform(get("/posts/{id}", Mockito.anyLong()))
                .andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void exists() throws Exception {
        given(postService.exists(Mockito.anyString(), Mockito.anyLong())).willReturn(true);

        mvc.perform(get("/posts/exists")
                        .queryParam("title", "test")
                        .queryParam("id", "1"))
                .andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void exist_error() throws Exception {
        given(postService.exists(Mockito.anyString(), Mockito.anyLong())).willThrow(new RuntimeException());

        mvc.perform(get("/posts/exists")
                        .queryParam("title", "test")
                        .queryParam("id", "1"))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

    @Test
    void create() throws Exception {
        given(postService.create(Mockito.any(PostDTO.class))).willReturn(vo);

        mvc.perform(post("/posts").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("test"))
                .andDo(print()).andReturn();
    }

    @Test
    void create_error() throws Exception {
        given(postService.create(Mockito.any(PostDTO.class))).willThrow(new RuntimeException());

        mvc.perform(post("/posts").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

    @Test
    void modify() throws Exception {
        given(postService.modify(Mockito.anyLong(), Mockito.any(PostDTO.class))).willReturn(vo);

        mvc.perform(put("/posts/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isAccepted())
                .andDo(print()).andReturn();
    }

    @Test
    void modify_error() throws Exception {
        given(postService.modify(Mockito.anyLong(), Mockito.any(PostDTO.class))).willThrow(new RuntimeException());

        mvc.perform(put("/posts/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isNotModified())
                .andDo(print()).andReturn();
    }

    @Test
    void remove() throws Exception {
        postService.remove(Mockito.anyLong());
        mvc.perform(delete("/posts/{id}", 1L).with(csrf().asHeader()))
                .andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void remove_error() throws Exception {
        doThrow(new RuntimeException()).when(postService).remove(Mockito.anyLong());
        mvc.perform(delete("/posts/{id}", 1L).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

}
