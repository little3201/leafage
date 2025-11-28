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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;
import top.leafage.assets.domain.vo.FileRecordVO;
import top.leafage.assets.service.FileRecordService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/**
 * files 接口测试
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
        vo = new FileRecordVO(1L, "test", "", "", 3121.23f);
    }

    @Test
    void retrieve() {
        Page<@NonNull FileRecordVO> page = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        when(fileRecordService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).thenReturn(page);

        assertThat(mvc.get().uri("/files")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "true")
                .queryParam("filters", "name:like:a")
        )
                .hasStatusOk()
                .body().isNotNull().hasSize(1);
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
                .queryParam("filters", "name:like:a")
        )
                .hasStatus5xxServerError();
    }

    @Test
    void fetch() {
        when(fileRecordService.fetch(anyLong())).thenReturn(vo);

        assertThat(mvc.get().uri("/files/{id}", 1L))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void fetch_error() {
        when(fileRecordService.fetch(anyLong())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/files/{id}", 1L))
                .hasStatus5xxServerError();
    }

    @Test
    void upload() {
        when(fileRecordService.exists(anyString(), any())).thenReturn(false);
        when(fileRecordService.upload(any(MultipartFile.class))).thenReturn(vo);

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());
        assertThat(mvc.post().multipart().uri("/files").file(file).with(csrf().asHeader()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void upload_error() {
        when(fileRecordService.exists(anyString(), any())).thenReturn(false);

        when(fileRecordService.upload(any(MultipartFile.class))).thenThrow(new RuntimeException());

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());
        assertThat(mvc.post().multipart().uri("/files").file(file).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

    @Test
    void remove() {
        fileRecordService.remove(anyLong());

        assertThat(mvc.delete().uri("/files/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void remove_error() {
        doThrow(new RuntimeException()).when(fileRecordService).remove(anyLong());

        assertThat(mvc.delete().uri("/files/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }
}