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
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import top.leafage.hypervisor.docs.domain.Template;
import top.leafage.hypervisor.docs.domain.dto.TemplateDTO;
import top.leafage.hypervisor.docs.domain.vo.TemplateVO;
import top.leafage.hypervisor.docs.repository.SectionRepository;
import top.leafage.hypervisor.docs.repository.TemplateRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.verify;

/**
 * Template service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class TemplateServiceImplTest {

    @Mock
    private TemplateRepository templateRepository;

    @Mock
    private SectionRepository sectionRepository;

    @InjectMocks
    private TemplateServiceImpl templateService;

    private TemplateDTO dto;
    private Template entity;

    @BeforeEach
    void setUp() {
        dto = new TemplateDTO();
        dto.setName("test");
        dto.setType(Template.Type.EXCEL);

        entity = TemplateDTO.toEntity(dto);
    }

    @Test
    void retrieve() {
        Page<Template> page = new PageImpl<>(List.of(entity));
        when(templateRepository.findAll(ArgumentMatchers.<Specification<Template>>any(),
                any(Pageable.class))).thenReturn(page);

        Page<TemplateVO> voPage = templateService.retrieve(0, 2, "id", true, "name:like:test");
        assertEquals(1, voPage.getTotalElements());
        verify(templateRepository).findAll(ArgumentMatchers.<Specification<Template>>any(), any(Pageable.class));
    }

    @Test
    void fetch() {
        when(templateRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        TemplateVO vo = templateService.fetch(anyLong());
        assertNotNull(vo);
        assertEquals("test", vo.name());
    }

    @Test
    void create() {
        when(templateRepository.existsByName("test")).thenReturn(false);
        when(templateRepository.save(any(Template.class))).thenReturn(entity);

        TemplateVO vo = templateService.create(dto);
        assertNotNull(vo);
        assertEquals("test", vo.name());
    }

    @Test
    void modify() {
        when(templateRepository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(templateRepository.existsByName("demo")).thenReturn(false);
        when(templateRepository.save(any(Template.class))).thenReturn(entity);

        dto.setName("demo");
        TemplateVO vo = templateService.modify(1L, dto);
        assertNotNull(vo);
        assertEquals("demo", vo.name());
    }

    @Test
    void enable() {
        when(templateRepository.existsById(anyLong())).thenReturn(true);
        when(templateRepository.enableById(anyLong())).thenReturn(1);

        assertTrue(templateService.enable(anyLong()));
    }

    @Test
    void disable() {
        when(templateRepository.existsById(anyLong())).thenReturn(true);
        when(templateRepository.disableById(anyLong())).thenReturn(1);

        assertTrue(templateService.disable(1L));
    }

    @Test
    void disable_not_found() {
        when(templateRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> templateService.disable(1L)
        );
        assertEquals("template not found: 1", exception.getMessage());
    }

    @Test
    void remove_not_found() {
        when(templateRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> templateService.remove(anyLong())
        );
        assertEquals("template not found: 0", exception.getMessage());
    }

    @Test
    void publish() {
        when(templateRepository.updateStatusById(1L, Template.Status.PUBLISHED)).thenReturn(1);

        assertTrue(templateService.publish(1L));
        verify(templateRepository).updateStatusById(1L, Template.Status.PUBLISHED);
    }

    @Test
    void archive() {
        when(templateRepository.updateStatusById(1L, Template.Status.ARCHIVED)).thenReturn(1);

        assertTrue(templateService.archive(1L));
        verify(templateRepository).updateStatusById(1L, Template.Status.ARCHIVED);
    }
}
