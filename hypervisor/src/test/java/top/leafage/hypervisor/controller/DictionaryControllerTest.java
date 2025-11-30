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

import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;
import top.leafage.hypervisor.domain.dto.DictionaryDTO;
import top.leafage.hypervisor.domain.vo.DictionaryVO;
import top.leafage.hypervisor.domain.vo.GroupVO;
import top.leafage.hypervisor.service.DictionaryService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
        vo = new DictionaryVO(1L, "test", null, "description", true);

        dto = new DictionaryDTO();
        dto.setName("gender");
        dto.setSuperiorId(1L);
        dto.setDescription("description");
    }

    @Test
    void retrieve() {
        Page<@NonNull DictionaryVO> voPage = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        when(dictionaryService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).thenReturn(voPage);

        assertThat(mvc.get().uri("/dictionaries")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "false")
                .queryParam("filters", "name:like:test")
        )
                .hasStatusOk()
                .bodyJson().extractingPath("$.content")
                .convertTo(InstanceOfAssertFactories.list(DictionaryVO.class))
                .hasSize(1)
                .element(0).satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));

        verify(dictionaryService).retrieve(anyInt(), anyInt(), anyString(), anyBoolean(), anyString());
    }

    @Test
    void retrieve_error() {
        when(dictionaryService.retrieve(anyInt(), anyInt(), eq("id"),
                anyBoolean(), anyString())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/dictionaries")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "false")
                .queryParam("filters", "name:like:test")
        )
                .hasStatus5xxServerError();
    }

    @Test
    void subset() {
        when(dictionaryService.subset(anyLong())).thenReturn(List.of(vo));

        assertThat(mvc.get().uri("/dictionaries/{id}/subset", anyLong()))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(DictionaryVO.class))
                .hasSize(1)
                .element(0).satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

    @Test
    void fetch() {
        when(dictionaryService.fetch(anyLong())).thenReturn(vo);

        assertThat(mvc.get().uri("/dictionaries/{id}", anyLong()))
                .hasStatusOk()
                .bodyJson()
                .convertTo(DictionaryVO.class)
                .satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

    @Test
    void fetch_error() {
        when(dictionaryService.fetch(anyLong())).thenThrow(new EntityNotFoundException());

        assertThat(mvc.get().uri("/dictionaries/{id}", anyLong()))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void subset_error() {
        when(dictionaryService.subset(anyLong())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/dictionaries/{id}/subset", "1"))
                .hasStatus5xxServerError();
    }

    @Test
    void create() {
        when(dictionaryService.create(any(DictionaryDTO.class))).thenReturn(vo);

        assertThat(mvc.post().uri("/dictionaries").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(DictionaryVO.class)
                .satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

    @Test
    void create_error() {
        when(dictionaryService.create(any(DictionaryDTO.class))).thenThrow(new RuntimeException());

        assertThat(mvc.post().uri("/dictionaries").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

    @Test
    void modify() {
        when(dictionaryService.modify(anyLong(), any(DictionaryDTO.class))).thenReturn(vo);

        assertThat(mvc.put().uri("/dictionaries/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus(HttpStatus.ACCEPTED)
                .bodyJson()
                .convertTo(DictionaryVO.class)
                .satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

    @Test
    void modify_error() {
        when(dictionaryService.modify(anyLong(), any(DictionaryDTO.class))).thenThrow(new RuntimeException());

        assertThat(mvc.put().uri("/dictionaries/{id}", anyLong()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

    @Test
    void remove() {
        this.dictionaryService.remove(anyLong());

        assertThat(mvc.delete().uri("/dictionaries/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void enable() {
        when(dictionaryService.enable(anyLong())).thenReturn(true);

        assertThat(mvc.patch().uri("/dictionaries/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void importFromFile() {
        when(dictionaryService.createAll(anyList())).thenReturn(List.of(vo));

        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[1]);
        assertThat(mvc.post().uri("/dictionaries/import").multipart().file(file).with(csrf().asHeader()))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(DictionaryVO.class))
                .hasSize(1)
                .element(0).satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

}