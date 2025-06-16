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

import io.leafage.hypervisor.service.SchedulerLogService;
import io.leafage.hypervisor.vo.SchedulerLogVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * scheduler log controller test
 *
 * @author wq li
 **/
@WithMockUser
@ExtendWith(SpringExtension.class)
@WebMvcTest(SchedulerLogController.class)
class SchedulerLogControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private SchedulerLogService schedulerLogService;

    private SchedulerLogVO vo;

    @BeforeEach
    void setUp() {
        vo = new SchedulerLogVO();
        vo.setId(1L);
        vo.setName("test");
        vo.setExecutedTimes(200);
        vo.setStartTime(Instant.now());
        vo.setNextExecuteTime(Instant.now().plus(12, ChronoUnit.HOURS));
    }

    @Test
    void retrieve() throws Exception {
        Page<SchedulerLogVO> voPage = new PageImpl<>(List.of(vo), Mockito.mock(PageRequest.class), 2L);

        given(this.schedulerLogService.retrieve(Mockito.anyInt(), Mockito.anyInt(), eq("id"),
                Mockito.anyBoolean(), Mockito.anyString())).willReturn(voPage);

        mvc.perform(get("/scheduler-logs")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "true")
                        .queryParam("filters", "scheduler:like:a")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andDo(print())
                .andReturn();
    }

    @Test
    void retrieve_error() throws Exception {
        given(this.schedulerLogService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyBoolean(), Mockito.anyString())).willThrow(new RuntimeException());

        mvc.perform(get("/scheduler-logs")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "true")
                        .queryParam("filters", "scheduler:like:a")
                )
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn();
    }

    @Test
    void fetch() throws Exception {
        given(this.schedulerLogService.fetch(Mockito.anyLong())).willReturn(vo);

        mvc.perform(get("/scheduler-logs/{id}", Mockito.anyLong())).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test")).andDo(print()).andReturn();
    }

    @Test
    void fetch_error() throws Exception {
        given(this.schedulerLogService.fetch(Mockito.anyLong())).willThrow(new RuntimeException());

        mvc.perform(get("/scheduler-logs/{id}", Mockito.anyLong())).andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void remove() throws Exception {
        this.schedulerLogService.remove(Mockito.anyLong());

        mvc.perform(delete("/scheduler-logs/{id}", Mockito.anyLong()).with(csrf().asHeader())).andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void remove_error() throws Exception {
        doThrow(new RuntimeException()).when(this.schedulerLogService).remove(Mockito.anyLong());

        mvc.perform(delete("/scheduler-logs/{id}", Mockito.anyLong()).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

    @Test
    void clear() throws Exception {
        this.schedulerLogService.clear();

        mvc.perform(delete("/scheduler-logs").with(csrf().asHeader())).andExpect(status().isOk())
                .andDo(print()).andReturn();
    }

    @Test
    void clear_error() throws Exception {
        doThrow(new RuntimeException()).when(this.schedulerLogService).clear();

        mvc.perform(delete("/scheduler-logs").with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }

}