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

package top.leafage.assets.controller;

import top.leafage.assets.dto.CommentDTO;
import top.leafage.assets.service.CommentService;
import top.leafage.assets.vo.CommentVO;
import top.leafage.assets.vo.TagVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

    private CommentDTO dto;
    private CommentVO vo;

    @BeforeEach
    void setUp() {
        dto = new CommentDTO();
        dto.setPostId(1L);
        dto.setContent("test");

        vo = new CommentVO();
        vo.setId(1L);
        vo.setPostId(dto.getPostId());
        vo.setContent(dto.getContent());
    }

    @Test
    void comments() {
        given(this.commentService.comments(anyLong())).willReturn(Flux.just(vo));

        webTestClient.get().uri("/comments/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TagVO.class);
    }

    @Test
    void comments_error() {
        given(this.commentService.comments(anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/comments/{id}", 1L)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void replies() {
        given(this.commentService.replies(anyLong())).willReturn(Flux.just(vo));

        webTestClient.get().uri("/comments/{id}/replies", 1L)
                .exchange()
                .expectStatus().isOk().expectBodyList(TagVO.class);
    }

    @Test
    void repliers_error() {
        given(this.commentService.replies(anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/comments/{id}/replies", 1L)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void create() {
        given(this.commentService.create(any(CommentDTO.class))).willReturn(Mono.just(vo));

        webTestClient.mutateWith(csrf()).post().uri("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.content").isEqualTo("test");
    }

    @Test
    void create_error() {
        given(this.commentService.create(any(CommentDTO.class))).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).post().uri("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange().expectStatus().is5xxServerError();
    }

    @Test
    void remove() {
        given(this.commentService.remove(anyLong())).willReturn(Mono.empty());

        webTestClient.mutateWith(csrf()).delete().uri("/comments/{id}", 1L)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void remove_error() {
        given(this.commentService.remove(anyLong())).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).delete().uri("/comments/{id}", 1L)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}