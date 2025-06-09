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
import io.leafage.assets.dto.RegionDTO;
import io.leafage.assets.service.RegionService;
import io.leafage.assets.vo.RegionVO;
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

import java.time.Instant;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * regions 接口测试
 *
 * @author wq li
 **/
@WithMockUser
@ExtendWith(SpringExtension.class)
@WebMvcTest(RegionController.class)
class RegionControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private RegionService regionService;

    private RegionVO vo;

    private RegionDTO dto;

    @BeforeEach
    void setUp() {
        vo = new RegionVO(1L, true, Instant.now());
        vo.setName("test");
        vo.setAreaCode("23234");
        vo.setPostalCode(1212);
        vo.setDescription("description");

        dto = new RegionDTO();
        dto.setName("test");
        dto.setAreaCode("23234");
        dto.setPostalCode(1212);
        dto.setSuperiorId(1L);
        dto.setDescription("description");
    }

    @Test
    void retrieve() throws Exception {
        Page<RegionVO> voPage = new PageImpl<>(List.of(vo), Mockito.mock(PageRequest.class), 2L);

        // 使用 eq() 准确匹配参数
        given(this.regionService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyBoolean(), Mockito.anyString())).willReturn(voPage);

        // 调用接口并验证结果
        mvc.perform(get("/regions")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andDo(print())
                .andReturn();
    }

    @Test
    void fetch() throws Exception {
        given(this.regionService.fetch(Mockito.anyLong())).willReturn(vo);

        mvc.perform(get("/regions/{id}", Mockito.anyLong())).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andDo(print()).andReturn();
    }

    @Test
    void fetch_error() throws Exception {
        given(this.regionService.fetch(Mockito.anyLong())).willThrow(new RuntimeException());

        mvc.perform(get("/regions/{id}", Mockito.anyLong()))
                .andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void exists() throws Exception {
        given(this.regionService.exists(Mockito.anyString(), Mockito.anyLong())).willReturn(true);

        mvc.perform(get("/regions/exists")
                        .queryParam("name", "text")
                        .queryParam("id", "1"))
                .andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void exist_error() throws Exception {
        given(this.regionService.exists(Mockito.anyString(), Mockito.anyLong())).willThrow(new RuntimeException());

        mvc.perform(get("/regions/exists", "test")
                        .queryParam("name", "text")
                        .queryParam("id", "1"))
                .andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void create() throws Exception {
        given(this.regionService.create(Mockito.any(RegionDTO.class))).willReturn(vo);

        mvc.perform(post("/regions").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test"))
                .andDo(print()).andReturn();
    }

    @Test
    void create_error() throws Exception {
        given(this.regionService.create(Mockito.any(RegionDTO.class))).willThrow(new RuntimeException());

        mvc.perform(post("/regions").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

    @Test
    void modify() throws Exception {
        given(this.regionService.modify(Mockito.anyLong(), Mockito.any(RegionDTO.class))).willReturn(vo);

        mvc.perform(put("/regions/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isAccepted())
                .andDo(print()).andReturn();
    }

    @Test
    void modify_error() throws Exception {
        given(this.regionService.modify(Mockito.anyLong(), Mockito.any(RegionDTO.class))).willThrow(new RuntimeException());

        mvc.perform(put("/regions/{id}", Mockito.anyLong()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isNotModified())
                .andDo(print()).andReturn();
    }

    @Test
    void remove() throws Exception {
        this.regionService.remove(Mockito.anyLong());

        mvc.perform(delete("/regions/{id}", Mockito.anyLong()).with(csrf().asHeader()))
                .andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void remove_error() throws Exception {
        doThrow(new RuntimeException()).when(this.regionService).remove(Mockito.anyLong());

        mvc.perform(delete("/regions/{id}", Mockito.anyLong()).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }
}