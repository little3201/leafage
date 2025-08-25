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

import io.leafage.assets.dto.TagDTO;
import io.leafage.assets.service.TagService;
import io.leafage.assets.vo.TagVO;
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
 * tag controller
 *
 * @author wq li
 */
@Validated
@RestController
@RequestMapping("/tags")
public class TagController {

    private final Logger logger = LoggerFactory.getLogger(TagController.class);

    private final TagService tagService;

    /**
     * <p>Constructor for CategoryController.</p>
     *
     * @param tagService a {@link TagService} object
     */
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * 分页查询
     *
     * @param page 分页位置
     * @param size 分页大小
     * @return 查询到数据集，异常时返回204
     */
    @GetMapping
    public Mono<ResponseEntity<Page<TagVO>>> retrieve(@RequestParam int page, @RequestParam int size,
                                                      String sortBy, boolean descending, String filters) {
        return tagService.retrieve(page, size, sortBy, descending, filters)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.error("Retrieve tags error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    /**
     * 根据 id 查询信息
     *
     * @param id 主键
     * @return 查询到数据，异常时返回204
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TagVO>> fetch(@PathVariable Long id) {
        return tagService.fetch(id)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.error("Fetch tag error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    /**
     * 是否已存在
     *
     * @param name 名称
     * @return true-是，false-否
     */
    @GetMapping("/exists")
    public Mono<ResponseEntity<Boolean>> exists(@RequestParam String name, Long id) {
        return tagService.exists(name, id)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.error("Check is exists error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    /**
     * 添加信息
     *
     * @param dto 要添加的数据
     * @return 添加后的信息，异常时返回417状态码
     */
    @PostMapping
    public Mono<ResponseEntity<TagVO>> create(@RequestBody @Valid TagDTO dto) {
        return tagService.create(dto)
                .map(vo -> ResponseEntity.status(HttpStatus.CREATED).body(vo))
                .onErrorResume(e -> {
                    logger.error("Create tag occurred an error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build());
                });
    }

    /**
     * 修改信息
     *
     * @param id  主键
     * @param dto 要修改的数据
     * @return 修改后的信息，异常时返回417状态码
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TagVO>> modify(@PathVariable Long id, @RequestBody @Valid TagDTO dto) {
        return tagService.modify(id, dto)
                .map(vo -> ResponseEntity.accepted().body(vo))
                .onErrorResume(e -> {
                    logger.error("Modify tag occurred an error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build());
                });
    }

    /**
     * 删除信息
     *
     * @param id 主键
     * @return 200状态码，异常时返回417状态码
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> remove(@PathVariable Long id) {
        return tagService.remove(id)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorResume(e -> {
                    logger.error("Remove tag error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build());
                });
    }

}
