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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import top.leafage.hypervisor.domain.vo.AuditLogVO;
import top.leafage.hypervisor.service.AuditLogService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/**
 * audit log controller test
 *
 * @author wq li
 **/
@WithMockUser
@WebMvcTest(AuditLogController.class)
class AuditLogControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private AuditLogService auditLogService;

    private AuditLogVO vo;

    @BeforeEach
    void setUp() throws UnknownHostException {
        vo = new AuditLogVO(1L, "", "", "", "", InetAddress.getByName("12.1.3.2"), 200, 2132L);
    }

    @Test
    void retrieve() {
        Page<@NonNull AuditLogVO> voPage = new PageImpl<>(List.of(Mockito.mock(AuditLogVO.class)), mock(PageRequest.class), 2L);

        given(this.auditLogService.retrieve(anyInt(), anyInt(), eq("id"),
                anyBoolean(), anyString())).willReturn(voPage);

        assertThat(this.mvc.get().uri("/audit-logs")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "false")
                .queryParam("filters", "url:like:test")
        )
                .hasStatusOk()
                .body().isNotNull().hasSize(1);
    }

    @Test
    void retrieve_error() {
        given(this.auditLogService.retrieve(anyInt(), anyInt(), eq("id"),
                anyBoolean(), anyString())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/audit-logs")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "false")
                .queryParam("filters", "url:like:test")
        )
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void fetch() {
        given(this.auditLogService.fetch(anyLong())).willReturn(Mockito.mock(AuditLogVO.class));

        assertThat(this.mvc.get().uri("/audit-logs/{id}", anyLong()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void fetch_error() {
        given(this.auditLogService.fetch(anyLong())).willThrow(new RuntimeException());

        assertThat(this.mvc.get().uri("/audit-logs/{id}", anyLong()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void remove() {
        this.auditLogService.remove(anyLong());

        assertThat(this.mvc.delete().uri("/audit-logs/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void remove_error() {
        doThrow(new RuntimeException()).when(this.auditLogService).remove(anyLong());

        assertThat(this.mvc.delete().uri("/audit-logs/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatusOk()
                .body().isNotNull();
    }

}