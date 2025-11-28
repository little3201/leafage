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
import static org.mockito.BDDMockito.when;
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
        Page<Post> page = new PageImpl<>(List.of(mock(Post.class)));

        when(postRepository.findAll(ArgumentMatchers.<Specification<Post>>any(),
                any(Pageable.class))).thenReturn(page);

        Page<PostVO> voPage = postsService.retrieve(0, 2, "id", true, "name:like:a");
assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(mock(Post.class)));

        PostVO vo = postsService.fetch(anyLong());

assertNotNull(vo);
    }

    @Test
    void fetch_posts_null() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        PostVO vo = postsService.fetch(anyLong());

assertNull(vo);
    }

    @Test
    void exists() {
        when(postRepository.existsByTitleAndIdNot(anyString(), anyLong())).thenReturn(true);

        boolean exists = postsService.exists("test", 1L);

assertTrue(exists);
    }

    @Test
    void exists_id_null() {
        when(postRepository.existsByTitle(anyString())).thenReturn(true);

        boolean exists = postsService.exists("test", null);

assertTrue(exists);
    }

    @Test
    void create() {
        when(postRepository.saveAndFlush(any(Post.class))).thenReturn(mock(Post.class));

        PostVO vo = postsService.create(dto);

        verify(postRepository).saveAndFlush(any(Post.class));
assertNotNull(vo);
    }

    @Test
    void modify() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(mock(Post.class)));

        when(postRepository.save(any(Post.class))).thenReturn(mock(Post.class));

        PostVO vo = postsService.modify(1L, dto);

        verify(postRepository).save(any(Post.class));
assertNotNull(vo);
    }

    @Test
    void remove() {
        postsService.remove(anyLong());

        verify(postRepository).deleteById(anyLong());
    }

}
