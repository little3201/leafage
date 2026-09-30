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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;
import top.leafage.common.data.core.domain.TreeNode;
import top.leafage.hypervisor.docs.domain.Section;
import top.leafage.hypervisor.docs.domain.SectionField;
import top.leafage.hypervisor.docs.domain.dto.SectionDTO;
import top.leafage.hypervisor.docs.domain.dto.SectionDataDTO;
import top.leafage.hypervisor.docs.domain.dto.SectionFieldDTO;
import top.leafage.hypervisor.docs.domain.vo.SectionDataVO;
import top.leafage.hypervisor.docs.domain.vo.SectionFieldVO;
import top.leafage.hypervisor.docs.domain.vo.SectionVO;
import top.leafage.hypervisor.docs.service.SectionService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/**
 * Section controller test
 *
 * @author wq li
 **/
@WithMockUser
@WebMvcTest(SectionController.class)
class SectionControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private SectionService sectionService;

    private SectionDTO dto;
    private SectionVO vo;
    private SectionFieldDTO fieldDTO;
    private SectionDataDTO dataDTO;
    private SectionFieldVO fieldVO;
    private SectionDataVO dataVO;

    @BeforeEach
    void setUp() {
        dto = new SectionDTO();
        dto.setName("test");
        dto.setSuperiorId(1L);
        dto.setBody(Collections.emptyMap());
        dto.setOwnerId(1L);
        dto.setOwnerType(Section.OwnerType.TEMPLATE);

        vo = new SectionVO(1L, "test", 1L, 1L, Section.OwnerType.TEMPLATE, 1, 2, null, 2L);

        fieldDTO = new SectionFieldDTO();
        fieldDTO.setSectionId(1L);
        fieldDTO.setName("field name");
        fieldDTO.setField("field");
        fieldDTO.setType(SectionField.Type.STRING);
        fieldDTO.setLength(32);
        fieldDTO.setRequired(true);

        dataDTO = new SectionDataDTO();
        dataDTO.setSectionId(1L);
        dataDTO.setData(Map.of("field", "value"));

        fieldVO = new SectionFieldVO(1L, "field name", "field", "STRING", 32, true);
        dataVO = new SectionDataVO(1L, Map.of("field", "value"));
    }

    @Test
    void fetch() {
        when(sectionService.fetch(anyLong())).thenReturn(vo);

        assertThat(mvc.get().uri("/sections/{id}", anyLong()))
                .hasStatusOk()
                .bodyJson()
                .convertTo(SectionVO.class)
                .satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

    @Test
    void fetch_error() {
        when(sectionService.fetch(anyLong())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/sections/{id}", anyLong()))
                .hasStatus5xxServerError();
    }

    @Test
    void create() {
        when(sectionService.create(any(SectionDTO.class))).thenReturn(vo);

        assertThat(mvc.post().uri("/sections").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(SectionVO.class)
                .satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

    @Test
    void create_error() {
        when(sectionService.create(any(SectionDTO.class))).thenThrow(new RuntimeException());

        assertThat(mvc.post().uri("/sections").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

    @Test
    void modify() {
        when(sectionService.modify(anyLong(), any(SectionDTO.class))).thenReturn(vo);

        assertThat(mvc.put().uri("/sections/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus(HttpStatus.ACCEPTED)
                .bodyJson()
                .convertTo(SectionVO.class)
                .satisfies(vo -> assertThat(vo.name()).isEqualTo("test"));
    }

    @Test
    void modify_error() {
        when(sectionService.modify(anyLong(), any(SectionDTO.class))).thenThrow(new RuntimeException());

        assertThat(mvc.put().uri("/sections/{id}", anyLong()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

    @Test
    void remove() {
        this.sectionService.remove(anyLong());

        assertThat(mvc.delete().uri("/sections/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void remove_error() {
        doThrow(new RuntimeException()).when(sectionService).remove(anyLong());

        assertThat(mvc.delete().uri("/sections/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

    @Test
    void tree() {
        TreeNode<Long> treeNode = TreeNode.withId(1L).name("test").build();
        when(sectionService.tree(anyLong(), any(Section.OwnerType.class))).thenReturn(List.of(treeNode));

        assertThat(mvc.get().uri("/sections/{ownerId}/tree", 1L).queryParam("ownerType", "template"))
                .hasStatusOk()
                .bodyJson().extractingPath("$[0].name").isEqualTo("test");
    }

    @Test
    void fields() {
        when(sectionService.fields(anyLong())).thenReturn(List.of(fieldVO));

        assertThat(mvc.get().uri("/sections/{id}/fields", 1L))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(SectionFieldVO.class))
                .hasSize(1)
                .element(0).satisfies(vo -> assertThat(vo.name()).isEqualTo("field name"));
    }

    @Test
    void datas() {
        when(sectionService.datas(anyLong())).thenReturn(List.of(dataVO));

        assertThat(mvc.get().uri("/sections/{id}/datas", 1L))
                .hasStatusOk()
                .bodyJson()
                .convertTo(InstanceOfAssertFactories.list(SectionDataVO.class))
                .hasSize(1)
                .element(0).satisfies(vo -> assertThat(vo.data()).containsEntry("field", "value"));
    }

    @Test
    void createField() throws Exception {
        when(sectionService.createField(any(SectionFieldDTO.class))).thenReturn(fieldVO);

        assertThat(mvc.post().uri("/sections/fields").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(fieldDTO)).with(csrf().asHeader()))
                .hasStatusOk()
                .bodyJson()
                .convertTo(SectionFieldVO.class)
                .satisfies(vo -> assertThat(vo.field()).isEqualTo("field"));
    }

    @Test
    void createData() throws Exception {
        when(sectionService.createData(any(SectionDataDTO.class))).thenReturn(dataVO);

        assertThat(mvc.post().uri("/sections/datas").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dataDTO)).with(csrf().asHeader()))
                .hasStatusOk()
                .bodyJson()
                .convertTo(SectionDataVO.class)
                .satisfies(vo -> assertThat(vo.data()).containsEntry("field", "value"));
    }

    @Test
    void modifyField() throws Exception {
        when(sectionService.modifyField(anyLong(), any(SectionFieldDTO.class))).thenReturn(fieldVO);

        assertThat(mvc.put().uri("/sections/fields/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(fieldDTO)).with(csrf().asHeader()))
                .hasStatusOk()
                .bodyJson()
                .convertTo(SectionFieldVO.class)
                .satisfies(vo -> assertThat(vo.name()).isEqualTo("field name"));
    }

    @Test
    void modifyData() throws Exception {
        when(sectionService.modifyData(anyLong(), any(SectionDataDTO.class))).thenReturn(dataVO);

        assertThat(mvc.put().uri("/sections/datas/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dataDTO)).with(csrf().asHeader()))
                .hasStatusOk()
                .bodyJson()
                .convertTo(SectionDataVO.class)
                .satisfies(vo -> assertThat(vo.id()).isEqualTo(1L));
    }

    @Test
    void removeField() {
        sectionService.removeField(anyLong());

        assertThat(mvc.delete().uri("/sections/fields/{id}", 1L).with(csrf().asHeader()))
                .hasStatusOk();
    }

    @Test
    void removeData() {
        sectionService.removeData(anyLong());

        assertThat(mvc.delete().uri("/sections/datas/{id}", 1L).with(csrf().asHeader()))
                .hasStatusOk();
    }
}
