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

package top.leafage.hypervisor.docs.controller;

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
import tools.jackson.databind.ObjectMapper;
import top.leafage.hypervisor.docs.domain.dto.ReportDTO;
import top.leafage.hypervisor.docs.domain.vo.ReportVO;
import top.leafage.hypervisor.docs.service.ReportService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static top.leafage.hypervisor.ImportTestUtils.createMinimalXlsxBytes;

/**
 * Report controller test
 *
 * @author wq li
 **/
@WithMockUser
@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private ReportService reportService;

    private ReportDTO dto;
    private ReportVO vo;

    @BeforeEach
    void setUp() {
        dto = new ReportDTO();
        dto.setTitle("test");
        dto.setSchemaId(1L);
        dto.setBody("body");

        vo = new ReportVO(1L, "test", 1L, "REPORT", 0, "owner", null);
    }

    @Test
    void retrieve() {
        Page<ReportVO> page = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);
        when(reportService.retrieve(anyInt(), anyInt(), anyString(), anyBoolean(), anyString())).thenReturn(page);

        assertThat(mvc.get().uri("/reports")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "false")
                .queryParam("filters", "title:like:test"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.content")
                .convertTo(InstanceOfAssertFactories.list(ReportVO.class))
                .hasSize(1)
                .element(0).satisfies(vo -> assertThat(vo.title()).isEqualTo("test"));
    }

    @Test
    void fetch() {
        when(reportService.fetch(anyLong())).thenReturn(vo);

        assertThat(mvc.get().uri("/reports/{id}", 1L))
                .hasStatusOk()
                .bodyJson()
                .convertTo(ReportVO.class)
                .satisfies(vo -> assertThat(vo.title()).isEqualTo("test"));
    }

    @Test
    void create() {
        when(reportService.create(any(ReportDTO.class))).thenReturn(vo);

        assertThat(mvc.post().uri("/reports").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus(HttpStatus.CREATED);
    }

    @Test
    void modify() {
        when(reportService.modify(anyLong(), any(ReportDTO.class))).thenReturn(vo);

        assertThat(mvc.put().uri("/reports/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus(HttpStatus.ACCEPTED);
    }

    @Test
    void remove() {
        assertThat(mvc.delete().uri("/reports/{id}", 1L).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void importFromFile() {
        when(reportService.createAll(anyList())).thenReturn(List.of(vo));

        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", createMinimalXlsxBytes());
        assertThat(mvc.post().uri("/reports/import").multipart().file(file).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void generate() {
        when(reportService.generate(anyLong())).thenReturn(new byte[]{1, 2});

        assertThat(mvc.get().uri("/reports/{id}/generate", 1L))
                .hasStatusOk();
    }

    @Test
    void preview() {
        when(reportService.preview(anyLong())).thenReturn("preview");

        assertThat(mvc.get().uri("/reports/{id}/preview", 1L))
                .hasStatusOk()
                .bodyText().isEqualTo("preview");
    }
}
