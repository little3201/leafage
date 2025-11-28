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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import top.leafage.hypervisor.domain.vo.OperationLogVO;
import top.leafage.hypervisor.service.OperationLogService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/**
 * operation log controller test
 *
 * @author wq li
 **/
@WithMockUser
@WebMvcTest(OperationLogController.class)
class OperationLogControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private OperationLogService operationLogService;

    private OperationLogVO vo;

    @BeforeEach
    void setUp() {
        vo = new OperationLogVO(1L, "test", "create", "filters=test", "test", "127.0.0.1", "test", "test", 200);
    }

    @Test
    void retrieve() {
        Page<OperationLogVO> voPage = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        when(operationLogService.retrieve(anyInt(), anyInt(), eq("id"),
                anyBoolean(), anyString())).thenReturn(voPage);

        assertThat(mvc.get().uri("/operation-logs")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "true")
                .queryParam("filters", "operation:like:a")
        )
                .hasStatusOk()
                .body().isNotNull().hasSize(1);
    }

    @Test
    void retrieve_error() {
        when(operationLogService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/operation-logs")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "true")
                .queryParam("filters", "operation:like:a")
        )
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void fetch() {
        when(operationLogService.fetch(anyLong())).thenReturn(vo);

        assertThat(mvc.get().uri("/operation-logs/{id}", anyLong()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void fetch_error() {
        when(operationLogService.fetch(anyLong())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/operation-logs/{id}", anyLong()))
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void remove() {
        this.operationLogService.remove(anyLong());

        assertThat(mvc.delete().uri("/operation-logs/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void remove_error() {
        doThrow(new RuntimeException()).when(operationLogService).remove(anyLong());

        assertThat(mvc.delete().uri("/operation-logs/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatus4xxClientError();
    }

    @Test
    void clear() {
        this.operationLogService.clear();

        assertThat(mvc.delete().uri("/operation-logs").with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void clear_error() {
        doThrow(new RuntimeException()).when(operationLogService).clear();

        assertThat(mvc.delete().uri("/operation-logs").with(csrf().asHeader()))
                .hasStatus4xxClientError();
    }

}