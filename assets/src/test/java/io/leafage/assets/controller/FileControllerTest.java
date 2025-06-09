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
import io.leafage.assets.service.FileRecordService;
import io.leafage.assets.vo.FileRecordVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

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
 * files 接口测试
 *
 * @author wq li
 **/
@WithMockUser
@ExtendWith(SpringExtension.class)
@WebMvcTest(FileController.class)
class FileControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private FileRecordService fileRecordService;

    private FileRecordVO vo;

    @BeforeEach
    void setUp() {
        vo = new FileRecordVO(1L, true, Instant.now());
        vo.setName("test");
        vo.setSize(21232);
    }

    @Test
    void retrieve() throws Exception {
        Page<FileRecordVO> page = new PageImpl<>(List.of(vo), Mockito.mock(PageRequest.class), 2L);

        given(fileRecordService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyBoolean(), Mockito.anyString())).willReturn(page);

        mvc.perform(get("/files")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "true"))
                .andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void retrieve_error() throws Exception {
        given(fileRecordService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyBoolean(), Mockito.anyString())).willThrow(new RuntimeException());

        mvc.perform(get("/files").queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "true"))
                .andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void fetch() throws Exception {
        given(fileRecordService.fetch(Mockito.anyLong())).willReturn(vo);

        mvc.perform(get("/files/{id}", 1L)).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test")).andDo(print()).andReturn();
    }

    @Test
    void fetch_error() throws Exception {
        given(fileRecordService.fetch(Mockito.anyLong())).willThrow(new RuntimeException());

        mvc.perform(get("/files/{id}", 1L)).andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void upload() throws Exception {
        given(fileRecordService.exists(Mockito.anyString(), Mockito.any())).willReturn(false);
        given(fileRecordService.upload(Mockito.any(MultipartFile.class))).willReturn(vo);

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());
        mvc.perform(multipart("/files").file(file).with(csrf().asHeader()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test"))
                .andDo(print()).andReturn();
    }

    @Test
    void upload_error() throws Exception {
        given(fileRecordService.exists(Mockito.anyString(), Mockito.any())).willReturn(false);

        given(fileRecordService.upload(Mockito.any(MultipartFile.class))).willThrow(new RuntimeException());

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());
        mvc.perform(multipart("/files").file(file).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

    @Test
    void remove() throws Exception {
        fileRecordService.remove(Mockito.anyLong());

        mvc.perform(delete("/files/{id}", Mockito.anyLong()).with(csrf().asHeader()))
                .andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void remove_error() throws Exception {
        doThrow(new RuntimeException()).when(fileRecordService).remove(Mockito.anyLong());

        mvc.perform(delete("/files/{id}", Mockito.anyLong()).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }
}