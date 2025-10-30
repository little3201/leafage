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

package io.leafage.hypervisor.controller;

import io.leafage.hypervisor.service.AccessLogService;
import io.leafage.hypervisor.vo.AccessLogVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

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
 * access log controller test
 *
 * @author wq li
 **/
@WithMockUser
@ExtendWith(SpringExtension.class)
@WebMvcTest(AccessLogController.class)
class AccessLogControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private AccessLogService accessLogService;

    private AccessLogVO vo;

    @BeforeEach
    void setUp() throws UnknownHostException {
        vo = new AccessLogVO();
        vo.setId(1L);
        vo.setIp(InetAddress.getByName("12.1.3.2"));
        vo.setLocation("test");
        vo.setHttpMethod("POST");
        vo.setResponseTimes(232L);
        vo.setResponseMessage("sessionId");
        vo.setStatusCode(200);
        vo.setUrl("test");
        vo.setParams("xxx");
    }

    @Test
    void retrieve() throws Exception {
        Page<AccessLogVO> voPage = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        given(this.accessLogService.retrieve(anyInt(), anyInt(), eq("id"),
                anyBoolean(), anyString())).willReturn(voPage);

        mvc.perform(get("/access-logs")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                        .queryParam("filters", "url:like:test")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andDo(print())
                .andReturn();
    }

    @Test
    void retrieve_error() throws Exception {
        given(this.accessLogService.retrieve(anyInt(), anyInt(), eq("id"),
                anyBoolean(), anyString())).willThrow(new RuntimeException());

        mvc.perform(get("/access-logs")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                        .queryParam("filters", "url:like:test")
                )
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn();
    }

    @Test
    void fetch() throws Exception {
        given(this.accessLogService.fetch(anyLong())).willReturn(vo);

        mvc.perform(get("/access-logs/{id}", anyLong())).andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("test")).andDo(print()).andReturn();
    }

    @Test
    void fetch_error() throws Exception {
        given(this.accessLogService.fetch(anyLong())).willThrow(new RuntimeException());

        mvc.perform(get("/access-logs/{id}", anyLong())).andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void remove() throws Exception {
        this.accessLogService.remove(anyLong());

        mvc.perform(delete("/access-logs/{id}", anyLong()).with(csrf().asHeader())).andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void remove_error() throws Exception {
        doThrow(new RuntimeException()).when(this.accessLogService).remove(anyLong());

        mvc.perform(delete("/access-logs/{id}", anyLong()).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

    @Test
    void clear() throws Exception {
        this.accessLogService.clear();

        mvc.perform(delete("/access-logs").with(csrf().asHeader())).andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void clear_error() throws Exception {
        doThrow(new RuntimeException()).when(this.accessLogService).clear();

        mvc.perform(delete("/access-logs").with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

}