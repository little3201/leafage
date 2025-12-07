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

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import top.leafage.assets.domain.dto.CommentDTO;
import top.leafage.assets.service.CommentService;

/**
 * comment controller
 *
 * @author wq li
 */
@RestController
@RequestMapping("/comments")
public class CommentController {

    private final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;

    /**
     * <p>Constructor for CommentController.</p>
     *
     * @param commentService a {@link CommentService} object
     */
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 根据 postId 查询信息
     *
     * @param postId 帖子代码
     * @return 关联的评论
     */
    @GetMapping("/{postId}")
    public Mono<ServerResponse> comments(@PathVariable Long postId) {
        return commentService.comments(postId)
                .collectList()
                .flatMap(voList -> ServerResponse.ok().bodyValue(voList));
    }


    /**
     * 根据 id 查询回复信息
     *
     * @param id 评论代码
     * @return 关联的评论
     */
    @GetMapping("/{id}/replies")
    public Mono<ServerResponse> replies(@PathVariable Long id) {
        return commentService.replies(id)
                .collectList()
                .flatMap(voList -> ServerResponse.ok().bodyValue(voList));
    }

    /**
     * 添加信息
     *
     * @param dto 要添加的数据
     * @return 添加后的信息
     */
    @PostMapping
    public Mono<ServerResponse> create(@RequestBody @Valid CommentDTO dto) {
        return commentService.create(dto)
                .flatMap(vo -> ServerResponse.status(HttpStatus.CREATED).bodyValue(vo))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    /**
     * 删除信息
     *
     * @param id the pk.
     * @return 200状态码
     */
    @DeleteMapping("/{id}")
    public Mono<ServerResponse> remove(@PathVariable Long id) {
        return commentService.remove(id)
                .then(ServerResponse.noContent().build())
                .onErrorResume(ResponseStatusException.class,
                        e -> ServerResponse.notFound().build());
    }

}
