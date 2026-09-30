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
import org.springframework.test.util.ReflectionTestUtils;
import top.leafage.hypervisor.docs.domain.Archive;
import top.leafage.hypervisor.docs.domain.Section;
import top.leafage.hypervisor.docs.domain.dto.ArchiveDTO;
import top.leafage.hypervisor.docs.domain.vo.ArchiveVO;
import top.leafage.hypervisor.docs.repository.ArchiveRepository;
import top.leafage.hypervisor.docs.repository.SectionRepository;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Archive service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class ArchiveServiceImplTest {

    @Mock
    private ArchiveRepository archiveRepository;

    @Mock
    private SectionRepository sectionRepository;

    @InjectMocks
    private ArchiveServiceImpl archiveService;

    private ArchiveDTO dto;
    private Archive entity;

    @BeforeEach
    void setUp() {
        dto = new ArchiveDTO();
        dto.setTitle("test");
        dto.setBody("body");
        dto.setOwner("owner");

        entity = ArchiveDTO.toEntity(dto);
        ReflectionTestUtils.setField(entity, "id", 2L);
    }

    @Test
    void retrieve() {
        Page<Archive> page = new PageImpl<>(List.of(mock(Archive.class)));

        when(archiveRepository.findAll(ArgumentMatchers.<Specification<Archive>>any(),
                any(Pageable.class))).thenReturn(page);

        Page<ArchiveVO> voPage = archiveService.retrieve(0, 2, "id", true, "title:like:test");
        assertEquals(1, voPage.getTotalElements());
        assertEquals(1, voPage.getContent().size());
        verify(archiveRepository).findAll(ArgumentMatchers.<Specification<Archive>>any(), any(Pageable.class));
    }

    @Test
    void fetch() {
        when(archiveRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        ArchiveVO vo = archiveService.fetch(anyLong());
        assertNotNull(vo);
        assertEquals("test", vo.title());
        verify(archiveRepository).findById(anyLong());
    }

    @Test
    void create() {
        when(archiveRepository.existsByTitle("test")).thenReturn(false);
        when(archiveRepository.save(any(Archive.class))).thenReturn(entity);

        ArchiveVO vo = archiveService.create(dto);
        assertNotNull(vo);
        assertEquals("test", vo.title());
        verify(archiveRepository).save(any(Archive.class));
    }

    @Test
    void create_with_schema_copy_sections() {
        dto.setSchemaId(1L);
        Section source = new Section(null, 1L, Section.OwnerType.ARCHIVE, "section", 1, 1, Map.of());
        ReflectionTestUtils.setField(source, "id", 10L);
        Section copied = new Section(2L, Section.OwnerType.ARCHIVE, source);
        ReflectionTestUtils.setField(copied, "id", 20L);

        when(archiveRepository.existsByTitle("test")).thenReturn(false);
        when(archiveRepository.save(any(Archive.class))).thenReturn(entity);
        when(sectionRepository.findAllByOwnerIdAndOwnerType(1L, Section.OwnerType.ARCHIVE)).thenReturn(List.of(source));
        when(sectionRepository.saveAll(any())).thenReturn(List.of(copied));

        ArchiveVO vo = archiveService.create(dto);

        assertEquals("test", vo.title());
        verify(sectionRepository, times(2)).saveAll(any());
    }

    @Test
    void create_title_conflict() {
        when(archiveRepository.existsByTitle("test")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> archiveService.create(dto));
        assertEquals("title already exists: test", exception.getMessage());
    }

    @Test
    void modify() {
        when(archiveRepository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(archiveRepository.existsByTitle("demo")).thenReturn(false);
        when(archiveRepository.save(any(Archive.class))).thenReturn(entity);

        dto.setTitle("demo");
        ArchiveVO vo = archiveService.modify(1L, dto);
        assertNotNull(vo);
        assertEquals("demo", vo.title());
        verify(archiveRepository).save(any(Archive.class));
    }

    @Test
    void remove() {
        when(archiveRepository.existsById(anyLong())).thenReturn(true);
        when(sectionRepository.findAllByOwnerIdAndOwnerType(anyLong(), any())).thenReturn(List.of());

        archiveService.remove(anyLong());
        verify(archiveRepository).deleteById(anyLong());
        verify(sectionRepository).deleteAllById(List.of());
    }

    @Test
    void fetch_not_found() {
        when(archiveRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> archiveService.fetch(1L));
        assertEquals("archive not found: 1", exception.getMessage());
    }

    @Test
    void modify_not_found() {
        when(archiveRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> archiveService.modify(1L, dto));
        assertEquals("archive not found: 1", exception.getMessage());
    }

    @Test
    void modify_title_conflict() {
        when(archiveRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(archiveRepository.existsByTitle("demo")).thenReturn(true);

        dto.setTitle("demo");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> archiveService.modify(1L, dto));
        assertEquals("title already exists: demo", exception.getMessage());
    }

    @Test
    void remove_not_found() {
        when(archiveRepository.existsById(1L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> archiveService.remove(1L));
        assertEquals("archive not found: 1", exception.getMessage());
    }
}
