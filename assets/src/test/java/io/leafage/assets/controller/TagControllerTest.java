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
import io.leafage.assets.dto.TagDTO;
import io.leafage.assets.service.TagService;
import io.leafage.assets.vo.TagVO;
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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * tags 接口测试
 *
 * @author wq li
 **/
@WithMockUser
@ExtendWith(SpringExtension.class)
@WebMvcTest(TagController.class)
class TagControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private TagService tagService;

    private TagVO vo;

    private TagDTO dto;

    @BeforeEach
    void setUp() {
        vo = new TagVO();
        vo.setId(1L);
        vo.setName("test");

        dto = new TagDTO();
        dto.setName("test");
    }

    @Test
    void retrieve() throws Exception {
        Page<TagVO> page = new PageImpl<>(List.of(vo), Mockito.mock(PageRequest.class), 2L);

        given(tagService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyBoolean(), Mockito.anyString())).willReturn(page);

        mvc.perform(get("/tags").queryParam("page", "0")
                        .queryParam("size", "2").queryParam("sortBy", "id"))
                .andExpect(status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void retrieve_error() throws Exception {
        given(tagService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyBoolean(), Mockito.anyString())).willThrow(new RuntimeException());

        mvc.perform(get("/tags")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                )
                .andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void fetch() throws Exception {
        given(tagService.fetch(Mockito.anyLong())).willReturn(vo);

        mvc.perform(get("/tags/{id}", 1L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andDo(print()).andReturn();
    }

    @Test
    void fetch_error() throws Exception {
        given(tagService.fetch(Mockito.anyLong())).willThrow(new RuntimeException());

        mvc.perform(get("/tags/{id}", 1L))
                .andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void exists() throws Exception {
        given(this.tagService.exists(Mockito.anyString(), Mockito.anyLong())).willReturn(true);

        mvc.perform(get("/tags/exists")
                        .queryParam("name", "text")
                        .queryParam("id", "1"))
                .andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void exist_error() throws Exception {
        given(this.tagService.exists(Mockito.anyString(), Mockito.anyLong())).willThrow(new RuntimeException());

        mvc.perform(get("/tags/exists", "test")
                        .queryParam("name", "text")
                        .queryParam("id", "1"))
                .andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void create() throws Exception {
        given(tagService.create(Mockito.any(TagDTO.class))).willReturn(vo);

        mvc.perform(post("/tags").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test"))
                .andDo(print()).andReturn();
    }

    @Test
    void create_error() throws Exception {
        given(tagService.create(Mockito.any(TagDTO.class))).willThrow(new RuntimeException());

        mvc.perform(post("/tags").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

    @Test
    void modify() throws Exception {
        given(tagService.modify(Mockito.anyLong(), Mockito.any(TagDTO.class))).willReturn(vo);

        mvc.perform(put("/tags/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isAccepted())
                .andDo(print()).andReturn();
    }

    @Test
    void modify_error() throws Exception {
        given(tagService.modify(Mockito.anyLong(), Mockito.any(TagDTO.class))).willThrow(new RuntimeException());

        mvc.perform(put("/tags/{id}", Mockito.anyLong()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isNotModified())
                .andDo(print()).andReturn();
    }

    @Test
    void remove() throws Exception {
        tagService.remove(Mockito.anyLong());

        mvc.perform(delete("/tags/{id}", Mockito.anyLong()).with(csrf().asHeader()))
                .andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void remove_error() throws Exception {
        doThrow(new RuntimeException()).when(tagService).remove(Mockito.anyLong());

        mvc.perform(delete("/tags/{id}", Mockito.anyLong()).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }
}