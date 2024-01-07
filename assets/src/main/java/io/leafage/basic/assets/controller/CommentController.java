/*
 *  Copyright 2018-2024 the original author or authors.
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

package io.leafage.basic.assets.controller;

import io.leafage.basic.assets.dto.CommentDTO;
import io.leafage.basic.assets.service.CommentService;
import io.leafage.basic.assets.vo.CommentVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * comment controller
 *
 * @author liwenqiang 2021-07-17 21:01
 **/
@RestController
@RequestMapping("/comments")
public class CommentController {

    private final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;

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
    public ResponseEntity<Flux<CommentVO>> comments(@PathVariable Long postId) {
        Flux<CommentVO> voFlux;
        try {
            voFlux = commentService.comments(postId);
        } catch (Exception e) {
            logger.error("Retrieve comments by postId occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voFlux);
    }


    /**
     * 根据 id 查询回复信息
     *
     * @param id 评论代码
     * @return 关联的评论
     */
    @GetMapping("/{id}/replies")
    public ResponseEntity<Flux<CommentVO>> replies(@PathVariable Long id) {
        Flux<CommentVO> voFlux;
        try {
            voFlux = commentService.replies(id);
        } catch (Exception e) {
            logger.error("Retrieve comment repliers occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voFlux);
    }

    /**
     * 添加信息
     *
     * @param commentDTO 要添加的数据
     * @return 添加后的信息，异常时返回417状态码
     */
    @PostMapping
    public ResponseEntity<Mono<CommentVO>> create(@RequestBody @Valid CommentDTO commentDTO) {
        Mono<CommentVO> voMono;
        try {
            voMono = commentService.create(commentDTO);
        } catch (Exception e) {
            logger.error("Create comment occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(voMono);
    }

    /**
     * 删除信息
     *
     * @param id 主键
     * @return 200状态码，异常时返回417状态码
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<Void>> remove(@PathVariable Long id) {
        Mono<Void> voidMono;
        try {
            voidMono = commentService.remove(id);
        } catch (Exception e) {
            logger.error("Remove comment occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok(voidMono);
    }

}
