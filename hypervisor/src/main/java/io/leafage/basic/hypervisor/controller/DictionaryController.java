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

package io.leafage.basic.hypervisor.controller;

import io.leafage.basic.hypervisor.dto.DictionaryDTO;
import io.leafage.basic.hypervisor.service.DictionaryService;
import io.leafage.basic.hypervisor.vo.DictionaryVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * dictionary controller
 *
 * @author wq li
 */
@Validated
@RestController
@RequestMapping("/dictionaries")
public class DictionaryController {

    private final Logger logger = LoggerFactory.getLogger(DictionaryController.class);

    private final DictionaryService dictionaryService;

    /**
     * <p>Constructor for DictionaryController.</p>
     *
     * @param dictionaryService a {@link DictionaryService} object
     */
    public DictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 大小
     * @return 查询的数据集，异常时返回204状态码
     */
    @GetMapping
    public ResponseEntity<Mono<Page<DictionaryVO>>> retrieve(@RequestParam int page, @RequestParam int size,
                                                             String sortBy, boolean descending) {
        Mono<Page<DictionaryVO>> pageMono;
        try {
            pageMono = dictionaryService.retrieve(page, size, sortBy, descending);
        } catch (Exception e) {
            logger.error("Retrieve dictionary occurred an error: ", e);
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
    public ResponseEntity<Mono<DictionaryVO>> fetch(@PathVariable Long id) {
        Mono<DictionaryVO> voMono;
        try {
            voMono = dictionaryService.fetch(id);
        } catch (Exception e) {
            logger.error("Fetch dictionary occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voMono);
    }

    /**
     * 查询下级数据
     *
     * @param id 主键
     * @return 查询到的数据，否则返回空
     */
    @GetMapping("/{id}/subset")
    public ResponseEntity<Flux<DictionaryVO>> subset(@PathVariable Long id) {
        Flux<DictionaryVO> voFlux;
        try {
            voFlux = dictionaryService.subset(id);
        } catch (Exception e) {
            logger.info("Retrieve dictionaries lower occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voFlux);
    }

    /**
     * 添加
     *
     * @param dictionaryDTO 要添加的数据
     * @return 添加后的信息，异常时返回417状态码
     */
    @PostMapping
    public ResponseEntity<Mono<DictionaryVO>> create(@RequestBody @Valid DictionaryDTO dictionaryDTO) {
        Mono<DictionaryVO> voMono;
        try {
            voMono = dictionaryService.create(dictionaryDTO);
        } catch (Exception e) {
            logger.info("Create dictionary occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(voMono);
    }

}
