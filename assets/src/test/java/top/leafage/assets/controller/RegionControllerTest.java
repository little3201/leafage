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
import top.leafage.assets.domain.dto.RegionDTO;
import top.leafage.assets.domain.vo.RegionVO;
import top.leafage.assets.service.RegionService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/**
 * regions 接口测试
 *
 * @author wq li
 **/
@WithMockUser
@WebMvcTest(RegionController.class)
class RegionControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private RegionService regionService;

    private RegionDTO dto;
    private RegionVO vo;

    @BeforeEach
    void setUp() {
        dto = new RegionDTO();
        dto.setName("test");
        dto.setAreaCode("23234");
        dto.setPostalCode(1212);
        dto.setSuperiorId(1L);
        dto.setDescription("description");

        vo = new RegionVO(1L, "test", "029", 712000, "");
    }

    @Test
    void retrieve() {
        Page<@NonNull RegionVO> voPage = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        // 使用 eq() 准确匹配参数
        when(regionService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).thenReturn(voPage);

        // 调用接口并验证结果
        assertThat(mvc.get().uri("/regions")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "false")
                .queryParam("filters", "name:like:a")
        )
                .hasStatusOk()
                .body().isNotNull().hasSize(1);
    }

    @Test
    void retrieve_error() {
        when(regionService.retrieve(anyInt(), anyInt(), anyString(), anyBoolean(), anyString())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/regions")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "true")
                .queryParam("filters", "content:like:a")
        )
                .hasStatus2xxSuccessful();
    }

    @Test
    void fetch() {
        when(regionService.fetch(anyLong())).thenReturn(vo);

        assertThat(mvc.get().uri("/regions/{id}", anyLong()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void fetch_error() {
        when(regionService.fetch(anyLong())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/regions/{id}", anyLong()))
                .hasStatus2xxSuccessful();
    }

    @Test
    void create() {
        when(regionService.create(any(RegionDTO.class))).thenReturn(vo);

        assertThat(mvc.post().uri("/regions").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void create_error() {
        when(regionService.create(any(RegionDTO.class))).thenThrow(new RuntimeException());

        assertThat(mvc.post().uri("/regions").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus4xxClientError();
    }

    @Test
    void modify() {
        when(regionService.modify(anyLong(), any(RegionDTO.class))).thenReturn(vo);

        assertThat(mvc.put().uri("/regions/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void modify_error() {
        when(regionService.modify(anyLong(), any(RegionDTO.class))).thenThrow(new RuntimeException());

        assertThat(mvc.put().uri("/regions/{id}", anyLong()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus4xxClientError();
    }

    @Test
    void remove() {
        this.regionService.remove(anyLong());

        assertThat(mvc.delete().uri("/regions/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void remove_error() {
        doThrow(new RuntimeException()).when(regionService).remove(anyLong());

        assertThat(mvc.delete().uri("/regions/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatus4xxClientError();
    }
}