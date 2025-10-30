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

package io.leafage.hypervisor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.leafage.hypervisor.dto.DictionaryDTO;
import io.leafage.hypervisor.service.DictionaryService;
import io.leafage.hypervisor.vo.DictionaryVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * dictionary controller test
 *
 * @author wq li
 **/
@WithMockUser
@ExtendWith(SpringExtension.class)
@WebMvcTest(DictionaryController.class)
class DictionaryControllerTest {


    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private DictionaryService dictionaryService;

    private DictionaryVO vo;

    private DictionaryDTO dto;

    @BeforeEach
    void setUp() {
        vo = new DictionaryVO();
        vo.setId(1L);
        vo.setName("gender");
        vo.setDescription("description");

        dto = new DictionaryDTO();
        dto.setName("gender");
        dto.setSuperiorId(1L);
        dto.setDescription("description");
    }

    @Test
    void retrieve() throws Exception {
        Page<DictionaryVO> voPage = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        given(this.dictionaryService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willReturn(voPage);

        mvc.perform(get("/dictionaries")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                        .queryParam("filters", "name:like:a")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andDo(print())
                .andReturn();
    }

    @Test
    void retrieve_error() throws Exception {
        given(this.dictionaryService.retrieve(anyInt(), anyInt(), eq("id"),
                anyBoolean(), anyString())).willThrow(new RuntimeException());

        mvc.perform(get("/dictionaries")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                        .queryParam("filters", "name:like:a")
                )
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn();
    }

    @Test
    void subset() throws Exception {
        given(this.dictionaryService.subset(anyLong())).willReturn(List.of(vo));

        mvc.perform(get("/dictionaries/{id}/subset", anyLong()))
                .andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void fetch() throws Exception {
        given(this.dictionaryService.fetch(anyLong())).willReturn(vo);

        mvc.perform(get("/dictionaries/{id}", anyLong()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("gender"))
                .andDo(print()).andReturn();
    }

    @Test
    void fetch_error() throws Exception {
        given(this.dictionaryService.fetch(anyLong())).willThrow(new RuntimeException());

        mvc.perform(get("/dictionaries/{id}", anyLong()))
                .andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }


    @Test
    void modify() throws Exception {
        given(this.dictionaryService.modify(anyLong(), any(DictionaryDTO.class))).willReturn(vo);

        mvc.perform(put("/dictionaries/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isAccepted())
                .andDo(print()).andReturn();
    }

    @Test
    void modify_error() throws Exception {
        given(this.dictionaryService.modify(anyLong(), any(DictionaryDTO.class))).willThrow(new RuntimeException());

        mvc.perform(put("/dictionaries/{id}", anyLong()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isNotModified())
                .andDo(print()).andReturn();
    }

    @Test
    void remove() throws Exception {
        this.dictionaryService.remove(anyLong());

        mvc.perform(delete("/dictionaries/{id}", anyLong()).with(csrf().asHeader()))
                .andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void enable() throws Exception {
        given(this.dictionaryService.enable(anyLong())).willReturn(true);

        mvc.perform(patch("/dictionaries/{id}", anyLong()).with(csrf().asHeader()))
                .andExpect(status().isAccepted())
                .andDo(print()).andReturn();
    }

    @Test
    void subset_error() throws Exception {
        given(this.dictionaryService.subset(anyLong())).willThrow(new RuntimeException());

        mvc.perform(get("/dictionaries/{id}/subset", "1"))
                .andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void create() throws Exception {
        given(this.dictionaryService.create(any(DictionaryDTO.class))).willReturn(vo);

        mvc.perform(post("/dictionaries").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("gender"))
                .andDo(print()).andReturn();
    }

    @Test
    void create_error() throws Exception {
        given(this.dictionaryService.create(any(DictionaryDTO.class))).willThrow(new RuntimeException());

        mvc.perform(post("/dictionaries").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }
}