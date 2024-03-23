/*
 *  Copyright 2018-2024 little3201.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.leafage.basic.assets.service.impl;

import io.leafage.basic.assets.domain.Comment;
import io.leafage.basic.assets.dto.CommentDTO;
import io.leafage.basic.assets.repository.CommentRepository;
import io.leafage.basic.assets.vo.CommentVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;

/**
 * comment 接口测试
 *
 * @author wq li 2021/12/7 17:55
 **/
@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void retrieve() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Comment> page = new PageImpl<>(List.of(Mockito.mock(Comment.class)), pageable, 2L);
        given(commentRepository.findAll(PageRequest.of(0, 2))).willReturn(page);

        Page<CommentVO> voPage = commentService.retrieve(0, 2);
        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void relation() {
        given(commentRepository.findByPostIdAndReplierIsNull(Mockito.anyLong())).willReturn(Mockito.anyList());

        List<CommentVO> voList = commentService.relation(1L);
        Assertions.assertNotNull(voList);
    }

    @Test
    void relation_empty() {
        given(commentRepository.findByPostIdAndReplierIsNull(Mockito.anyLong())).willReturn(Collections.emptyList());

        List<CommentVO> voList = commentService.relation(Mockito.anyLong());
        Assertions.assertTrue(voList.isEmpty());
    }

    @Test
    void replies() {
        Comment comment = new Comment();
        comment.setContent("评论信息");
        comment.setPostId(1L);

        Comment comm = new Comment();
        comm.setContent("评论信息2222");
        comm.setPostId(1L);
        comm.setReplier(comment.getReplier());
        given(commentRepository.findByReplier(Mockito.anyLong())).willReturn(List.of(comment, comm));

        List<CommentVO> voList = commentService.replies(Mockito.anyLong());
        Assertions.assertNotNull(voList);
    }

    @Test
    void replies_empty() {
        List<CommentVO> voList = commentService.replies(Mockito.anyLong());
        Assertions.assertTrue(voList.isEmpty());
    }

    @Test
    void create() {
        given(commentRepository.saveAndFlush(Mockito.any(Comment.class))).willReturn(Mockito.mock(Comment.class));

        given(commentRepository.countByReplier(Mockito.anyLong())).willReturn(2L);

        CommentVO commentVO = commentService.create(Mockito.mock(CommentDTO.class));
        Assertions.assertNotNull(commentVO);
    }

}