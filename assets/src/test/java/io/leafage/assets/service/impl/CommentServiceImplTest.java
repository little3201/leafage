/*
 *  Copyright 2018-2025 little3201.
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

package io.leafage.assets.service.impl;

import io.leafage.assets.domain.Comment;
import io.leafage.assets.dto.CommentDTO;
import io.leafage.assets.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.BDDMockito.given;

/**
 * comment service test
 *
 * @author wq li
 */
@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private CommentDTO commentDTO;

    @BeforeEach
    void setUp() {
        commentDTO = new CommentDTO();
        commentDTO.setPostId(1L);
        commentDTO.setReplier(1L);
    }

    @Test
    void comments() {
        given(this.commentRepository.findByPostIdAndReplierIsNull(Mockito.anyLong())).willReturn(Flux.just(Mockito.mock(Comment.class)));

        StepVerifier.create(commentService.comments(Mockito.anyLong())).expectNextCount(1).verifyComplete();
    }

    @Test
    void repliers() {
        given(this.commentRepository.findByReplier(Mockito.anyLong())).willReturn(Flux.just(Mockito.mock(Comment.class)));

        StepVerifier.create(commentService.replies(Mockito.anyLong())).expectNextCount(1).verifyComplete();
    }

    @Test
    void create() {
        given(this.commentRepository.save(Mockito.any(Comment.class))).willReturn(Mono.just(Mockito.mock(Comment.class)));

        StepVerifier.create(commentService.create(commentDTO)).expectNextCount(1).verifyComplete();
    }

}