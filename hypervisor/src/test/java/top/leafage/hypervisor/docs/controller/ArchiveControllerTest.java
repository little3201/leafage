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
import top.leafage.hypervisor.docs.domain.dto.ArchiveDTO;
import top.leafage.hypervisor.docs.domain.vo.ArchiveVO;
import top.leafage.hypervisor.docs.service.ArchiveService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static top.leafage.hypervisor.ImportTestUtils.createMinimalXlsxBytes;

/**
 * Archive controller test
 *
 * @author wq li
 **/
@WithMockUser
@WebMvcTest(ArchiveController.class)
class ArchiveControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private ArchiveService archiveService;

    private ArchiveDTO dto;
    private ArchiveVO vo;

    @BeforeEach
    void setUp() {
        dto = new ArchiveDTO();
        dto.setTitle("test");
        dto.setSchemaId(1L);
        dto.setBody("body");

        vo = new ArchiveVO(1L, "test", 1L, "ARCHIVE", 1, "owner", null);
    }

    @Test
    void retrieve() {
        Page<ArchiveVO> voPage = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        // 使用 eq() 准确匹配参数
        when(archiveService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).thenReturn(voPage);

        // 调用接口并验证结果
        assertThat(mvc.get().uri("/archives")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "false")
                .queryParam("filters", "title:like:test")
        )
                .hasStatusOk()

                .bodyJson().extractingPath("$.content")
                .convertTo(InstanceOfAssertFactories.list(ArchiveVO.class))
                .hasSize(1)
                .element(0).satisfies(vo -> assertThat(vo.title()).isEqualTo("test"));
    }

    @Test
    void fetch() {
        when(archiveService.fetch(anyLong())).thenReturn(vo);

        assertThat(mvc.get().uri("/archives/{id}", 1L))
                .hasStatusOk()
                .bodyJson()
                .convertTo(ArchiveVO.class)
                .satisfies(vo -> assertThat(vo.title()).isEqualTo("test"));
    }

    @Test
    void create() {
        when(archiveService.create(any(ArchiveDTO.class))).thenReturn(vo);

        assertThat(mvc.post().uri("/archives").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(ArchiveVO.class)
                .satisfies(vo -> assertThat(vo.title()).isEqualTo("test"));
    }

    @Test
    void modify() {
        when(archiveService.modify(anyLong(), any(ArchiveDTO.class))).thenReturn(vo);

        assertThat(mvc.put().uri("/archives/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus(HttpStatus.ACCEPTED)
                .bodyJson()
                .convertTo(ArchiveVO.class)
                .satisfies(vo -> assertThat(vo.title()).isEqualTo("test"));
    }

    @Test
    void remove() {
        archiveService.remove(anyLong());

        assertThat(mvc.delete().uri("/archives/{id}", 1L).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void importFromFile() {
        when(archiveService.createAll(anyList())).thenReturn(List.of(vo));

        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", createMinimalXlsxBytes());
        assertThat(mvc.post().uri("/archives/import").multipart().file(file).with(csrf().asHeader()))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(ArchiveVO.class))
                .hasSize(1)
                .element(0).satisfies(vo -> assertThat(vo.title()).isEqualTo("test"));
    }
}
