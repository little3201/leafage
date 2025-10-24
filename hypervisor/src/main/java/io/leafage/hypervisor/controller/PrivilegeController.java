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

import io.leafage.hypervisor.dto.PrivilegeDTO;
import io.leafage.hypervisor.service.PrivilegeService;
import io.leafage.hypervisor.vo.PrivilegeVO;
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
import top.leafage.common.TreeNode;

import java.security.Principal;
import java.util.List;

/**
 * privilege controller
 *
 * @author wq li
 */
@Validated
@RestController
@RequestMapping("/privileges")
public class PrivilegeController {

    private final Logger logger = LoggerFactory.getLogger(PrivilegeController.class);

    private final PrivilegeService privilegeService;

    /**
     * <p>Constructor for PrivilegeController.</p>
     *
     * @param privilegeService a {@link PrivilegeService} object
     */
    public PrivilegeController(PrivilegeService privilegeService) {
        this.privilegeService = privilegeService;
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 大小
     * @return 查询的数据集，异常时返回204状态码
     */
    @GetMapping
    public Mono<ResponseEntity<Page<PrivilegeVO>>> retrieve(@RequestParam int page, @RequestParam int size,
                                                            String sortBy, boolean descending, String filters) {
        return privilegeService.retrieve(page, size, sortBy, descending, filters)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.error("Retrieve privileges error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    /**
     * 树形查询
     *
     * @return 查询到的数据，否则返回空
     */
    @GetMapping("/tree")
    public Mono<ResponseEntity<List<TreeNode<Long>>>> tree(Principal principal) {
        return privilegeService.tree(principal.getName())
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.error("Retrieve privilege tree error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    /**
     * 根据 id 查询信息
     *
     * @param id 主键
     * @return 查询的数据，异常时返回204状态码
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<PrivilegeVO>> fetch(@PathVariable Long id) {
        return privilegeService.fetch(id)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.error("Fetch privilege error: ", e);
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
        return privilegeService.exists(name, id)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.error("Check is exists error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    /**
     * 查询
     *
     * @return 查询到的数据，否则返回空
     */
    @GetMapping("/{superiorId}/subset")
    public Flux<PrivilegeVO> subset(@PathVariable Long superiorId) {
        return privilegeService.subset(superiorId)
                .onErrorResume(e -> {
                    logger.error("Retrieve privilege subset error: ", e);
                    return Flux.empty();
                });
    }

    /**
     * 添加
     *
     * @param dto 要添加的数据
     * @return 添加后的信息
     */
    @PostMapping
    public Mono<ResponseEntity<PrivilegeVO>> create(@RequestBody @Valid PrivilegeDTO dto) {
        return privilegeService.create(dto)
                .map(vo -> ResponseEntity.status(HttpStatus.CREATED).body(vo))
                .onErrorResume(e -> {
                    logger.error("Create privilege occurred an error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build());
                });
    }

    /**
     * 修改
     *
     * @param id           主键
     * @param dto 要修改的数据
     * @return 修改后的信息
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<PrivilegeVO>> modify(@PathVariable Long id, @RequestBody @Valid PrivilegeDTO dto) {
        return privilegeService.modify(id, dto)
                .map(vo -> ResponseEntity.accepted().body(vo))
                .onErrorResume(e -> {
                    logger.error("Modify privilege occurred an error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build());
                });
    }

}
