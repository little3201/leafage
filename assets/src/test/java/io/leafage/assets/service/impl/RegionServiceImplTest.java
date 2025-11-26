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
import io.leafage.assets.domain.dto.RegionDTO;
import io.leafage.assets.domain.vo.RegionVO;
import io.leafage.assets.repository.RegionRepository;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Assertions;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

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
        Page<@NonNull Region> page = new PageImpl<>(List.of(mock(Region.class)));

        given(this.regionRepository.findAll(ArgumentMatchers.<Specification<@NonNull Region>>any(),
                any(Pageable.class))).willReturn(page);

        Page<@NonNull RegionVO> voPage = regionService.retrieve(0, 2, "id", true, "test:eq:a");
        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        given(this.regionRepository.findById(anyLong())).willReturn(Optional.ofNullable(mock(Region.class)));

        RegionVO vo = regionService.fetch(anyLong());

        Assertions.assertNotNull(vo);
    }

    @Test
    void exists() {
        given(this.regionRepository.existsByNameAndIdNot(anyString(), anyLong())).willReturn(true);

        boolean exists = regionService.exists("test", 1L);

        Assertions.assertTrue(exists);
    }

    @Test
    void exists_id_null() {
        given(this.regionRepository.existsByName(anyString())).willReturn(true);

        boolean exists = regionService.exists("test", null);

        Assertions.assertTrue(exists);
    }

    @Test
    void create() {
        given(this.regionRepository.saveAndFlush(any(Region.class))).willReturn(mock(Region.class));

        RegionVO vo = regionService.create(mock(RegionDTO.class));

        verify(this.regionRepository, times(1)).saveAndFlush(any(Region.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void modify() {
        given(this.regionRepository.findById(anyLong())).willReturn(Optional.ofNullable(mock(Region.class)));

        given(this.regionRepository.save(any(Region.class))).willReturn(mock(Region.class));

        RegionVO vo = regionService.modify(anyLong(), dto);

        verify(this.regionRepository, times(1)).save(any(Region.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void remove() {
        regionService.remove(11L);

        verify(this.regionRepository, times(1)).deleteById(anyLong());
    }
}