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

import io.leafage.assets.dto.RegionDTO;
import io.leafage.assets.service.RegionService;
import io.leafage.assets.vo.RegionVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * region controller
 *
 * @author wq li
 */
@Validated
@RestController
@RequestMapping("/regions")
public class RegionController {

    private final Logger logger = LoggerFactory.getLogger(RegionController.class);

    private final RegionService regionService;

    /**
     * <p>Constructor for RegionController.</p>
     *
     * @param regionService a {@link RegionService} object
     */
    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 大小
     * @return 查询的数据集
     */
    @GetMapping
    public Mono<Page<RegionVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                         String sortBy, boolean descending, String filters) {
        return regionService.retrieve(page, size, sortBy, descending, filters)
                .doOnError(e -> logger.error("Retrieve regions error: ", e));
    }

    /**
     * 根据 id 查询
     *
     * @param id 主键
     * @return 查询的数据
     */
    @GetMapping("/{id}")
    public Mono<RegionVO> fetch(@PathVariable Long id) {
        return regionService.fetch(id)
                .doOnError(e -> logger.error("Fetch region error: ", e));
    }

    /**
     * 是否已存在
     *
     * @param title 标题
     * @return true-是，false-否
     */
    @GetMapping("/exists")
    public Mono<Boolean> exists(@RequestParam String title, Long id) {
        return regionService.exists(title, id)
                .doOnError(e -> logger.error("Check is exists error: ", e));
    }

    /**
     * 查询下级数据
     *
     * @param id a {@link java.lang.Long} object
     * @return 查询到的数据，否则返回空
     */
    @GetMapping("/{id}/subset")
    public Flux<RegionVO> subset(@PathVariable Long id) {
        return regionService.subset(id)
                .doOnError(e -> logger.error("Retrieve region subset error: ", e));
    }

    /**
     * 添加信息
     *
     * @param dto 要添加的数据
     * @return 修改后的信息
     */
    @PostMapping
    public Mono<RegionVO> create(@RequestBody @Valid RegionDTO dto) {
        return regionService.create(dto)
                .doOnError(e -> logger.error("Create region occurred an error: ", e));
    }

    /**
     * 修改信息
     *
     * @param id  user 主键
     * @param dto 要修改的数据
     * @return 修改后的信息
     */
    @PutMapping("/{id}")
    public Mono<RegionVO> modify(@PathVariable Long id, @RequestBody @Valid RegionDTO dto) {
        return regionService.modify(id, dto)
                .doOnError(e -> logger.error("Modify region occurred an error: ", e));
    }

    /**
     * 删除信息
     *
     * @param id user 主键
     * @return 200状态码
     */
    @DeleteMapping("/{id}")
    public Mono<Void> remove(@PathVariable Long id) {
        return regionService.remove(id)
                .doOnError(e -> logger.error("Remove region error: ", e));
    }
}
