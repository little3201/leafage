/*
 *
 *  * Copyright(c) 2019-present the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *       https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package top.leafage.hypervisor.assets.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Query;
import top.leafage.hypervisor.assets.domain.Comment;
import top.leafage.hypervisor.assets.domain.dto.CommentDTO;
import top.leafage.hypervisor.assets.domain.vo.CommentVO;
import top.leafage.hypervisor.assets.repository.CommentRepository;
import top.leafage.hypervisor.assets.service.impl.CommentServiceImpl;

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

    @Mock
    private JdbcAggregateTemplate jdbcAggregateTemplate;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment entity;

    @BeforeEach
    void setUp() {
        entity = new Comment(1L, 1L, "test");
    }

    @Test
    void retrieve() {
        when(jdbcAggregateTemplate.findAll(any(Query.class), eq(Comment.class)))
                .thenReturn(List.of(entity));

        when(jdbcAggregateTemplate.count(any(Query.class), eq(Comment.class)))
                .thenReturn(1L);

        Page<CommentVO> voPage = commentService.retrieve(0, 2, "id", true, "body:like:test");
        assertEquals(1, voPage.getTotalElements());
        assertEquals(1, voPage.getContent().size());
    }

    @Test
    void relation() {
        when(commentRepository.findAllByPostIdAndSuperiorIdIsNull(anyLong())).thenReturn(List.of(entity));

        List<CommentVO> voList = commentService.relation(1L);
        assertEquals(1, voList.size());
        verify(commentRepository).findAllByPostIdAndSuperiorIdIsNull(anyLong());
    }

    @Test
    void relation_empty() {
        when(commentRepository.findAllByPostIdAndSuperiorIdIsNull(anyLong())).thenReturn(Collections.emptyList());

        List<CommentVO> voList = commentService.relation(anyLong());
        assertTrue(voList.isEmpty());
    }

    @Test
    void replies() {
        when(commentRepository.findAllBySuperiorId(anyLong())).thenReturn(List.of(entity));

        List<CommentVO> voList = commentService.replies(anyLong());
        assertEquals(1, voList.size());
        verify(commentRepository).findAllBySuperiorId(anyLong());
    }

    @Test
    void replies_empty() {
        List<CommentVO> voList = commentService.replies(anyLong());
        assertTrue(voList.isEmpty());
    }

    @Test
    void create() {
        when(commentRepository.save(any(Comment.class))).thenReturn(entity);

        CommentVO vo = commentService.create(mock(CommentDTO.class));
        assertEquals("test", vo.body());
    }

}