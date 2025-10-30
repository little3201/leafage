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

import io.leafage.assets.dto.TagDTO;
import io.leafage.assets.service.TagService;
import io.leafage.assets.vo.TagVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * tag controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/tags")
public class TagController {

    private final Logger logger = LoggerFactory.getLogger(TagController.class);

    private final TagService tagService;

    /**
     * <p>Constructor for TagController.</p>
     *
     * @param tagService a {@link TagService} object
     */
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * 分页查询tag
     *
     * @param page       页码
     * @param size       大小
     * @param sortBy     排序字段
     * @param descending a boolean
     * @return 分页结果集
     */
    @GetMapping
    public ResponseEntity<Page<TagVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                String sortBy, boolean descending, String filters) {
        Page<TagVO> voPage;
        try {
            voPage = tagService.retrieve(page, size, sortBy, descending, filters);
        } catch (Exception e) {
            logger.error("Retrieve posts error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voPage);
    }

    /**
     * 查询tag信息
     *
     * @param id 主键
     * @return 匹配到的tag信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<TagVO> fetch(@PathVariable Long id) {
        TagVO categoryVO;
        try {
            categoryVO = tagService.fetch(id);
        } catch (Exception e) {
            logger.error("Fetch posts error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categoryVO);
    }

    /**
     * 保存tag信息
     *
     * @param dto tag信息
     * @return tag信息
     */
    @PostMapping
    public ResponseEntity<TagVO> create(@Valid @RequestBody TagDTO dto) {
        TagVO vo;
        try {
            boolean existed = tagService.exists(dto.getName(), null);
            if (existed) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            vo = tagService.create(dto);
        } catch (Exception e) {
            logger.error("Save tag error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    /**
     * 修改tag信息
     *
     * @param id  主键
     * @param dto tag信息
     * @return 修改后的tag信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<TagVO> modify(@PathVariable Long id, @Valid @RequestBody TagDTO dto) {
        TagVO vo;
        try {
            boolean existed = tagService.exists(dto.getName(), id);
            if (existed) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            vo = tagService.modify(id, dto);
        } catch (Exception e) {
            logger.error("Modify tag error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(vo);
    }

    /**
     * 删除tag信息
     *
     * @param id 主键
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        try {
            tagService.remove(id);
        } catch (Exception e) {
            logger.error("Remove tag error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().build();
    }
}
