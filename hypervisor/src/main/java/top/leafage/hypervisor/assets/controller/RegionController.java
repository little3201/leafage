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

package top.leafage.hypervisor.assets.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.assets.domain.dto.RegionDTO;
import top.leafage.hypervisor.assets.domain.vo.RegionVO;
import top.leafage.hypervisor.assets.service.RegionService;
import top.leafage.common.poi.reactive.ReactiveExcelReader;

/**
 * region controller
 *
 * @author wq li
 */
@RestController
@RequestMapping("/regions")
public class RegionController {

    private final RegionService regionService;

    /**
     * Constructor for RegionController.
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
        return regionService.retrieve(page, size, sortBy, descending, filters);
    }

    /**
     * 根据 id 查询
     *
     * @param id the pk.
     * @return 查询的数据
     */
    @GetMapping("/{id}")
    public Mono<RegionVO> fetch(@PathVariable Long id) {
        return regionService.fetch(id);
    }

    /**
     * 查询下级数据
     *
     * @param id a {@link java.lang.Long} object
     * @return 查询到的数据，否则返回空
     */
    @GetMapping("/{id}/subset")
    public Flux<RegionVO> subset(@PathVariable Long id) {
        return regionService.subset(id);
    }

    /**
     * 添加信息
     *
     * @param dto 要添加的数据
     * @return 修改后的信息
     */
    @PostMapping
    public Mono<RegionVO> create(@RequestBody @Valid RegionDTO dto) {
        return regionService.create(dto);
    }

    /**
     * 修改信息
     *
     * @param id  user the pk.
     * @param dto 要修改的数据
     * @return 修改后的信息
     */
    @PutMapping("/{id}")
    public Mono<RegionVO> modify(@PathVariable Long id, @RequestBody @Valid RegionDTO dto) {
        return regionService.modify(id, dto);
    }

    /**
     * 删除信息
     *
     * @param id user the pk.
     * @return 200状态码
     */
    @DeleteMapping("/{id}")
    public Mono<Void> remove(@PathVariable Long id) {
        return regionService.remove(id);
    }

    /**
     * Enable a record when enabled is false or disable when enabled is ture.
     *
     * @param id The record ID.
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_regions:enable')")
    @PatchMapping("/{id}")
    public Mono<Boolean> enable(@PathVariable Long id) {
        return regionService.enable(id);
    }

    /**
     * Import the records.
     *
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_schemas:import')")
    @PostMapping("/import")
    public Flux<RegionVO> importFromFile(FilePart file) {
        return ReactiveExcelReader.read(file, RegionDTO.class)
                .flatMapMany(regionService::createAll);
    }
}
