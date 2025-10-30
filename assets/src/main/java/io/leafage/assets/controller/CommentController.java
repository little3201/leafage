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
import io.leafage.assets.vo.CommentVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * comment controller
 *
 * @author wq li
 */
@Validated
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
    public Flux<CommentVO> comments(@PathVariable Long postId) {
        return commentService.comments(postId)
                .doOnError(e -> logger.error("Retrieve comments error: ", e));
    }


    /**
     * 根据 id 查询回复信息
     *
     * @param id 评论代码
     * @return 关联的评论
     */
    @GetMapping("/{id}/replies")
    public Flux<CommentVO> replies(@PathVariable Long id) {
        return commentService.replies(id)
                .doOnError(e -> logger.error("Retrieve comment repliers error: ", e));
    }

    /**
     * 添加信息
     *
     * @param dto 要添加的数据
     * @return 添加后的信息
     */
    @PostMapping
    public Mono<CommentVO> create(@RequestBody @Valid CommentDTO dto) {
        return commentService.create(dto)
                .doOnError(e -> logger.error("Create comment occurred an error: ", e));
    }

    /**
     * 删除信息
     *
     * @param id 主键
     * @return 200状态码
     */
    @DeleteMapping("/{id}")
    public Mono<Void> remove(@PathVariable Long id) {
        return commentService.remove(id)
                .doOnError(e -> logger.error("Remove comment occurred an error: ", e));
    }

}
