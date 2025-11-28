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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import top.leafage.hypervisor.domain.vo.SchedulerLogVO;
import top.leafage.hypervisor.service.SchedulerLogService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/**
 * scheduler log controller test
 *
 * @author wq li
 **/
@WithMockUser
@WebMvcTest(SchedulerLogController.class)
class SchedulerLogControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private SchedulerLogService schedulerLogService;

    private SchedulerLogVO vo;

    @BeforeEach
    void setUp() {
        vo = new SchedulerLogVO(1L, "test", Instant.now(), 232, Instant.now().plus(12, ChronoUnit.HOURS), "description");
    }

    @Test
    void retrieve() {
        Page<@NonNull SchedulerLogVO> voPage = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        given(this.schedulerLogService.retrieve(anyInt(), anyInt(), eq("id"),
                anyBoolean(), anyString())).willReturn(voPage);

        assertThat(this.mvc.get().uri("/scheduler-logs")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "true")
                .queryParam("filters", "scheduler:like:a")
        )
                .hasStatusOk()
                .body().isNotNull().hasSize(1);
    }

    @Test
    void retrieve_error() {
        given(this.schedulerLogService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/scheduler-logs")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "true")
                .queryParam("filters", "scheduler:like:a")
        )
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void fetch() {
        given(this.schedulerLogService.fetch(anyLong())).willReturn(vo);

        assertThat(this.mvc.get().uri("/scheduler-logs/{id}", anyLong()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void fetch_error() {
        given(this.schedulerLogService.fetch(anyLong())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/scheduler-logs/{id}", anyLong()))
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void remove() {
        this.schedulerLogService.remove(anyLong());

        assertThat(this.mvc.delete().uri("/scheduler-logs/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void remove_error() {
        doThrow(new RuntimeException()).when(this.schedulerLogService).remove(anyLong());

        assertThat(this.mvc.delete().uri("/scheduler-logs/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatus4xxClientError();
    }

    @Test
    void clear() {
        this.schedulerLogService.clear();

        assertThat(this.mvc.delete().uri("/scheduler-logs").with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void clear_error() {
        doThrow(new RuntimeException()).when(this.schedulerLogService).clear();

        assertThat(this.mvc.delete().uri("/scheduler-logs").with(csrf().asHeader()))
                .hasStatus4xxClientError();
    }

}