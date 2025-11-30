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
import top.leafage.assets.domain.Comment;
import top.leafage.assets.domain.dto.CommentDTO;
import top.leafage.assets.domain.vo.CommentVO;
import top.leafage.assets.repository.CommentRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * comment 接口测试
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment entity;

    @BeforeEach
    void setUp() {
        entity = new Comment(1L, "test", 1L);
    }

    @Test
    void retrieve() {
        Page<Comment> page = new PageImpl<>(List.of(entity));

        when(commentRepository.findAll(ArgumentMatchers.<Specification<Comment>>any(),
                any(Pageable.class))).thenReturn(page);

        Page<CommentVO> voPage = commentService.retrieve(0, 2, "id", true, "body:like:test");
        assertEquals(1, voPage.getTotalElements());
        assertEquals(1, voPage.getContent().size());
        verify(commentRepository).findAll(ArgumentMatchers.<Specification<Comment>>any(), any(Pageable.class));
    }

    @Test
    void relation() {
        when(commentRepository.findAllByPostIdAndReplierIsNull(anyLong())).thenReturn(List.of(entity));

        List<CommentVO> voList = commentService.relation(1L);
        assertEquals(1, voList.size());
        verify(commentRepository).findAllByPostIdAndReplierIsNull(anyLong());
    }

    @Test
    void relation_empty() {
        when(commentRepository.findAllByPostIdAndReplierIsNull(anyLong())).thenReturn(Collections.emptyList());

        List<CommentVO> voList = commentService.relation(anyLong());
        assertTrue(voList.isEmpty());
    }

    @Test
    void replies() {
        when(commentRepository.findAllByReplier(anyLong())).thenReturn(List.of(entity));

        List<CommentVO> voList = commentService.replies(anyLong());
        assertEquals(1, voList.size());
        verify(commentRepository).findAllByReplier(anyLong());
    }

    @Test
    void replies_empty() {
        List<CommentVO> voList = commentService.replies(anyLong());
        assertTrue(voList.isEmpty());
    }

    @Test
    void create() {
        when(commentRepository.saveAndFlush(any(Comment.class))).thenReturn(entity);

        CommentVO vo = commentService.create(mock(CommentDTO.class));
        assertEquals("test", vo.body());
    }

}