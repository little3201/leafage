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

package io.leafage.assets.controller;

import io.leafage.assets.dto.CommentDTO;
import io.leafage.assets.service.CommentService;
import io.leafage.assets.vo.CategoryVO;
import io.leafage.assets.vo.CommentVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

/**
 * comment controller test
 *
 * @author wq li
 */
@WithMockUser
@ExtendWith(SpringExtension.class)
@WebFluxTest(CommentController.class)
class CommentControllerTest {

    @MockitoBean
    private CommentService commentService;

    @Autowired
    private WebTestClient webTestClient;

    private CommentDTO commentDTO;
    private CommentVO commentVO;

    @BeforeEach
    void setUp() {
        commentDTO = new CommentDTO();
        commentDTO.setPostId(1L);
        commentDTO.setContext("test");

        commentVO = new CommentVO(1L, true, Instant.now());
        commentVO.setPostId(commentDTO.getPostId());
        commentVO.setContext(commentDTO.getContext());
    }

    @Test
    void comments() {
        given(this.commentService.comments(Mockito.anyLong())).willReturn(Flux.just(commentVO));

        webTestClient.get().uri("/comments/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CategoryVO.class);
    }

    @Test
    void comments_error() {
        given(this.commentService.comments(Mockito.anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/comments/{id}", 1)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void replies() {
        given(this.commentService.replies(Mockito.anyLong())).willReturn(Flux.just(commentVO));

        webTestClient.get().uri("/comments/{id}/replies", 1)
                .exchange()
                .expectStatus().isOk().expectBodyList(CategoryVO.class);
    }

    @Test
    void repliers_error() {
        given(this.commentService.replies(Mockito.anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/comments/{id}/replies", 1)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void create() {
        given(this.commentService.create(Mockito.any(CommentDTO.class))).willReturn(Mono.just(commentVO));

        webTestClient.mutateWith(csrf()).post().uri("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(commentDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.context").isEqualTo("test");
    }

    @Test
    void create_error() {
        given(this.commentService.create(Mockito.any(CommentDTO.class))).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).post().uri("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(commentDTO)
                .exchange().expectStatus().is4xxClientError();
    }

    @Test
    void remove() {
        given(this.commentService.remove(Mockito.anyLong())).willReturn(Mono.empty());

        webTestClient.mutateWith(csrf()).delete().uri("/comments/{id}", 1)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void remove_error() {
        given(this.commentService.remove(Mockito.anyLong())).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).delete().uri("/comments/{id}", 1)
                .exchange()
                .expectStatus().is4xxClientError();
    }
}