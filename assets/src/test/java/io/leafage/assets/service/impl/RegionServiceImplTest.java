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

package io.leafage.assets.service.impl;

import io.leafage.assets.domain.Region;
import io.leafage.assets.dto.RegionDTO;
import io.leafage.assets.repository.RegionRepository;
import io.leafage.assets.vo.RegionVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
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
        Page<Region> page = new PageImpl<>(List.of(Mockito.mock(Region.class)));

        given(this.regionRepository.findAll(Mockito.any(Pageable.class))).willReturn(page);

        Page<RegionVO> voPage = regionService.retrieve(0, 2, "id", true, "test:eq:a");
        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        given(this.regionRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(Mockito.mock(Region.class)));

        RegionVO vo = regionService.fetch(Mockito.anyLong());

        Assertions.assertNotNull(vo);
    }

    @Test
    void exists() {
        given(this.regionRepository.existsByNameAndIdNot(Mockito.anyString(), Mockito.anyLong())).willReturn(true);

        boolean exists = regionService.exists("test", 1L);

        Assertions.assertTrue(exists);
    }

    @Test
    void exists_id_null() {
        given(this.regionRepository.existsByName(Mockito.anyString())).willReturn(true);

        boolean exists = regionService.exists("test", null);

        Assertions.assertTrue(exists);
    }

    @Test
    void create() {
        given(this.regionRepository.saveAndFlush(Mockito.any(Region.class))).willReturn(Mockito.mock(Region.class));

        RegionVO vo = regionService.create(Mockito.mock(RegionDTO.class));

        verify(this.regionRepository, times(1)).saveAndFlush(Mockito.any(Region.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void modify() {
        given(this.regionRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(Mockito.mock(Region.class)));

        given(this.regionRepository.save(Mockito.any(Region.class))).willReturn(Mockito.mock(Region.class));

        RegionVO vo = regionService.modify(Mockito.anyLong(), dto);

        verify(this.regionRepository, times(1)).save(Mockito.any(Region.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void remove() {
        regionService.remove(11L);

        verify(this.regionRepository, times(1)).deleteById(Mockito.anyLong());
    }
}