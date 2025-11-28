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

package top.leafage.hypervisor.service.impl;

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
import top.leafage.hypervisor.domain.Dictionary;
import top.leafage.hypervisor.domain.dto.DictionaryDTO;
import top.leafage.hypervisor.domain.vo.DictionaryVO;
import top.leafage.hypervisor.repository.DictionaryRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.*;

/**
 * dictionary controller test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class DictionaryServiceImplTest {


    @Mock
    private DictionaryRepository dictionaryRepository;

    @InjectMocks
    private DictionaryServiceImpl dictionaryService;

    private DictionaryDTO dto;
    private Dictionary entity;

    @BeforeEach
    void setUp() {
        dto = new DictionaryDTO();
        dto.setName("test");
        dto.setSuperiorId(1L);
        dto.setDescription("description");

        entity = DictionaryDTO.toEntity(dto);
    }

    @Test
    void retrieve() {
        Page<Dictionary> page = new PageImpl<>(List.of(mock(Dictionary.class)));

        when(dictionaryRepository.findAll(ArgumentMatchers.<Specification<Dictionary>>any(),
                any(Pageable.class))).thenReturn(page);

        Page<DictionaryVO> voPage = dictionaryService.retrieve(0, 2, "id", true, "test");
        assertEquals(1, voPage.getTotalElements());
        assertEquals(1, voPage.getContent().size());
        verify(dictionaryRepository).findAll(ArgumentMatchers.<Specification<Dictionary>>any(), any(Pageable.class));
    }

    @Test
    void fetch() {
        when(dictionaryRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        DictionaryVO vo = dictionaryService.fetch(1L);
        assertNotNull(vo);
        assertEquals("test", vo.name());
        verify(dictionaryRepository).findById(anyLong());
    }

    @Test
    void fetch_not_found() {
        when(dictionaryRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> dictionaryService.fetch(anyLong())
        );
        assertEquals("dictionary not found: 0", exception.getMessage());
        verify(dictionaryRepository).findById(anyLong());
    }

    @Test
    void subset() {
        when(dictionaryRepository.findAllBySuperiorId(anyLong())).thenReturn(List.of(mock(Dictionary.class)));

        List<DictionaryVO> voList = dictionaryService.subset(1L);
        assertNotNull(voList);
    }

    @Test
    void subset_empty() {
        when(dictionaryRepository.findAllBySuperiorId(anyLong())).thenReturn(Collections.emptyList());

        List<DictionaryVO> voList = dictionaryService.subset(1L);
        assertEquals(Collections.emptyList(), voList);
    }

    @Test
    void create() {
        when(dictionaryRepository.existsByName("test")).thenReturn(false);
        when(dictionaryRepository.saveAndFlush(any(Dictionary.class))).thenReturn(entity);

        DictionaryVO vo = dictionaryService.create(dto);
        assertNotNull(vo);
        assertEquals("test", vo.name());
        verify(dictionaryRepository).saveAndFlush(any(Dictionary.class));
    }

    @Test
    void create_name_conflict() {
        when(dictionaryRepository.existsByName("test")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dictionaryService.create(dto)
        );
        assertEquals("name already exists: test", exception.getMessage());
        verify(dictionaryRepository, never()).save(any());
    }

    @Test
    void modify() {
        when(dictionaryRepository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(dictionaryRepository.existsByName("demo")).thenReturn(false);
        when(dictionaryRepository.save(any(Dictionary.class))).thenReturn(entity);

        dto.setName("demo");
        DictionaryVO vo = dictionaryService.modify(1L, dto);
        assertNotNull(vo);
        assertEquals("demo", vo.name());
        verify(dictionaryRepository).save(any(Dictionary.class));
    }

    @Test
    void modify_username_conflict() {
        when(dictionaryRepository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(dictionaryRepository.existsByName("demo")).thenReturn(true);

        dto.setName("demo");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dictionaryService.modify(1L, dto)
        );
        assertEquals("name already exists: demo", exception.getMessage());
    }

    @Test
    void remove() {
        when(dictionaryRepository.existsById(anyLong())).thenReturn(true);

        dictionaryService.remove(1L);
        verify(dictionaryRepository).deleteById(anyLong());
    }

    @Test
    void enable() {
        when(dictionaryRepository.existsById(anyLong())).thenReturn(true);
        when(dictionaryRepository.updateEnabledById(anyLong())).thenReturn(1);

        boolean enabled = dictionaryService.enable(1L);
        assertTrue(enabled);
    }

}