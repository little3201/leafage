/*
 * Copyright (c) 2026.  little3201.
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

package top.leafage.hypervisor.system.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.poi.reactive.ReactiveExcelReader;
import top.leafage.hypervisor.system.domain.dto.DictionaryDTO;
import top.leafage.hypervisor.system.domain.vo.DictionaryVO;
import top.leafage.hypervisor.system.service.DictionaryService;

/**
 * dictionary controller
 *
 * @author wq li
 */
@RestController
@RequestMapping("/dictionaries")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    /**
     * Constructor for DictionaryController.
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
        return dictionaryService.retrieve(page, size, sortBy, descending, filters);
    }

    /**
     * 根据 id 查询
     *
     * @param id the pk.
     * @return 查询的数据
     */
    @GetMapping("/{id}")
    public Mono<DictionaryVO> fetch(@PathVariable Long id) {
        return dictionaryService.fetch(id);
    }

    /**
     * 查询下级数据
     *
     * @param id the pk.
     * @return 查询到的数据，否则返回空
     */
    @GetMapping("/{id}/subset")
    public Flux<DictionaryVO> subset(@PathVariable Long id) {
        return dictionaryService.subset(id);
    }

    /**
     * 添加
     *
     * @param dto 要添加的数据
     * @return 添加后的信息
     */
    @PostMapping
    public Mono<DictionaryVO> create(@RequestBody @Valid DictionaryDTO dto) {
        return dictionaryService.create(dto);
    }

    /**
     * 修改信息
     *
     * @param id  dictionary the pk.
     * @param dto 要修改的数据
     * @return 修改后的信息
     */
    @PutMapping("/{id}")
    public Mono<DictionaryVO> modify(@PathVariable Long id, @RequestBody @Valid DictionaryDTO dto) {
        return dictionaryService.modify(id, dto);
    }

    /**
     * 删除
     *
     * @param id the pk.
     * @return 200状态码
     */
    @DeleteMapping("/{id}")
    public Mono<Void> remove(@PathVariable Long id) {
        return dictionaryService.remove(id);
    }

    /**
     * Enable a record when enabled is false or disable when enabled is ture.
     *
     * @param id The record ID.
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_users:enable')")
    @PatchMapping("/{id}")
    public Mono<Boolean> enable(@PathVariable Long id) {
        return dictionaryService.enable(id);
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
                .flatMapMany(dictionaryService::createAll);
    }
}
