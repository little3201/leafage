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

package top.leafage.hypervisor.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.data.domain.TreeNode;
import top.leafage.common.poi.reactive.ReactiveExcelReader;
import top.leafage.hypervisor.domain.dto.PrivilegeDTO;
import top.leafage.hypervisor.domain.vo.PrivilegeVO;
import top.leafage.hypervisor.service.PrivilegeService;

import java.security.Principal;
import java.util.List;

/**
 * privilege controller
 *
 * @author wq li
 */
@RestController
@RequestMapping("/privileges")
public class PrivilegeController {

    private final PrivilegeService privilegeService;

    /**
     * Constructor for PrivilegeController.
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
     * @return 查询的数据集
     */
    @GetMapping
    public Mono<Page<PrivilegeVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                            String sortBy, boolean descending, String filters) {
        return privilegeService.retrieve(page, size, sortBy, descending, filters);
    }

    /**
     * 树形查询
     *
     * @return 查询到的数据，否则返回空
     */
    @GetMapping("/tree")
    public Mono<List<TreeNode<Long>>> tree(Principal principal) {
        return privilegeService.tree(principal.getName());
    }

    /**
     * 根据 id 查询信息
     *
     * @param id the pk.
     * @return 查询的数据
     */
    @GetMapping("/{id}")
    public Mono<PrivilegeVO> fetch(@PathVariable Long id) {
        return privilegeService.fetch(id);
    }

    /**
     * 查询
     *
     * @return 查询到的数据，否则返回空
     */
    @GetMapping("/{superiorId}/subset")
    public Flux<PrivilegeVO> subset(@PathVariable Long superiorId) {
        return privilegeService.subset(superiorId);
    }

    /**
     * 添加
     *
     * @param dto 要添加的数据
     * @return 添加后的信息
     */
    @PostMapping
    public Mono<PrivilegeVO> create(@RequestBody @Valid PrivilegeDTO dto) {
        return privilegeService.create(dto);
    }

    /**
     * 修改
     *
     * @param id  the pk.
     * @param dto 要修改的数据
     * @return 修改后的信息
     */
    @PutMapping("/{id}")
    public Mono<PrivilegeVO> modify(@PathVariable Long id, @RequestBody @Valid PrivilegeDTO dto) {
        return privilegeService.modify(id, dto);
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
        return privilegeService.enable(id);
    }

    /**
     * Import the records.
     *
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_privileges:import')")
    @PostMapping("/import")
    public Flux<PrivilegeVO> importFromFile(FilePart file) {
        return ReactiveExcelReader.read(file, PrivilegeDTO.class)
                .flatMapMany(privilegeService::createAll);
    }
}
