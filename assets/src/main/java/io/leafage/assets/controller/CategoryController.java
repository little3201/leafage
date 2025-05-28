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

import io.leafage.assets.dto.CategoryDTO;
import io.leafage.assets.service.CategoryService;
import io.leafage.assets.vo.CategoryVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


/**
 * category controller
 *
 * @author wq li
 */
@Validated
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    /**
     * <p>Constructor for CategoryController.</p>
     *
     * @param categoryService a {@link CategoryService} object
     */
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 分页查询
     *
     * @param page 分页位置
     * @param size 分页大小
     * @return 查询到数据集，异常时返回204
     */
    @GetMapping
    public ResponseEntity<Mono<Page<CategoryVO>>> retrieve(@RequestParam int page, @RequestParam int size,
                                                           String sortBy, boolean descending) {
        Mono<Page<CategoryVO>> pageMono;
        try {
            pageMono = categoryService.retrieve(page, size, sortBy, descending);
        } catch (Exception e) {
            logger.error("Retrieve categories occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pageMono);
    }

    /**
     * 根据 id 查询信息
     *
     * @param id 主键
     * @return 查询到数据，异常时返回204
     */
    @GetMapping("/{id}")
    public ResponseEntity<Mono<CategoryVO>> fetch(@PathVariable Long id) {
        Mono<CategoryVO> voMono;
        try {
            voMono = categoryService.fetch(id);
        } catch (Exception e) {
            logger.error("Fetch category occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voMono);
    }

    /**
     * 是否已存在
     *
     * @param name 名称
     * @return true-是，false-否
     */
    @GetMapping("/exists")
    public ResponseEntity<Mono<Boolean>> exists(@RequestParam String name, Long id) {
        Mono<Boolean> existsMono;
        try {
            existsMono = categoryService.exists(name, id);
        } catch (Exception e) {
            logger.error("Check category is exists occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(existsMono);
    }

    /**
     * 添加信息
     *
     * @param categoryDTO 要添加的数据
     * @return 添加后的信息，异常时返回417状态码
     */
    @PostMapping
    public ResponseEntity<Mono<CategoryVO>> create(@RequestBody @Valid CategoryDTO categoryDTO) {
        Mono<CategoryVO> voMono;
        try {
            voMono = categoryService.create(categoryDTO);
        } catch (Exception e) {
            logger.error("Create category occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(voMono);
    }

    /**
     * 修改信息
     *
     * @param id          主键
     * @param categoryDTO 要修改的数据
     * @return 修改后的信息，异常时返回304状态码
     */
    @PutMapping("/{id}")
    public ResponseEntity<Mono<CategoryVO>> modify(@PathVariable Long id, @RequestBody @Valid CategoryDTO categoryDTO) {
        Mono<CategoryVO> voMono;
        try {
            voMono = categoryService.modify(id, categoryDTO);
        } catch (Exception e) {
            logger.error("Modify category occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(voMono);
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
            voidMono = categoryService.remove(id);
        } catch (Exception e) {
            logger.error("Remove category occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok(voidMono);
    }

}
