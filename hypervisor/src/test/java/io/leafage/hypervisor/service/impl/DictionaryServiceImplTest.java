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

package io.leafage.hypervisor.service.impl;

import io.leafage.hypervisor.domain.Dictionary;
import io.leafage.hypervisor.domain.dto.DictionaryDTO;
import io.leafage.hypervisor.domain.vo.DictionaryVO;
import io.leafage.hypervisor.repository.DictionaryRepository;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

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

    @BeforeEach
    void setUp() {
        dto = new DictionaryDTO();
        dto.setName("group");
    }

    @Test
    void retrieve() {
        Page<Dictionary> page = new PageImpl<>(List.of(mock(Dictionary.class)));

        given(this.dictionaryRepository.findAll(ArgumentMatchers.<Specification<Dictionary>>any(),
                any(Pageable.class))).willReturn(page);

        Page<DictionaryVO> voPage = dictionaryService.retrieve(0, 2, "id", true, "test");

        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        given(this.dictionaryRepository.findById(anyLong())).willReturn(Optional.ofNullable(mock(Dictionary.class)));

        DictionaryVO vo = dictionaryService.fetch(1L);

        Assertions.assertNotNull(vo);
    }

    @Test
    void subset() {
        given(this.dictionaryRepository.findAllBySuperiorId(anyLong())).willReturn(List.of(mock(Dictionary.class)));

        List<Dictionary> list = dictionaryService.subset(1L);

        Assertions.assertNotNull(list);
    }

    @Test
    void subset_empty() {
        given(this.dictionaryRepository.findAllBySuperiorId(anyLong())).willReturn(Collections.emptyList());

        List<Dictionary> list = dictionaryService.subset(1L);

        Assertions.assertEquals(Collections.emptyList(), list);
    }

    @Test
    void exists() {
        given(this.dictionaryRepository.existsBySuperiorIdAndNameAndIdNot(anyLong(), anyString(),
                anyLong())).willReturn(true);

        boolean exists = dictionaryService.exists(1L, "test", 2L);

        Assertions.assertTrue(exists);
    }

    @Test
    void exists_id_null() {
        given(this.dictionaryRepository.existsBySuperiorIdAndName(anyLong(), anyString())).willReturn(true);

        boolean exists = dictionaryService.exists(1L, "test", null);

        Assertions.assertTrue(exists);
    }

    @Test
    void create() {
        given(this.dictionaryRepository.saveAndFlush(any(Dictionary.class))).willReturn(mock(Dictionary.class));

        DictionaryVO vo = dictionaryService.create(mock(DictionaryDTO.class));

        verify(this.dictionaryRepository, times(1)).saveAndFlush(any(Dictionary.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void modify() {
        given(this.dictionaryRepository.findById(anyLong())).willReturn(Optional.ofNullable(mock(Dictionary.class)));

        given(this.dictionaryRepository.save(any(Dictionary.class))).willReturn(mock(Dictionary.class));

        DictionaryVO vo = dictionaryService.modify(1L, dto);

        verify(this.dictionaryRepository, times(1)).save(any(Dictionary.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void remove() {
        dictionaryService.remove(1L);

        verify(this.dictionaryRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void enable() {
        given(this.dictionaryRepository.updateEnabledById(anyLong())).willReturn(1);

        boolean enabled = dictionaryService.enable(1L);

        Assertions.assertTrue(enabled);
    }

}