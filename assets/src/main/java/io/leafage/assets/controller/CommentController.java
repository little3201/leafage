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

package io.leafage.assets.controller;

import io.leafage.assets.dto.CommentDTO;
import io.leafage.assets.service.CommentService;
import io.leafage.assets.vo.CommentVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * comment controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/comments")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

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
     * 列表查询
     *
     * @param page       分页位置
     * @param size       分页大小
     * @param sortBy     a {@link String} object
     * @param descending a boolean
     * @return 查询到数据集，异常时返回204
     */
    @GetMapping
    public ResponseEntity<Page<CommentVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                    String sortBy, boolean descending) {
        Page<CommentVO> voPage;
        try {
            voPage = commentService.retrieve(page, size, sortBy, descending);
        } catch (Exception e) {
            logger.error("Retrieve comment error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voPage);
    }

    /**
     * 根据 posts id 查询
     *
     * @param id 帖子代码
     * @return 关联的评论
     */
    @GetMapping("/{id}")
    public ResponseEntity<List<CommentVO>> relation(@PathVariable Long id) {
        List<CommentVO> voList;
        try {
            voList = commentService.relation(id);
        } catch (Exception e) {
            logger.error("Retrieve comment by posts error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voList);
    }


    /**
     * 根据id查询回复
     *
     * @param id 帖子代码
     * @return 关联的评论
     */
    @GetMapping("/{id}/replies")
    public ResponseEntity<List<CommentVO>> replies(@PathVariable Long id) {
        List<CommentVO> voList;
        try {
            voList = commentService.replies(id);
        } catch (Exception e) {
            logger.error("Retrieve comment replies error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voList);
    }

    /**
     * 添加信息
     *
     * @param dto 要添加的数据
     * @return 添加后的信息，异常时返回417状态码
     */
    @PostMapping
    public ResponseEntity<CommentVO> create(@RequestBody @Valid CommentDTO dto) {
        CommentVO vo;
        try {
            vo = commentService.create(dto);
        } catch (Exception e) {
            logger.error("Create comment error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

}
