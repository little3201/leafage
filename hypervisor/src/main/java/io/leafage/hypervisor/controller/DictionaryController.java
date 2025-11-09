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

package io.leafage.hypervisor.controller;

import io.leafage.hypervisor.dto.DictionaryDTO;
import io.leafage.hypervisor.service.DictionaryService;
import io.leafage.hypervisor.vo.DictionaryVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.poi.reactive.ReactiveExcelReader;

import javax.management.openmbean.KeyAlreadyExistsException;

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
     * @return 查询的数据集
     */
    @GetMapping
    public Mono<Page<DictionaryVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                             String sortBy, boolean descending, String filters) {
        return dictionaryService.retrieve(page, size, sortBy, descending, filters)
                .doOnError(e -> logger.error("Retrieve dictionaries error: ", e));
    }

    /**
     * 根据 id 查询
     *
     * @param id 主键
     * @return 查询的数据
     */
    @GetMapping("/{id}")
    public Mono<DictionaryVO> fetch(@PathVariable Long id) {
        return dictionaryService.fetch(id)
                .doOnError(e -> logger.error("Fetch dictionary error: ", e));
    }

    /**
     * 查询下级数据
     *
     * @param id 主键
     * @return 查询到的数据，否则返回空
     */
    @GetMapping("/{id}/subset")
    public Flux<DictionaryVO> subset(@PathVariable Long id) {
        return dictionaryService.subset(id)
                .doOnError(e -> logger.error("Retrieve dictionary subset error: ", e));
    }

    /**
     * 添加
     *
     * @param dto 要添加的数据
     * @return 添加后的信息
     */
    @PostMapping
    public Mono<DictionaryVO> create(@RequestBody @Valid DictionaryDTO dto) {
        return dictionaryService.exists(dto.getName(), null).flatMap(exists -> {
            if (exists) {
                return Mono.error(new KeyAlreadyExistsException("Already exists: " + dto.getName()));
            } else {
                return dictionaryService.create(dto);
            }
        }).doOnError(e -> logger.error("Create dictionary occurred an error: ", e));
    }

    /**
     * 修改信息
     *
     * @param id  dictionary 主键
     * @param dto 要修改的数据
     * @return 修改后的信息
     */
    @PutMapping("/{id}")
    public Mono<DictionaryVO> modify(@PathVariable Long id, @RequestBody @Valid DictionaryDTO dto) {
        return dictionaryService.exists(dto.getName(), id).flatMap(exists -> {
            if (exists) {
                return Mono.error(new KeyAlreadyExistsException("Already exists: " + dto.getName()));
            } else {
                return dictionaryService.modify(id, dto);
            }
        }).doOnError(e -> logger.error("Modify dictionary occurred an error: ", e));
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return 200状态码
     */
    @DeleteMapping("/{id}")
    public Mono<Void> remove(@PathVariable Long id) {
        return dictionaryService.remove(id)
                .doOnError(e -> logger.error("Remove dictionary error: ", e));
    }

    /**
     * Import the records.
     *
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_dictionaries:import')")
    @PostMapping("/import")
    public Flux<DictionaryVO> importFromFile(FilePart file) {
        return ReactiveExcelReader.read(file, DictionaryDTO.class)
                .flatMapMany(dictionaryService::createAll)
                .onErrorMap(e -> {
                    logger.error("Failed import from file: ", e);
                    return new RuntimeException("Failed import from file", e);
                });
    }
}
