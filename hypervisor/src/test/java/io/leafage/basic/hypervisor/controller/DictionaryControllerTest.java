/*
 *  Copyright 2018-2024 little3201.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.leafage.basic.hypervisor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.leafage.basic.hypervisor.dto.DictionaryDTO;
import io.leafage.basic.hypervisor.service.DictionaryService;
import io.leafage.basic.hypervisor.vo.DictionaryVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * dictionary controller test
 *
 * @author wq li 2022-04-07 9:19
 **/
@WithMockUser
@ExtendWith(SpringExtension.class)
@WebMvcTest(DictionaryController.class)
class DictionaryControllerTest {


    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private DictionaryService dictionaryService;

    private DictionaryVO dictionaryVO;

    private DictionaryDTO dictionaryDTO;

    @BeforeEach
    void init() {
        dictionaryVO = new DictionaryVO();
        dictionaryVO.setName("gender");
        dictionaryVO.setSuperior("superior");
        dictionaryVO.setDescription("description");

        dictionaryDTO = new DictionaryDTO();
        dictionaryDTO.setName("gender");
        dictionaryDTO.setSuperiorId(1L);
        dictionaryDTO.setDescription("description");
    }

    @Test
    void retrieve() throws Exception {
        Pageable pageable = PageRequest.of(0,2);
        Page<DictionaryVO> voPage = new PageImpl<>(List.of(dictionaryVO), pageable, 2L);
        given(this.dictionaryService.retrieve(Mockito.anyInt(), Mockito.anyInt())).willReturn(voPage);

        mvc.perform(get("/dictionaries").queryParam("page", "0").queryParam("size", "2")
                        .queryParam("sort", "")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty()).andDo(print()).andReturn();
    }

    @Test
    void retrieve_error() throws Exception {
        given(this.dictionaryService.retrieve(Mockito.anyInt(), Mockito.anyInt())).willThrow(new RuntimeException());

        mvc.perform(get("/dictionaries").queryParam("page", "0").queryParam("size", "2")
                .queryParam("sort", "")).andExpect(status().isNoContent()).andDo(print()).andReturn();
    }

    @Test
    void fetch() throws Exception {
        given(this.dictionaryService.fetch(Mockito.anyLong())).willReturn(dictionaryVO);

        mvc.perform(get("/dictionaries/{id}", Mockito.anyLong())).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("gender")).andDo(print()).andReturn();
    }

    @Test
    void fetch_error() throws Exception {
        given(this.dictionaryService.fetch(Mockito.anyLong())).willThrow(new RuntimeException());

        mvc.perform(get("/dictionaries/{id}", Mockito.anyLong())).andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void exist() throws Exception {
        given(this.dictionaryService.exist(Mockito.anyString())).willReturn(true);

        mvc.perform(get("/dictionaries/{name}/exist", "gender")).andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void exist_error() throws Exception {
        given(this.dictionaryService.exist(Mockito.anyString())).willThrow(new RuntimeException());

        mvc.perform(get("/dictionaries/{name}/exist", "gender")).andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }


    @Test
    void lower() throws Exception {
        given(this.dictionaryService.lower(Mockito.anyLong())).willReturn(List.of(dictionaryVO));

        mvc.perform(get("/dictionaries/{id}/lower", Mockito.anyLong())).andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void lower_error() throws Exception {
        given(this.dictionaryService.lower(Mockito.anyLong())).willThrow(new RuntimeException());

        mvc.perform(get("/dictionaries/{id}/lower", Mockito.anyLong())).andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void create() throws Exception {
        given(this.dictionaryService.create(Mockito.any(DictionaryDTO.class))).willReturn(dictionaryVO);

        mvc.perform(post("/dictionaries").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dictionaryDTO)).with(csrf().asHeader()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("gender"))
                .andDo(print()).andReturn();
    }

    @Test
    void create_error() throws Exception {
        given(this.dictionaryService.create(Mockito.any(DictionaryDTO.class))).willThrow(new RuntimeException());

        mvc.perform(post("/dictionaries").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dictionaryDTO)).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }
}