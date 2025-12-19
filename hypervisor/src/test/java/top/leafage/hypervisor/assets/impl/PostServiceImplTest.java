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
import top.leafage.hypervisor.assets.domain.Post;
import top.leafage.hypervisor.assets.domain.dto.PostDTO;
import top.leafage.hypervisor.assets.domain.vo.PostVO;
import top.leafage.hypervisor.assets.repository.PostRepository;
import top.leafage.hypervisor.assets.service.impl.PostServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.never;
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

    @InjectMocks
    private PostServiceImpl postsService;

    private PostDTO dto;
    private Post entity;

    @BeforeEach
    void setUp() {
        dto = new PostDTO();
        dto.setTitle("test");
        dto.setSummary("excerpt");
        dto.setBody("body");
        dto.setTags(Set.of("code"));

        entity = PostDTO.toEntity(dto);
    }

    @Test
    void retrieve() {
        Page<Post> page = new PageImpl<>(List.of(entity));

        when(postRepository.findAll(ArgumentMatchers.<Specification<Post>>any(),
                any(Pageable.class))).thenReturn(page);

        Page<PostVO> voPage = postsService.retrieve(0, 2, "id", true, "title:like:test");
        assertEquals(1, voPage.getTotalElements());
        assertEquals(1, voPage.getContent().size());
        verify(postRepository).findAll(ArgumentMatchers.<Specification<Post>>any(), any(Pageable.class));
    }

    @Test
    void fetch() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        PostVO vo = postsService.fetch(anyLong());
        assertNotNull(vo);
        assertEquals("test", vo.title());
        verify(postRepository).findById(anyLong());
    }

    @Test
    void fetch_posts_null() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> postsService.fetch(anyLong())
        );
        assertEquals("post not found: 0", exception.getMessage());
        verify(postRepository).findById(anyLong());
    }

    @Test
    void create() {
        when(postRepository.existsByTitle("test")).thenReturn(false);
        when(postRepository.saveAndFlush(any(Post.class))).thenReturn(entity);

        PostVO vo = postsService.create(dto);
        assertNotNull(vo);
        assertEquals("test", vo.title());
        verify(postRepository).saveAndFlush(any(Post.class));
    }

    @Test
    void create_name_conflict() {
        when(postRepository.existsByTitle("test")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> postsService.create(dto)
        );
        assertEquals("title already exists: test", exception.getMessage());
        verify(postRepository, never()).save(any());
    }

    @Test
    void modify() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(postRepository.existsByTitle("demo")).thenReturn(false);
        when(postRepository.save(any(Post.class))).thenReturn(entity);

        dto.setTitle("demo");
        PostVO vo = postsService.modify(1L, dto);
        assertNotNull(vo);
        assertEquals("demo", vo.title());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void modify_username_conflict() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(postRepository.existsByTitle("demo")).thenReturn(true);

        dto.setTitle("demo");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> postsService.modify(1L, dto)
        );
        assertEquals("title already exists: demo", exception.getMessage());
    }

    @Test
    void remove() {
        when(postRepository.existsById(anyLong())).thenReturn(true);

        postsService.remove(anyLong());
        verify(postRepository).deleteById(anyLong());
    }

    @Test
    void remove_not_found() {
        when(postRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> postsService.remove(anyLong())
        );
        assertEquals("post not found: 0", exception.getMessage());
    }

}
