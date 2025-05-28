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

import io.leafage.assets.service.RegionService;
import io.leafage.assets.vo.RegionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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
     * @return 查询的数据集，异常时返回204状态码
     */
    @GetMapping
    public ResponseEntity<Mono<Page<RegionVO>>> retrieve(@RequestParam int page, @RequestParam int size,
                                                         String sortBy, boolean descending) {
        Mono<Page<RegionVO>> pageMono;
        try {
            pageMono = regionService.retrieve(page, size, sortBy, descending);
        } catch (Exception e) {
            logger.error("Retrieve regions occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pageMono);
    }

    /**
     * 根据 id 查询
     *
     * @param id 主键
     * @return 查询的数据，异常时返回204状态码
     */
    @GetMapping("/{id}")
    public ResponseEntity<Mono<RegionVO>> fetch(@PathVariable Long id) {
        Mono<RegionVO> voMono;
        try {
            voMono = regionService.fetch(id);
        } catch (Exception e) {
            logger.error("Fetch region occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voMono);
    }

    /**
     * 查询下级数据
     *
     * @param id a {@link java.lang.Long} object
     * @return 查询到的数据，否则返回空
     */
    @GetMapping("/{id}/subset")
    public ResponseEntity<Flux<RegionVO>> subset(@PathVariable Long id) {
        Flux<RegionVO> voFlux;
        try {
            voFlux = regionService.subset(id);
        } catch (Exception e) {
            logger.info("Retrieve region subset occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voFlux);
    }

}
