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

import io.leafage.assets.dto.PostDTO;
import io.leafage.assets.service.PostService;
import io.leafage.assets.vo.PostVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.leafage.common.poi.ExcelReader;

import java.util.List;

/**
 * posts controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/posts")
public class PostController {

    private final Logger logger = LoggerFactory.getLogger(PostController.class);

    private final PostService postService;

    /**
     * <p>Constructor for PostController.</p>
     *
     * @param postService a {@link PostService} object
     */
    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * retrieve with page .
     *
     * @param page       页码
     * @param size       大小
     * @param sortBy     排序字段
     * @param descending a boolean
     * @return 分页结果集
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_posts:read')")
    @GetMapping
    public ResponseEntity<Page<PostVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                 String sortBy, boolean descending) {
        Page<PostVO> voPage;
        try {
            voPage = postService.retrieve(page, size, sortBy, descending);
        } catch (Exception e) {
            logger.error("Retrieve posts error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voPage);
    }

    /**
     * fetch with id .
     *
     * @param id 主键
     * @return 帖子信息，不包括内容
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_posts:read')")
    @GetMapping("/{id}")
    public ResponseEntity<PostVO> fetch(@PathVariable Long id) {
        PostVO vo;
        try {
            vo = postService.fetch(id);
        } catch (Exception e) {
            logger.error("Fetch posts error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(vo);
    }

    /**
     * 查询帖子是否存在
     *
     * @param title 标题
     * @param id    主键
     * @return 帖子是否已存在
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> exists(@RequestParam String title, Long id) {
        boolean exists;
        try {
            exists = postService.exists(title, id);
        } catch (Exception e) {
            logger.info("Check posts exists error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok(exists);
    }

    /**
     * 保存文章信息
     *
     * @param dto 文章内容
     * @return 帖子信息
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_posts:create')")
    @PostMapping
    public ResponseEntity<PostVO> create(@Valid @RequestBody PostDTO dto) {
        PostVO vo;
        try {
            vo = postService.create(dto);
        } catch (Exception e) {
            logger.error("Save posts error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    /**
     * 修改帖子信息
     *
     * @param id  主键
     * @param dto 帖子信息
     * @return 修改后的帖子信息
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_posts:modify')")
    @PutMapping("/{id}")
    public ResponseEntity<PostVO> modify(@PathVariable Long id, @Valid @RequestBody PostDTO dto) {
        PostVO vo;
        try {
            vo = postService.modify(id, dto);
        } catch (Exception e) {
            logger.error("Modify posts error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(vo);
    }

    /**
     * 删除帖子信息
     *
     * @param id 主键
     * @return 删除结果
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_posts:remove')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        try {
            postService.remove(id);
        } catch (Exception e) {
            logger.error("Remove posts error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Enable a record when enabled is false or disable when enabled is ture.
     *
     * @param id The record ID.
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_posts:enable')")
    @PatchMapping("/{id}")
    public ResponseEntity<Boolean> enable(@PathVariable Long id) {
        boolean enabled;
        try {
            enabled = postService.enable(id);
        } catch (Exception e) {
            logger.error("Toggle enabled error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(enabled);
    }

    /**
     * Import the records.
     *
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_posts:import')")
    @PostMapping("/import")
    public ResponseEntity<List<PostVO>> importFromFile(MultipartFile file) {
        List<PostVO> voList;
        try {
            List<PostDTO> dtoList = ExcelReader.read(file.getInputStream(), PostDTO.class);
            voList = postService.createAll(dtoList);
        } catch (Exception e) {
            logger.error("Import post error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().body(voList);
    }

}
