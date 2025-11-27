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

import top.leafage.hypervisor.service.OperationLogService;
import top.leafage.hypervisor.domain.vo.OperationLogVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void setUp() throws UnknownHostException {
        vo = new OperationLogVO();
        vo.setId(1L);
        vo.setIp(InetAddress.getByName("127.0.0.1"));
        vo.setLocation("test");
        vo.setBrowser("Chrome");
        vo.setDeviceType("PC");
        vo.setBrowser("Edge");
        vo.setOs("Mac OS");
        vo.setReferer("test");
        vo.setBody("content");
        vo.setSessionId("sessionId");
        vo.setStatusCode(200);
        vo.setOperation("test");
        vo.setUserAgent("xxx");
    }

    @Test
    void retrieve() throws Exception {
        Page<OperationLogVO> voPage = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        given(this.operationLogService.retrieve(anyInt(), anyInt(), eq("id"),
                anyBoolean(), anyString())).willReturn(voPage);

        assertThat(this.mvc.get().uri("/operation-logs")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "true")
                        .queryParam("filters", "operation:like:a")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andDo(print())
                .andReturn();
    }

    @Test
    void retrieve_error() throws Exception {
        given(this.operationLogService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/operation-logs")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "true")
                        .queryParam("filters", "operation:like:a")
                )
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn();
    }

    @Test
    void fetch() throws Exception {
        given(this.operationLogService.fetch(anyLong())).willReturn(vo);

        assertThat(this.mvc.get().uri("/operation-logs/{id}", anyLong())).andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("test")).andDo(print()).andReturn();
    }

    @Test
    void fetch_error() throws Exception {
        given(this.operationLogService.fetch(anyLong())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/operation-logs/{id}", anyLong())).andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void remove() throws Exception {
        this.operationLogService.remove(anyLong());

        assertThat(this.mvc.delete().uri("/operation-logs/{id}", anyLong()).with(csrf().asHeader())).andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void remove_error() throws Exception {
        doThrow(new RuntimeException()).when(this.operationLogService).remove(anyLong());

        assertThat(this.mvc.delete().uri("/operation-logs/{id}", anyLong()).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

    @Test
    void clear() throws Exception {
        this.operationLogService.clear();

        assertThat(this.mvc.delete().uri("/operation-logs").with(csrf().asHeader())).andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void clear_error() throws Exception {
        doThrow(new RuntimeException()).when(this.operationLogService).clear();

        assertThat(this.mvc.delete().uri("/operation-logs").with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

}