/*
 * Copyright (c) 2024.  little3201.
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
package io.leafage.basic.assets.service.impl;

import io.leafage.basic.assets.domain.Tag;
import io.leafage.basic.assets.dto.TagDTO;
import io.leafage.basic.assets.repository.TagPostsRepository;
import io.leafage.basic.assets.repository.TagRepository;
import io.leafage.basic.assets.vo.TagVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * 类目接口测试
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagPostsRepository tagPostsRepository;

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
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"));
        Page<Tag> page = new PageImpl<>(List.of(Mockito.mock(Tag.class)), pageable, 2L);
        given(tagRepository.findAll(Mockito.any(PageRequest.class))).willReturn(page);

        given(tagPostsRepository.countByTagId(Mockito.anyLong())).willReturn(Mockito.anyLong());
        Page<TagVO> voPage = tagService.retrieve(0, 2, "id", true);

        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        given(tagRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(Mockito.mock(Tag.class)));

        TagVO categoryVO = tagService.fetch(Mockito.anyLong());

        Assertions.assertNotNull(categoryVO);
    }

    @Test
    void exists() {
        given(this.tagRepository.existsByNameAndIdNot(Mockito.anyString(), Mockito.anyLong())).willReturn(true);

        boolean exists = tagService.exists("test", 1L);

        Assertions.assertTrue(exists);
    }

    @Test
    void exists_id_null() {
        given(this.tagRepository.existsByName(Mockito.anyString())).willReturn(true);

        boolean exists = tagService.exists("test", null);

        Assertions.assertTrue(exists);
    }

    @Test
    void create() {
        given(tagRepository.saveAndFlush(Mockito.any(Tag.class))).willReturn(Mockito.mock(Tag.class));

        given(tagPostsRepository.countByTagId(Mockito.anyLong())).willReturn(2L);

        TagVO categoryVO = tagService.create(Mockito.mock(TagDTO.class));

        verify(tagRepository, times(1)).saveAndFlush(Mockito.any(Tag.class));
        Assertions.assertNotNull(categoryVO);
    }

    @Test
    void modify() {
        given(tagRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(Mockito.mock(Tag.class)));

        given(tagRepository.save(Mockito.any(Tag.class))).willReturn(Mockito.mock(Tag.class));

        given(tagPostsRepository.countByTagId(Mockito.anyLong())).willReturn(Mockito.anyLong());

        TagVO categoryVO = tagService.modify(1L, dto);

        verify(tagRepository, times(1)).save(Mockito.any(Tag.class));
        Assertions.assertNotNull(categoryVO);
    }

    @Test
    void remove() {
        tagService.remove(Mockito.anyLong());

        verify(tagRepository, times(1)).deleteById(Mockito.anyLong());
    }
}