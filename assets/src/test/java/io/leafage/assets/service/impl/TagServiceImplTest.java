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

import io.leafage.assets.domain.Tag;
import io.leafage.assets.dto.TagDTO;
import io.leafage.assets.repository.TagRepository;
import io.leafage.assets.vo.TagVO;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * 类目接口测试
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    private TagDTO dto;

    @BeforeEach
    void setUp() {
        dto = new TagDTO();
        dto.setName("test");
    }

    @Test
    void retrieve() {
        Page<Tag> page = new PageImpl<>(List.of(mock(Tag.class)));

        given(tagRepository.findAll(ArgumentMatchers.<Specification<Tag>>any(),
                any(PageRequest.class))).willReturn(page);

        Page<TagVO> voPage = tagService.retrieve(0, 2, "id", true, "");

        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        given(tagRepository.findById(anyLong())).willReturn(Optional.ofNullable(mock(Tag.class)));

        TagVO vo = tagService.fetch(anyLong());

        Assertions.assertNotNull(vo);
    }

    @Test
    void exists() {
        given(this.tagRepository.existsByNameAndIdNot(anyString(), anyLong())).willReturn(true);

        boolean exists = tagService.exists("test", 1L);

        Assertions.assertTrue(exists);
    }

    @Test
    void exists_id_null() {
        given(this.tagRepository.existsByName(anyString())).willReturn(true);

        boolean exists = tagService.exists("test", null);

        Assertions.assertTrue(exists);
    }

    @Test
    void create() {
        given(tagRepository.saveAndFlush(any(Tag.class))).willReturn(mock(Tag.class));

        TagVO vo = tagService.create(mock(TagDTO.class));

        verify(tagRepository, times(1)).saveAndFlush(any(Tag.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void modify() {
        given(tagRepository.findById(anyLong())).willReturn(Optional.ofNullable(mock(Tag.class)));

        given(tagRepository.save(any(Tag.class))).willReturn(mock(Tag.class));

        TagVO vo = tagService.modify(1L, dto);

        verify(tagRepository, times(1)).save(any(Tag.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void remove() {
        tagService.remove(anyLong());

        verify(tagRepository, times(1)).deleteById(anyLong());
    }
}