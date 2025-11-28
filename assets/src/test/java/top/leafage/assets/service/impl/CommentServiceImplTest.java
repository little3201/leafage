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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

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

    @Test
    void retrieve() {
        Page<Comment> page = new PageImpl<>(List.of(mock(Comment.class)));

        given(commentRepository.findAll(ArgumentMatchers.<Specification<Comment>>any(),
                any(Pageable.class))).willReturn(page);

        Page<CommentVO> voPage = commentService.retrieve(0, 2, "id", true, "");
        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void relation() {
        given(commentRepository.findAllByPostIdAndReplierIsNull(anyLong())).willReturn(anyList());

        List<CommentVO> voList = commentService.relation(1L);
        Assertions.assertNotNull(voList);
    }

    @Test
    void relation_empty() {
        given(commentRepository.findAllByPostIdAndReplierIsNull(anyLong())).willReturn(Collections.emptyList());

        List<CommentVO> voList = commentService.relation(anyLong());
        Assertions.assertTrue(voList.isEmpty());
    }

    @Test
    void replies() {
        Comment comment = new Comment();
        comment.setBody("评论信息");
        comment.setPostId(1L);

        Comment comm = new Comment();
        comm.setBody("评论信息2222");
        comm.setPostId(1L);
        comm.setReplier(comment.getReplier());
        given(commentRepository.findAllByReplier(anyLong())).willReturn(List.of(comment, comm));

        List<CommentVO> voList = commentService.replies(anyLong());
        Assertions.assertNotNull(voList);
    }

    @Test
    void replies_empty() {
        List<CommentVO> voList = commentService.replies(anyLong());
        Assertions.assertTrue(voList.isEmpty());
    }

    @Test
    void create() {
        given(commentRepository.saveAndFlush(any(Comment.class))).willReturn(mock(Comment.class));

        CommentVO vo = commentService.create(mock(CommentDTO.class));
        Assertions.assertNotNull(vo);
    }

}