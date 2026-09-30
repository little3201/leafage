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
import top.leafage.hypervisor.docs.domain.Template;
import top.leafage.hypervisor.docs.domain.dto.TemplateDTO;
import top.leafage.hypervisor.docs.domain.vo.TemplateVO;
import top.leafage.hypervisor.docs.service.TemplateService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static top.leafage.hypervisor.ImportTestUtils.createMinimalXlsxBytes;

/**
 * Template controller test
 *
 * @author wq li
 **/
@WithMockUser
@WebMvcTest(TemplateController.class)
class TemplateControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private TemplateService templateService;

    private TemplateDTO dto;
    private TemplateVO vo;

    @BeforeEach
    void setUp() {
        dto = new TemplateDTO();
        dto.setName("test");
        dto.setType(Template.Type.WORD);

        vo = new TemplateVO(1L, "test", Template.Type.WORD, 0, Template.Status.ARCHIVED, true, null);
    }

    @Test
    void retrieve() {
        Page<TemplateVO> page = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);
        when(templateService.retrieve(anyInt(), anyInt(), anyString(), anyBoolean(), anyString())).thenReturn(page);

        assertThat(mvc.get().uri("/templates")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "false")
                .queryParam("filters", "name:like:test"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.content")
                .convertTo(InstanceOfAssertFactories.list(TemplateVO.class))
                .hasSize(1)
                .element(0).satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

    @Test
    void fetch() {
        when(templateService.fetch(anyLong())).thenReturn(vo);

        assertThat(mvc.get().uri("/templates/{id}", 1L))
                .hasStatusOk()
                .bodyJson()
                .convertTo(TemplateVO.class)
                .satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

    @Test
    void create() {
        when(templateService.create(any(TemplateDTO.class))).thenReturn(vo);

        assertThat(mvc.post().uri("/templates").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(TemplateVO.class)
                .satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

    @Test
    void modify() {
        when(templateService.modify(anyLong(), any(TemplateDTO.class))).thenReturn(vo);

        assertThat(mvc.put().uri("/templates/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus(HttpStatus.ACCEPTED);
    }

    @Test
    void remove() {
        assertThat(mvc.delete().uri("/templates/{id}", 1L).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void enable() {
        when(templateService.enable(anyLong())).thenReturn(true);

        assertThat(mvc.patch().uri("/templates/{id}/enable", 1L).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void disable() {
        when(templateService.disable(anyLong())).thenReturn(true);

        assertThat(mvc.patch().uri("/templates/{id}/disable", 1L).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void publish() {
        when(templateService.publish(anyLong())).thenReturn(true);

        assertThat(mvc.patch().uri("/templates/{id}/publish", 1L).with(csrf().asHeader()))
                .hasStatusOk()
                .bodyJson()
                .convertTo(Boolean.class)
                .isEqualTo(true);
    }

    @Test
    void archive() {
        when(templateService.archive(anyLong())).thenReturn(true);

        assertThat(mvc.patch().uri("/templates/{id}/archive", 1L).with(csrf().asHeader()))
                .hasStatusOk()
                .bodyJson()
                .convertTo(Boolean.class)
                .isEqualTo(true);
    }

    @Test
    void importFromFile() {
        when(templateService.createAll(anyList())).thenReturn(List.of(vo));

        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", createMinimalXlsxBytes());
        assertThat(mvc.post().uri("/templates/import").multipart().file(file).with(csrf().asHeader()))
                .hasStatusOk();
    }
}
