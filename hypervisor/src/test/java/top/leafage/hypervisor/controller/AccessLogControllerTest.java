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

import org.assertj.core.api.InstanceOfAssertFactories;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import top.leafage.hypervisor.domain.vo.AccessLogVO;
import top.leafage.hypervisor.domain.vo.GroupVO;
import top.leafage.hypervisor.service.AccessLogService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/**
 * access log controller test
 *
 * @author wq li
 **/
@WithMockUser
@WebMvcTest(AccessLogController.class)
class AccessLogControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private AccessLogService accessLogService;

    private AccessLogVO vo;

    @BeforeEach
    void setUp() {
        vo = new AccessLogVO(1L, "test", "POST", "127.0.0.1", "", "", 200, 230L, "");
    }

    @Test
    void retrieve() {
        Page<@NonNull AccessLogVO> voPage = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        when(accessLogService.retrieve(anyInt(), anyInt(), eq("id"),
                anyBoolean(), anyString())).thenReturn(voPage);

        assertThat(mvc.get().uri("/access-logs")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "false")
                .queryParam("filters", "url:like:test")
        )
                .hasStatusOk()
                .bodyJson().extractingPath("$.content")
                .convertTo(InstanceOfAssertFactories.list(AccessLogVO.class))
                .hasSize(1)
                .element(0).satisfies(vo -> assertThat(vo.url()).isEqualTo("test"));

        verify(accessLogService).retrieve(anyInt(), anyInt(), anyString(), anyBoolean(), anyString());
    }

    @Test
    void retrieve_error() {
        when(accessLogService.retrieve(anyInt(), anyInt(), eq("id"),
                anyBoolean(), anyString())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/access-logs")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "false")
                .queryParam("filters", "url:like:test")
        )
                .hasStatus5xxServerError();
    }

    @Test
    void fetch() {
        when(accessLogService.fetch(anyLong())).thenReturn(Mockito.mock(AccessLogVO.class));

        assertThat(mvc.get().uri("/access-logs/{id}", anyLong()))
                .hasStatusOk()
                .body().isNotNull();
    }

    @Test
    void fetch_error() {
        when(accessLogService.fetch(anyLong())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/access-logs/{id}", anyLong()))
                .hasStatus5xxServerError();
    }

    @Test
    void remove() {
        this.accessLogService.remove(anyLong());

        assertThat(mvc.delete().uri("/access-logs/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void remove_error() {
        doThrow(new RuntimeException()).when(accessLogService).remove(anyLong());

        assertThat(mvc.delete().uri("/access-logs/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

    @Test
    void clear() {
        this.accessLogService.clear();

        assertThat(mvc.delete().uri("/access-logs").with(csrf().asHeader()))
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void clear_error() {
        doThrow(new RuntimeException()).when(accessLogService).clear();

        assertThat(mvc.delete().uri("/access-logs").with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

}