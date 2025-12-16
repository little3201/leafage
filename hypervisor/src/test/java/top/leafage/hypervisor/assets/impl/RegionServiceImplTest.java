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

package top.leafage.hypervisor.assets.impl;

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
import top.leafage.hypervisor.assets.domain.Region;
import top.leafage.hypervisor.assets.domain.dto.RegionDTO;
import top.leafage.hypervisor.assets.domain.vo.RegionVO;
import top.leafage.hypervisor.assets.repository.RegionRepository;
import top.leafage.hypervisor.assets.service.impl.RegionServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.*;

/**
 * region service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class RegionServiceImplTest {

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private RegionServiceImpl regionService;

    private RegionDTO dto;
    private Region entity;

    @BeforeEach
    void setUp() {
        dto = new RegionDTO();
        dto.setName("test");
        dto.setAreaCode("029");
        dto.setPostalCode("71000");
        dto.setSuperiorId(1L);

        entity = RegionDTO.toEntity(dto);
    }

    @Test
    void retrieve() {
        Page<Region> page = new PageImpl<>(List.of(mock(Region.class)));

        when(regionRepository.findAll(ArgumentMatchers.<Specification<Region>>any(),
                any(Pageable.class))).thenReturn(page);

        Page<RegionVO> voPage = regionService.retrieve(0, 2, "id", true, "name:like:test");
        assertEquals(1, voPage.getTotalElements());
        assertEquals(1, voPage.getContent().size());
        verify(regionRepository).findAll(ArgumentMatchers.<Specification<Region>>any(), any(Pageable.class));
    }

    @Test
    void fetch() {
        when(regionRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        RegionVO vo = regionService.fetch(anyLong());
        assertNotNull(vo);
        assertEquals("test", vo.name());
        verify(regionRepository).findById(anyLong());
    }

    @Test
    void fetch_not_found() {
        when(regionRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> regionService.fetch(anyLong())
        );
        assertEquals("region not found: 0", exception.getMessage());
        verify(regionRepository).findById(anyLong());
    }

    @Test
    void create() {
        when(regionRepository.existsByName("test")).thenReturn(false);
        when(regionRepository.saveAndFlush(any(Region.class))).thenReturn(entity);

        RegionVO vo = regionService.create(dto);
        assertNotNull(vo);
        assertEquals("test", vo.name());
        verify(regionRepository).saveAndFlush(any(Region.class));
    }

    @Test
    void create_name_conflict() {
        when(regionRepository.existsByName("test")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> regionService.create(dto)
        );
        assertEquals("name already exists: test", exception.getMessage());
        verify(regionRepository, never()).save(any());
    }

    @Test
    void modify() {
        when(regionRepository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(regionRepository.existsByName("demo")).thenReturn(false);
        when(regionRepository.save(any(Region.class))).thenReturn(entity);

        dto.setName("demo");
        RegionVO vo = regionService.modify(anyLong(), dto);
        assertNotNull(vo);
        assertEquals("demo", vo.name());
        verify(regionRepository).save(any(Region.class));
    }

    @Test
    void modify_username_conflict() {
        when(regionRepository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(regionRepository.existsByName("demo")).thenReturn(true);

        dto.setName("demo");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> regionService.modify(1L, dto)
        );
        assertEquals("name already exists: demo", exception.getMessage());
    }

    @Test
    void remove() {
        when(regionRepository.existsById(anyLong())).thenReturn(true);

        regionService.remove(1L);
        verify(regionRepository).deleteById(anyLong());
    }

    @Test
    void remove_not_found() {
        when(regionRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> regionService.remove(anyLong())
        );
        assertEquals("region not found: 0", exception.getMessage());
    }

    @Test
    void enable() {
        when(regionRepository.existsById(anyLong())).thenReturn(true);
        when(regionRepository.updateEnabledById(anyLong())).thenReturn(1);

        boolean enabled = regionService.enable(1L);
        assertTrue(enabled);
    }

    @Test
    void enable_not_found() {
        when(regionRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> regionService.enable(1L)
        );
        assertEquals("region not found: 1", exception.getMessage());
    }
}