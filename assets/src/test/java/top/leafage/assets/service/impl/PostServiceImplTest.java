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
import top.leafage.assets.domain.Post;
import top.leafage.assets.domain.dto.PostDTO;
import top.leafage.assets.domain.vo.PostVO;
import top.leafage.assets.repository.PostRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * 帖子接口测试
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postsService;

    private PostDTO dto;

    @BeforeEach
    void setUp() {
        dto = new PostDTO();
        dto.setTitle("title");
        dto.setSummary("excerpt");
        dto.setBody("body");
        dto.setTags(Set.of("code"));
    }

    @Test
    void retrieve() {
        Page<@NonNull Post> page = new PageImpl<>(List.of(mock(Post.class)));

        given(postRepository.findAll(ArgumentMatchers.<Specification<@NonNull Post>>any(),
                any(Pageable.class))).willReturn(page);

        Page<@NonNull PostVO> voPage = postsService.retrieve(0, 2, "id", true, "name:like:a");
        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        given(postRepository.findById(anyLong())).willReturn(Optional.ofNullable(mock(Post.class)));

        PostVO vo = postsService.fetch(anyLong());

        Assertions.assertNotNull(vo);
    }

    @Test
    void fetch_posts_null() {
        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        PostVO vo = postsService.fetch(anyLong());

        Assertions.assertNull(vo);
    }

    @Test
    void exists() {
        given(postRepository.existsByTitleAndIdNot(anyString(), anyLong())).willReturn(true);

        boolean exists = postsService.exists("test", 1L);

        Assertions.assertTrue(exists);
    }

    @Test
    void exists_id_null() {
        given(postRepository.existsByTitle(anyString())).willReturn(true);

        boolean exists = postsService.exists("test", null);

        Assertions.assertTrue(exists);
    }

    @Test
    void create() {
        given(postRepository.saveAndFlush(any(Post.class))).willReturn(mock(Post.class));

        PostVO vo = postsService.create(dto);

        verify(postRepository, times(1)).saveAndFlush(any(Post.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void modify() {
        given(postRepository.findById(anyLong())).willReturn(Optional.ofNullable(mock(Post.class)));

        given(postRepository.save(any(Post.class))).willReturn(mock(Post.class));

        PostVO vo = postsService.modify(1L, dto);

        verify(postRepository, times(1)).save(any(Post.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void remove() {
        postsService.remove(anyLong());

        verify(postRepository, times(1)).deleteById(anyLong());
    }

}
