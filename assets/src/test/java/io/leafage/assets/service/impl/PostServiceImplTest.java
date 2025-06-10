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


import io.leafage.assets.domain.Post;
import io.leafage.assets.domain.PostContent;
import io.leafage.assets.dto.PostDTO;
import io.leafage.assets.repository.PostContentRepository;
import io.leafage.assets.repository.PostRepository;
import io.leafage.assets.vo.PostVO;
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
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * 帖子接口测试
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostContentRepository postContentRepository;

    @InjectMocks
    private PostServiceImpl postsService;

    private PostDTO dto;

    @BeforeEach
    void setUp() {
        dto = new PostDTO();
        dto.setTitle("title");
        dto.setExcerpt("excerpt");
        dto.setContent("content");
        dto.setTags(Set.of("code"));
    }

    @Test
    void retrieve() {
        Page<Post> page = new PageImpl<>(List.of(Mockito.mock(Post.class)));

        given(postRepository.findAll(Mockito.any(Pageable.class))).willReturn(page);

        Page<PostVO> voPage = postsService.retrieve(0, 2, "id", true, "name:like:a");
        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        given(postRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(Mockito.mock(Post.class)));

        PostVO postVO = postsService.fetch(Mockito.anyLong());

        Assertions.assertNotNull(postVO);
    }

    @Test
    void fetch_posts_null() {
        given(postRepository.findById(Mockito.anyLong())).willReturn(Optional.empty());

        PostVO postVO = postsService.fetch(Mockito.anyLong());

        Assertions.assertNull(postVO);
    }

    @Test
    void exists() {
        given(postRepository.existsByTitleAndIdNot(Mockito.anyString(), Mockito.anyLong())).willReturn(true);

        boolean exists = postsService.exists("test", 1L);

        Assertions.assertTrue(exists);
    }

    @Test
    void exists_id_null() {
        given(postRepository.existsByTitle(Mockito.anyString())).willReturn(true);

        boolean exists = postsService.exists("test", null);

        Assertions.assertTrue(exists);
    }

    @Test
    void create() {
        given(postRepository.saveAndFlush(Mockito.any(Post.class))).willReturn(Mockito.mock(Post.class));

        given(postContentRepository.getByPostId(Mockito.anyLong())).willReturn(Optional.of(Mockito.mock(PostContent.class)));

        given(postContentRepository.saveAndFlush(Mockito.any(PostContent.class))).willReturn(Mockito.mock(PostContent.class));

        PostVO postVO = postsService.create(dto);

        verify(postRepository, times(1)).saveAndFlush(Mockito.any(Post.class));
        verify(postContentRepository, times(1)).saveAndFlush(Mockito.any(PostContent.class));
        Assertions.assertNotNull(postVO);
    }

    @Test
    void modify() {
        given(postRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(Mockito.mock(Post.class)));

        given(postRepository.save(Mockito.any(Post.class))).willReturn(Mockito.mock(Post.class));

        given(postContentRepository.getByPostId(Mockito.anyLong())).willReturn(Optional.of(Mockito.mock(PostContent.class)));

        given(postContentRepository.save(Mockito.any(PostContent.class))).willReturn(Mockito.mock(PostContent.class));

        PostVO postVO = postsService.modify(1L, dto);

        verify(postRepository, times(1)).save(Mockito.any(Post.class));
        verify(postContentRepository, times(1)).save(Mockito.any(PostContent.class));
        Assertions.assertNotNull(postVO);
    }

    @Test
    void remove() {
        postsService.remove(Mockito.anyLong());

        verify(postRepository, times(1)).deleteById(Mockito.anyLong());
    }

}
