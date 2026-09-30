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

package top.leafage.hypervisor.docs.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import top.leafage.common.data.core.domain.TreeNode;
import top.leafage.hypervisor.docs.domain.Section;
import top.leafage.hypervisor.docs.domain.SectionData;
import top.leafage.hypervisor.docs.domain.SectionField;
import top.leafage.hypervisor.docs.domain.dto.SectionDTO;
import top.leafage.hypervisor.docs.domain.dto.SectionDataDTO;
import top.leafage.hypervisor.docs.domain.dto.SectionFieldDTO;
import top.leafage.hypervisor.docs.domain.vo.SectionDataVO;
import top.leafage.hypervisor.docs.domain.vo.SectionFieldVO;
import top.leafage.hypervisor.docs.domain.vo.SectionVO;
import top.leafage.hypervisor.docs.repository.SectionDataRepository;
import top.leafage.hypervisor.docs.repository.SectionFieldRepository;
import top.leafage.hypervisor.docs.repository.SectionRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Section service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class SectionServiceImplTest {

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private SectionFieldRepository sectionFieldRepository;

    @Mock
    private SectionDataRepository sectionDataRepository;

    @InjectMocks
    private SectionServiceImpl sectionService;

    private SectionDTO dto;
    private SectionFieldDTO fieldDTO;
    private SectionDataDTO dataDTO;
    private Section entity;
    private SectionField field;
    private SectionData data;

    @BeforeEach
    void setUp() {
        dto = new SectionDTO();
        dto.setSuperiorId(1L);
        dto.setOwnerId(9L);
        dto.setOwnerType(Section.OwnerType.TEMPLATE);
        dto.setName("test");
        dto.setSequence(1);
        dto.setLevel(2);
        dto.setBody(Map.of("title", "demo"));

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

        entity = SectionDTO.toEntity(dto);
        ReflectionTestUtils.setField(entity, "id", 1L);

        field = SectionFieldDTO.toEntity(fieldDTO);
        ReflectionTestUtils.setField(field, "id", 2L);

        data = SectionDataDTO.toEntity(dataDTO);
        ReflectionTestUtils.setField(data, "id", 3L);
    }

    @Test
    void fetch() {
        when(sectionRepository.findById(1L)).thenReturn(Optional.of(entity));

        SectionVO vo = sectionService.fetch(1L);
        assertEquals(1L, vo.id());
        assertEquals("test", vo.name());
        assertEquals(Map.of("title", "demo"), vo.body());
        verify(sectionRepository).findById(1L);
    }

    @Test
    void fetch_not_found() {
        when(sectionRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> sectionService.fetch(1L));
        assertEquals("section not found: 1", exception.getMessage());
    }

    @Test
    void tree() {
        entity.setSuperiorId(null);
        Section child = new Section(1L, 9L, Section.OwnerType.TEMPLATE, "child", 2, 3, Map.of());
        ReflectionTestUtils.setField(child, "id", 2L);
        when(sectionRepository.findAllByOwnerIdAndOwnerType(9L, Section.OwnerType.TEMPLATE))
                .thenReturn(List.of(entity, child));

        List<TreeNode<Long>> nodes = sectionService.tree(9L, Section.OwnerType.TEMPLATE);
        assertEquals(1, nodes.size());
        assertEquals(1, nodes.getFirst().getChildren().size());
        verify(sectionRepository).findAllByOwnerIdAndOwnerType(9L, Section.OwnerType.TEMPLATE);
    }

    @Test
    void create() {
        when(sectionRepository.existsByOwnerIdAndOwnerTypeAndName(9L, Section.OwnerType.TEMPLATE, "test"))
                .thenReturn(false);
        when(sectionRepository.save(any(Section.class))).thenReturn(entity);

        SectionVO vo = sectionService.create(dto);
        assertEquals("test", vo.name());
        assertEquals(Section.OwnerType.TEMPLATE, vo.ownerType());
        verify(sectionRepository).save(any(Section.class));
    }

    @Test
    void create_duplicate_name() {
        when(sectionRepository.existsByOwnerIdAndOwnerTypeAndName(9L, Section.OwnerType.TEMPLATE, "test"))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sectionService.create(dto));
        assertEquals("name already exists: test", exception.getMessage());
        verify(sectionRepository, never()).save(any(Section.class));
    }

    @Test
    void fields() {
        when(sectionFieldRepository.findAllBySectionId(1L)).thenReturn(List.of(field));

        List<SectionFieldVO> fields = sectionService.fields(1L);
        assertEquals(1, fields.size());
        assertEquals("field name", fields.getFirst().name());
        assertEquals("STRING", fields.getFirst().type());
        verify(sectionFieldRepository).findAllBySectionId(1L);
    }

    @Test
    void datas() {
        when(sectionDataRepository.findAllBySectionId(1L)).thenReturn(List.of(data));

        List<SectionDataVO> datas = sectionService.datas(1L);
        assertEquals(1, datas.size());
        assertEquals(Map.of("field", "value"), datas.getFirst().data());
        verify(sectionDataRepository).findAllBySectionId(1L);
    }

    @Test
    void createField() {
        when(sectionFieldRepository.existsBySectionIdAndName(1L, "field name")).thenReturn(false);
        when(sectionFieldRepository.save(any(SectionField.class))).thenReturn(field);

        SectionFieldVO vo = sectionService.createField(fieldDTO);
        assertEquals("field name", vo.name());
        assertEquals("field", vo.field());
        assertTrue(vo.required());
        verify(sectionFieldRepository).save(any(SectionField.class));
    }

    @Test
    void createField_duplicate_name() {
        when(sectionFieldRepository.existsBySectionIdAndName(1L, "field name")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sectionService.createField(fieldDTO));
        assertEquals("name already exists: field name", exception.getMessage());
        verify(sectionFieldRepository, never()).save(any(SectionField.class));
    }

    @Test
    void createData() {
        when(sectionDataRepository.save(any(SectionData.class))).thenReturn(data);

        SectionDataVO vo = sectionService.createData(dataDTO);
        assertEquals(3L, vo.id());
        assertEquals(Map.of("field", "value"), vo.data());
        verify(sectionDataRepository).save(any(SectionData.class));
    }

    @Test
    void modify() {
        SectionDTO update = new SectionDTO();
        update.setSuperiorId(4L);
        update.setOwnerId(9L);
        update.setOwnerType(Section.OwnerType.TEMPLATE);
        update.setName("updated");
        update.setSequence(5);
        update.setLevel(6);
        update.setBody(Map.of("title", "updated"));
        when(sectionRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(sectionRepository.existsByOwnerIdAndOwnerTypeAndName(9L, Section.OwnerType.TEMPLATE, "updated"))
                .thenReturn(false);
        when(sectionRepository.save(entity)).thenReturn(entity);

        SectionVO vo = sectionService.modify(1L, update);
        assertEquals("updated", vo.name());
        assertEquals(4L, vo.superiorId());
        assertEquals(Map.of("title", "updated"), vo.body());
        verify(sectionRepository).save(entity);
    }

    @Test
    void modify_not_found() {
        when(sectionRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> sectionService.modify(1L, dto));
        assertEquals("section not found: 1", exception.getMessage());
    }

    @Test
    void modify_duplicate_name() {
        dto.setName("updated");
        when(sectionRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(sectionRepository.existsByOwnerIdAndOwnerTypeAndName(9L, Section.OwnerType.TEMPLATE, "updated"))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sectionService.modify(1L, dto));
        assertEquals("name already exists: updated", exception.getMessage());
        verify(sectionRepository, never()).save(any(Section.class));
    }

    @Test
    void modifyField() {
        SectionFieldDTO update = new SectionFieldDTO();
        update.setSectionId(1L);
        update.setName("updated field");
        update.setField("updated");
        update.setType(SectionField.Type.NUMBER);
        update.setLength(10);
        update.setRequired(false);
        when(sectionFieldRepository.findById(2L)).thenReturn(Optional.of(field));
        when(sectionFieldRepository.existsBySectionIdAndName(1L, "updated field")).thenReturn(false);
        when(sectionFieldRepository.save(field)).thenReturn(field);

        SectionFieldVO vo = sectionService.modifyField(2L, update);
        assertEquals("updated field", vo.name());
        assertEquals("NUMBER", vo.type());
        assertFalse(vo.required());
        verify(sectionFieldRepository).save(field);
    }

    @Test
    void modifyField_not_found() {
        when(sectionFieldRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> sectionService.modifyField(2L, fieldDTO));
        assertEquals("section field not found: 2", exception.getMessage());
    }

    @Test
    void modifyField_duplicate_name() {
        fieldDTO.setName("updated field");
        when(sectionFieldRepository.findById(2L)).thenReturn(Optional.of(field));
        when(sectionFieldRepository.existsBySectionIdAndName(1L, "updated field")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sectionService.modifyField(2L, fieldDTO));
        assertEquals("name already exists: updated field", exception.getMessage());
        verify(sectionFieldRepository, never()).save(any(SectionField.class));
    }

    @Test
    void modifyData() {
        SectionDataDTO update = new SectionDataDTO();
        update.setSectionId(1L);
        update.setData(Map.of("field", "updated"));
        when(sectionDataRepository.findById(3L)).thenReturn(Optional.of(data));
        when(sectionDataRepository.save(data)).thenReturn(data);

        SectionDataVO vo = sectionService.modifyData(3L, update);
        assertEquals(3L, vo.id());
        assertEquals(Map.of("field", "updated"), vo.data());
        verify(sectionDataRepository).save(data);
    }

    @Test
    void modifyData_not_found() {
        when(sectionDataRepository.findById(3L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> sectionService.modifyData(3L, dataDTO));
        assertEquals("section data not found: 3", exception.getMessage());
    }

    @Test
    void remove() {
        when(sectionRepository.existsById(1L)).thenReturn(true);

        sectionService.remove(1L);
        verify(sectionRepository).deleteById(1L);
    }

    @Test
    void remove_not_found() {
        when(sectionRepository.existsById(1L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> sectionService.remove(1L));
        assertEquals("section not found: 1", exception.getMessage());
        verify(sectionRepository, never()).deleteById(1L);
    }

    @Test
    void removeField() {
        when(sectionFieldRepository.existsById(2L)).thenReturn(true);

        sectionService.removeField(2L);
        verify(sectionFieldRepository).deleteById(2L);
    }

    @Test
    void removeField_not_found() {
        when(sectionFieldRepository.existsById(2L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> sectionService.removeField(2L));
        assertEquals("section not found: 2", exception.getMessage());
        verify(sectionFieldRepository, never()).deleteById(2L);
    }

    @Test
    void removeData() {
        when(sectionDataRepository.existsById(3L)).thenReturn(true);

        sectionService.removeData(3L);
        verify(sectionDataRepository).deleteById(3L);
    }

    @Test
    void removeData_not_found() {
        when(sectionDataRepository.existsById(3L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> sectionService.removeData(3L));
        assertEquals("section not found: 3", exception.getMessage());
        verify(sectionDataRepository, never()).deleteById(3L);
    }
}
