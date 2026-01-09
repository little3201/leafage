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

package top.leafage.hypervisor.assets.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import top.leafage.hypervisor.assets.domain.Comment;
import top.leafage.hypervisor.assets.domain.dto.CommentDTO;
import top.leafage.hypervisor.assets.repository.CommentRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

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

    private CommentDTO dto;

    @BeforeEach
    void setUp() {
        dto = new CommentDTO();
        dto.setPostId(1L);
        dto.setReplier(1L);
    }

    @Test
    void comments() {
        given(this.commentRepository.findByPostIdAndReplierIsNull(anyLong())).willReturn(Flux.just(mock(Comment.class)));

        StepVerifier.create(commentService.comments(anyLong())).expectNextCount(1).verifyComplete();
    }

    @Test
    void repliers() {
        given(this.commentRepository.findByReplier(anyLong())).willReturn(Flux.just(mock(Comment.class)));

        StepVerifier.create(commentService.replies(anyLong())).expectNextCount(1).verifyComplete();
    }

    @Test
    void create() {
        given(this.commentRepository.save(any(Comment.class))).willReturn(Mono.just(mock(Comment.class)));

        StepVerifier.create(commentService.create(dto)).expectNextCount(1).verifyComplete();
    }

}