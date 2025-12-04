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

import top.leafage.assets.dto.TagDTO;
import top.leafage.assets.service.TagService;
import top.leafage.assets.vo.TagVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.management.openmbean.KeyAlreadyExistsException;


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
    public Mono<Page<TagVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                      String sortBy, boolean descending, String filters) {
        return tagService.retrieve(page, size, sortBy, descending, filters)
                .doOnError(e -> logger.error("Retrieve tags error: ", e));
    }

    /**
     * 根据 id 查询信息
     *
     * @param id 主键
     * @return 查询到数据，异常时返回204
     */
    @GetMapping("/{id}")
    public Mono<TagVO> fetch(@PathVariable Long id) {
        return tagService.fetch(id)
                .doOnError(e -> logger.error("Fetch tag error: ", e));
    }

    /**
     * 添加信息
     *
     * @param dto 要添加的数据
     * @return 添加后的信息
     */
    @PostMapping
    public Mono<TagVO> create(@RequestBody @Valid TagDTO dto) {
        return tagService.exists(dto.getName(), null).flatMap(exists -> {
            if (exists) {
                return Mono.error(new KeyAlreadyExistsException("Already exists: " + dto.getName()));
            } else {
                return tagService.create(dto);
            }
        }).doOnError(e -> logger.error("Create tag occurred an error: ", e));
    }

    /**
     * 修改信息
     *
     * @param id  主键
     * @param dto 要修改的数据
     * @return 修改后的信息
     */
    @PutMapping("/{id}")
    public Mono<TagVO> modify(@PathVariable Long id, @RequestBody @Valid TagDTO dto) {
        return tagService.exists(dto.getName(), id).flatMap(exists -> {
            if (exists) {
                return Mono.error(new KeyAlreadyExistsException("Already exists: " + dto.getName()));
            } else {
                return tagService.modify(id, dto);
            }
        }).doOnError(e -> logger.error("Modify tag occurred an error: ", e));
    }

    /**
     * 删除信息
     *
     * @param id 主键
     * @return 200状态码
     */
    @DeleteMapping("/{id}")
    public Mono<Void> remove(@PathVariable Long id) {
        return tagService.remove(id)
                .doOnError(e -> logger.error("Remove tag error: ", e));
    }

}
