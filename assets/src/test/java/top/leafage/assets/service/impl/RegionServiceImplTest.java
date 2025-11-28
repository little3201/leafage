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

package top.leafage.assets.service.impl;

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
import top.leafage.assets.domain.Region;
import top.leafage.assets.domain.dto.RegionDTO;
import top.leafage.assets.domain.vo.RegionVO;
import top.leafage.assets.repository.RegionRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

    @BeforeEach
    void setUp() {
        dto = new RegionDTO();
        dto.setName("西安市");
        dto.setAreaCode("029");
        dto.setPostalCode(71000);
        dto.setSuperiorId(1L);
    }

    @Test
    void retrieve() {
        Page<Region> page = new PageImpl<>(List.of(mock(Region.class)));

        when(regionRepository.findAll(ArgumentMatchers.<Specification<Region>>any(),
                any(Pageable.class))).thenReturn(page);

        Page<RegionVO> voPage = regionService.retrieve(0, 2, "id", true, "test:eq:a");
        assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        when(regionRepository.findById(anyLong())).thenReturn(Optional.of(mock(Region.class)));

        RegionVO vo = regionService.fetch(anyLong());

        assertNotNull(vo);
    }

    @Test
    void create() {
        when(regionRepository.saveAndFlush(any(Region.class))).thenReturn(mock(Region.class));

        RegionVO vo = regionService.create(mock(RegionDTO.class));

        verify(regionRepository).saveAndFlush(any(Region.class));
        assertNotNull(vo);
    }

    @Test
    void modify() {
        when(regionRepository.findById(anyLong())).thenReturn(Optional.of(mock(Region.class)));

        when(regionRepository.save(any(Region.class))).thenReturn(mock(Region.class));

        RegionVO vo = regionService.modify(anyLong(), dto);

        verify(regionRepository).save(any(Region.class));
        assertNotNull(vo);
    }

    @Test
    void remove() {
        regionService.remove(11L);

        verify(regionRepository).deleteById(anyLong());
    }
}