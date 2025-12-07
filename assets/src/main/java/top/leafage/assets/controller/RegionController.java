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

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import top.leafage.assets.domain.dto.RegionDTO;
import top.leafage.assets.service.RegionService;
import top.leafage.common.poi.reactive.ReactiveExcelReader;

/**
 * region controller
 *
 * @author wq li
 */
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
    public Mono<ServerResponse> retrieve(@RequestParam int page, @RequestParam int size,
                                         String sortBy, boolean descending, String filters) {
        return regionService.retrieve(page, size, sortBy, descending, filters)
                .flatMap(voPage -> ServerResponse.ok().bodyValue(voPage));
    }

    /**
     * 根据 id 查询
     *
     * @param id the pk.
     * @return 查询的数据
     */
    @GetMapping("/{id}")
    public Mono<ServerResponse> fetch(@PathVariable Long id) {
        return regionService.fetch(id)
                .flatMap(vo -> ServerResponse.ok().bodyValue(vo))
                .onErrorResume(ResponseStatusException.class,
                        e -> ServerResponse.notFound().build());
    }

    /**
     * 查询下级数据
     *
     * @param id a {@link java.lang.Long} object
     * @return 查询到的数据，否则返回空
     */
    @GetMapping("/{id}/subset")
    public Mono<ServerResponse> subset(@PathVariable Long id) {
        return regionService.subset(id)
                .collectList()
                .flatMap(voList -> ServerResponse.ok().bodyValue(voList));
    }

    /**
     * 添加信息
     *
     * @param dto 要添加的数据
     * @return 修改后的信息
     */
    @PostMapping
    public Mono<ServerResponse> create(@RequestBody @Valid RegionDTO dto) {
        return regionService.create(dto)
                .flatMap(vo -> ServerResponse.status(HttpStatus.CREATED).bodyValue(vo))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    /**
     * 修改信息
     *
     * @param id  user the pk.
     * @param dto 要修改的数据
     * @return 修改后的信息
     */
    @PutMapping("/{id}")
    public Mono<ServerResponse> modify(@PathVariable Long id, @RequestBody @Valid RegionDTO dto) {
        return regionService.modify(id, dto)
                .flatMap(vo -> ServerResponse.status(HttpStatus.CREATED).bodyValue(vo))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    /**
     * 删除信息
     *
     * @param id user the pk.
     * @return 200状态码
     */
    @DeleteMapping("/{id}")
    public Mono<ServerResponse> remove(@PathVariable Long id) {
        return regionService.remove(id)
                .then(ServerResponse.noContent().build())
                .onErrorResume(ResponseStatusException.class,
                        e -> ServerResponse.notFound().build());
    }

    /**
     * Import the records.
     *
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_schemas:import')")
    @PostMapping("/import")
    public Mono<ServerResponse> importFromFile(FilePart file) {
        return ReactiveExcelReader.read(file, RegionDTO.class)
                .flatMapMany(regionService::createAll)
                .collectList()
                .flatMap(vo -> ServerResponse.ok().bodyValue(vo))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
}
