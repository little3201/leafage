/*
 * Copyright (c) 2021. Leafage All Right Reserved.
 */
package io.leafage.basic.assets.controller;

import io.leafage.basic.assets.dto.PostsDTO;
import io.leafage.basic.assets.service.PostsService;
import io.leafage.basic.assets.vo.ContentVO;
import io.leafage.basic.assets.vo.PostsVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * posts controller.
 *
 * @author liwenqiang 2018/12/20 9:54
 **/
@RestController
@RequestMapping("/posts")
public class PostsController {

    private final Logger logger = LoggerFactory.getLogger(PostsController.class);

    private final PostsService postsService;

    public PostsController(PostsService postsService) {
        this.postsService = postsService;
    }

    /**
     * retrieve with page .
     *
     * @param page 页码
     * @param size 大小
     * @param sort 排序字段
     * @return 分页结果集
     */
    @GetMapping
    public ResponseEntity<Page<PostsVO>> retrieve(@RequestParam int page, @RequestParam int size, String sort) {
        Page<PostsVO> voPage;
        try {
            voPage = postsService.retrieve(page, size, sort);
        } catch (Exception e) {
            logger.error("Retrieve posts occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voPage);
    }

    /**
     * fetch with code .
     *
     * @param code 代码
     * @return 帖子信息，不包括内容
     */
    @GetMapping("/{code}")
    public ResponseEntity<PostsVO> fetch(@PathVariable String code) {
        PostsVO vo;
        try {
            vo = postsService.fetch(code);
        } catch (Exception e) {
            logger.error("Fetch posts occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(vo);
    }

    /**
     * 查询帖子详细信息，同时viewed自增1
     *
     * @param code 代码
     * @return 帖子所有信息，包括内容
     */
    @GetMapping("/{code}/details")
    public ResponseEntity<PostsVO> details(@PathVariable String code) {
        PostsVO vo;
        try {
            vo = postsService.details(code);
        } catch (Exception e) {
            logger.info("Fetch posts details occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(vo);
    }

    /**
     * 查询帖子详细信息，同时viewed自增1
     *
     * @param code 代码
     * @return 帖子所有信息，包括内容
     */
    @GetMapping("/{code}/content")
    public ResponseEntity<ContentVO> content(@PathVariable String code) {
        ContentVO vo;
        try {
            vo = postsService.content(code);
        } catch (Exception e) {
            logger.info("Fetch posts content occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(vo);
    }

    /**
     * 查询帖子是否存在
     *
     * @param title 标题
     * @return 帖子是否已存在
     */
    @GetMapping("/exist")
    public ResponseEntity<Boolean> exist(@RequestParam String title) {
        boolean exist;
        try {
            exist = postsService.exist(title);
        } catch (Exception e) {
            logger.info("Fetch posts exist occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok(exist);
    }

    /**
     * 保存文章信息
     *
     * @param postsDTO 文章内容
     * @return 帖子信息
     */
    @PostMapping
    public ResponseEntity<PostsVO> create(@RequestBody @Valid PostsDTO postsDTO) {
        PostsVO vo;
        try {
            vo = postsService.create(postsDTO);
        } catch (Exception e) {
            logger.error("Save posts occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    /**
     * 修改帖子信息
     *
     * @param code     代码
     * @param postsDTO 帖子信息
     * @return 修改后的帖子信息
     */
    @PutMapping("/{code}")
    public ResponseEntity<PostsVO> modify(@PathVariable String code, @RequestBody @Valid PostsDTO postsDTO) {
        PostsVO vo;
        try {
            vo = postsService.modify(code, postsDTO);
        } catch (Exception e) {
            logger.error("Modify posts occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(vo);
    }

    /**
     * 删除帖子信息
     *
     * @param code 代码
     * @return 删除结果
     */
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> remove(@PathVariable String code) {
        try {
            postsService.remove(code);
        } catch (Exception e) {
            logger.error("Remove posts occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().build();
    }
}
