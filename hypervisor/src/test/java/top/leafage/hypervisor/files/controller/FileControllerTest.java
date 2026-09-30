/*
 * Copyright(c) 2019-present the original author or authors.
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

package top.leafage.hypervisor.files.controller;

import org.assertj.core.api.InstanceOfAssertFactories;
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
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;
import top.leafage.hypervisor.docs.domain.vo.FileStatisticsVO;
import top.leafage.hypervisor.files.domain.dto.FileRecordDTO;
import top.leafage.hypervisor.files.domain.vo.FileRecordVO;
import top.leafage.hypervisor.files.service.FileRecordService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/**
 * File controller test
 *
 * @author wq li
 **/
@WithMockUser
@WebMvcTest(FileController.class)
class FileControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private FileRecordService fileRecordService;

    private FileRecordVO vo;

    @BeforeEach
    void setUp() {
        vo = new FileRecordVO(1L, "test", ".txt", "src/test/resources/test.txt", "", 3121L, false, true, LocalDateTime.now());
    }

    @Test
    void retrieve() {
        Page<FileRecordVO> page = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        when(fileRecordService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).thenReturn(page);

        assertThat(mvc.get().uri("/files")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "true")
                .queryParam("filters", "name:like:test")
        )
                .hasStatusOk()
                .bodyJson().extractingPath("$.content")
                .convertTo(InstanceOfAssertFactories.list(FileRecordVO.class))
                .hasSize(1)
                .element(0).satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

    @Test
    void retrieve_error() {
        when(fileRecordService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/files")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "true")
                .queryParam("filters", "name:like:test")
        )
                .hasStatus5xxServerError();
    }

    @Test
    void fetch() {
        when(fileRecordService.fetch(anyLong())).thenReturn(vo);

        assertThat(mvc.get().uri("/files/{id}", 1L))
                .hasStatusOk()
                .bodyJson()
                .convertTo(FileRecordVO.class)
                .satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

    @Test
    void fetch_error() {
        when(fileRecordService.fetch(anyLong())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/files/{id}", 1L))
                .hasStatus5xxServerError();
    }

    @Test
    void download() {
        when(fileRecordService.fetch(anyLong())).thenReturn(vo);

        assertThat(mvc.get().uri("/files/{id}/download", 1L))
                .hasStatusOk()
                .contentType().isEqualTo(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }

    @Test
    void download_error() {
        when(fileRecordService.fetch(anyLong())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/files/{id}/download", 1L))
                .hasStatus5xxServerError();
    }

    @Test
    void upload() {
        when(fileRecordService.upload(any(MultipartFile.class), anyLong())).thenReturn(vo);

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());
        assertThat(mvc.post().uri("/files/upload").param("superiorId", "1")
                .multipart().file(file).with(csrf().asHeader()))
                .hasStatusOk()
                .bodyJson()
                .convertTo(FileRecordVO.class)
                .satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

    @Test
    void upload_error() {
        when(fileRecordService.upload(any(MultipartFile.class), anyLong())).thenThrow(new RuntimeException());

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());
        assertThat(mvc.post().uri("/files/upload").param("superiorId", "1")
                .multipart().file(file).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

    @Test
    void statistics() {
        when(fileRecordService.statistics()).thenReturn(List.of(new FileStatisticsVO("Image", 1, 3121L)));

        assertThat(mvc.get().uri("/files/statistics"))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(FileStatisticsVO.class))
                .hasSize(1)
                .element(0).satisfies(vo -> assertThat(vo.key()).isEqualTo("Image"));
    }

    @Test
    void statistics_error() {
        when(fileRecordService.statistics()).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/files/statistics"))
                .hasStatus5xxServerError();
    }

    @Test
    void create() throws Exception {
        FileRecordDTO dto = new FileRecordDTO();
        dto.setName("test");
        dto.setSuperiorId(1L);
        when(fileRecordService.create(any(FileRecordDTO.class))).thenReturn(vo);

        assertThat(mvc.post().uri("/files").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(FileRecordVO.class)
                .satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

    @Test
    void create_error() throws Exception {
        FileRecordDTO dto = new FileRecordDTO();
        dto.setName("test");
        dto.setSuperiorId(1L);
        when(fileRecordService.create(any(FileRecordDTO.class))).thenThrow(new RuntimeException());

        assertThat(mvc.post().uri("/files").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

    @Test
    void enable() {
        when(fileRecordService.enable(anyLong())).thenReturn(true);

        assertThat(mvc.patch().uri("/files/{id}/enable", anyLong()).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void enable_error() {
        when(fileRecordService.enable(anyLong())).thenThrow(new RuntimeException());

        assertThat(mvc.patch().uri("/files/{id}/enable", anyLong()).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

    @Test
    void disable() {
        when(fileRecordService.disable(anyLong())).thenReturn(true);

        assertThat(mvc.patch().uri("/files/{id}/disable", anyLong()).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void disable_error() {
        when(fileRecordService.disable(anyLong())).thenThrow(new RuntimeException());

        assertThat(mvc.patch().uri("/files/{id}/disable", anyLong()).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

    @Test
    void remove() {
        fileRecordService.remove(anyLong());

        assertThat(mvc.delete().uri("/files/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void remove_error() {
        doThrow(new RuntimeException()).when(fileRecordService).remove(anyLong());

        assertThat(mvc.delete().uri("/files/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

}
