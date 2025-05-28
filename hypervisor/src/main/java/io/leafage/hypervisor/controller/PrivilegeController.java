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
    public ResponseEntity<Mono<Page<PrivilegeVO>>> retrieve(@RequestParam int page, @RequestParam int size,
                                                            String sortBy, boolean descending) {
        Mono<Page<PrivilegeVO>> pageMono;
        try {
            pageMono = privilegeService.retrieve(page, size, sortBy, descending);
        } catch (Exception e) {
            logger.error("Retrieve privilege occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pageMono);
    }

    /**
     * 树形查询
     *
     * @return 查询到的数据，否则返回空
     */
    @GetMapping("/tree")
    public ResponseEntity<Mono<List<TreeNode>>> tree(Principal principal) {
        Mono<List<TreeNode>> authorities;
        try {
            authorities = privilegeService.tree(principal.getName());
        } catch (Exception e) {
            logger.info("Retrieve privileges occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(authorities);
    }

    /**
     * 查询
     *
     * @return 查询到的数据，否则返回空
     */
    @GetMapping("/{superiorId}/subset")
    public ResponseEntity<Flux<PrivilegeVO>> subset(@PathVariable Long superiorId) {
        Flux<PrivilegeVO> voFlux;
        try {
            voFlux = privilegeService.subset(superiorId);
        } catch (Exception e) {
            logger.info("Retrieve privilege subset occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voFlux);
    }

    /**
     * 根据 id 查询信息
     *
     * @param id 主键
     * @return 查询的数据，异常时返回204状态码
     */
    @GetMapping("/{id}")
    public ResponseEntity<Mono<PrivilegeVO>> fetch(@PathVariable Long id) {
        Mono<PrivilegeVO> voMono;
        try {
            voMono = privilegeService.fetch(id);
        } catch (Exception e) {
            logger.error("Fetch privilege occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voMono);
    }

    /**
     * 是否已存在
     *
     * @param name 名称
     * @return true-是，false-否
     */
    @GetMapping("/exists")
    public ResponseEntity<Mono<Boolean>> exists(@RequestParam String name, Long id) {
        Mono<Boolean> existsMono;
        try {
            existsMono = privilegeService.exists(name, id);
        } catch (Exception e) {
            logger.error("Check privilege is exists occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(existsMono);
    }

    /**
     * 添加
     *
     * @param privilegeDTO 要添加的数据
     * @return 添加后的信息，异常时返回417状态码
     */
    @PostMapping
    public ResponseEntity<Mono<PrivilegeVO>> create(@RequestBody @Valid PrivilegeDTO privilegeDTO) {
        Mono<PrivilegeVO> voMono;
        try {
            voMono = privilegeService.create(privilegeDTO);
        } catch (Exception e) {
            logger.error("Create privilege occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(voMono);
    }

    /**
     * 修改
     *
     * @param id           主键
     * @param privilegeDTO 要修改的数据
     * @return 修改后的信息，异常时返回304状态码
     */
    @PutMapping("/{id}")
    public ResponseEntity<Mono<PrivilegeVO>> modify(@PathVariable Long id, @RequestBody @Valid PrivilegeDTO privilegeDTO) {
        Mono<PrivilegeVO> voMono;
        try {
            voMono = privilegeService.modify(id, privilegeDTO);
        } catch (Exception e) {
            logger.error("Modify privilege occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(voMono);
    }

}
