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

package top.leafage.hypervisor.controller;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import top.leafage.hypervisor.domain.dto.DictionaryDTO;
import top.leafage.hypervisor.domain.vo.DictionaryVO;
import top.leafage.hypervisor.service.DictionaryService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/**
 * dictionary controller test
 *
 * @author wq li
 **/
@WithMockUser
@WebMvcTest(DictionaryController.class)
class DictionaryControllerTest {


    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private DictionaryService dictionaryService;

    private DictionaryVO vo;

    private DictionaryDTO dto;

    @BeforeEach
    void setUp() {
        vo = new DictionaryVO(1L, "test", null, "desctiption", true);

        dto = new DictionaryDTO();
        dto.setName("gender");
        dto.setSuperiorId(1L);
        dto.setDescription("description");
    }

    @Test
    void retrieve() {
        Page<@NonNull DictionaryVO> voPage = new PageImpl<>(List.of(Mockito.mock(DictionaryVO.class)), mock(PageRequest.class), 2L);

        given(this.dictionaryService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willReturn(voPage);

        assertThat(this.mvc.get().uri("/dictionaries")
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
        given(this.dictionaryService.retrieve(anyInt(), anyInt(), eq("id"),
                anyBoolean(), anyString())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/dictionaries")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "false")
                .queryParam("filters", "name:like:a")
        )
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void subset() {
        given(this.dictionaryService.subset(anyLong())).willReturn(List.of(Mockito.mock(DictionaryVO.class)));

        assertThat(this.mvc.get().uri("/dictionaries/{id}/subset", anyLong()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void fetch() {
        given(this.dictionaryService.fetch(anyLong())).willReturn(Mockito.mock(DictionaryVO.class));

        assertThat(this.mvc.get().uri("/dictionaries/{id}", anyLong()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void fetch_error() {
        given(this.dictionaryService.fetch(anyLong())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/dictionaries/{id}", anyLong()))
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void subset_error() {
        given(this.dictionaryService.subset(anyLong())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/dictionaries/{id}/subset", "1"))
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void create() {
        given(this.dictionaryService.create(any(DictionaryDTO.class))).willReturn(Mockito.mock(DictionaryVO.class));

        assertThat(this.mvc.post().uri("/dictionaries").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void create_error() {
        given(this.dictionaryService.create(any(DictionaryDTO.class))).willThrow(new RuntimeException());

        assertThat(this.mvc.post().uri("/dictionaries").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus4xxClientError();
    }

    @Test
    void modify() {
        given(this.dictionaryService.modify(anyLong(), any(DictionaryDTO.class))).willReturn(Mockito.mock(DictionaryVO.class));

        assertThat(this.mvc.put().uri("/dictionaries/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void modify_error() {
        given(this.dictionaryService.modify(anyLong(), any(DictionaryDTO.class))).willThrow(new RuntimeException());

        assertThat(this.mvc.put().uri("/dictionaries/{id}", anyLong()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus4xxClientError();
    }

    @Test
    void remove() {
        this.dictionaryService.remove(anyLong());

        assertThat(this.mvc.delete().uri("/dictionaries/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void enable() {
        given(this.dictionaryService.enable(anyLong())).willReturn(true);

        assertThat(this.mvc.patch().uri("/dictionaries/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatusOk();
    }

}