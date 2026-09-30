/*
 * Copyright(c) 2019-present the original author or authors.
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

package top.leafage.hypervisor.docs.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.leafage.common.data.core.domain.TreeNode;
import top.leafage.hypervisor.docs.domain.Section;
import top.leafage.hypervisor.docs.domain.dto.SectionDTO;
import top.leafage.hypervisor.docs.domain.dto.SectionDataDTO;
import top.leafage.hypervisor.docs.domain.dto.SectionFieldDTO;
import top.leafage.hypervisor.docs.domain.vo.SectionDataVO;
import top.leafage.hypervisor.docs.domain.vo.SectionFieldVO;
import top.leafage.hypervisor.docs.domain.vo.SectionVO;
import top.leafage.hypervisor.docs.service.SectionService;

import java.util.List;

/**
 * Section controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/sections")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }


    /**
     * Fetch.
     *
     * @param id The pk.
     * @return the result.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('sections')")
    @GetMapping("/{id}")
    public ResponseEntity<SectionVO> fetch(@PathVariable Long id) {
        SectionVO vo = sectionService.fetch(id);
        return ResponseEntity.ok(vo);
    }

    /**
     * tree.
     *
     * @param ownerId   The pk.
     * @param ownerType tye type.
     * @return the result.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('sections')")
    @GetMapping("/{ownerId}/tree")
    public ResponseEntity<List<TreeNode<Long>>> tree(@PathVariable Long ownerId, @RequestParam String ownerType) {
        List<TreeNode<Long>> treeNodes = sectionService.tree(ownerId, Section.OwnerType.of(ownerType));
        return ResponseEntity.ok(treeNodes);
    }

    /**
     * 查询 fields.
     *
     * @param id The pk.
     * @return 查询的数据集，异常时返回204状态码
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('sections:field')")
    @GetMapping("/{id}/fields")
    public ResponseEntity<List<SectionFieldVO>> fields(@PathVariable Long id) {
        List<SectionFieldVO> voList = sectionService.fields(id);
        return ResponseEntity.ok(voList);
    }

    /**
     * 查询 datas.
     *
     * @param id The pk.
     * @return 查询的数据集，异常时返回204状态码
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('sections:data')")
    @GetMapping("/{id}/datas")
    public ResponseEntity<List<SectionDataVO>> datas(@PathVariable Long id) {
        List<SectionDataVO> voList = sectionService.datas(id);
        return ResponseEntity.ok(voList);
    }

    /**
     * create.
     *
     * @param dto the request body.
     * @return the result.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('sections:create')")
    @PostMapping
    public ResponseEntity<SectionVO> create(@Valid @RequestBody SectionDTO dto) {
        SectionVO vo = sectionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    /**
     * Create field.
     *
     * @param dto the request body.
     * @return 查询的数据集，异常时返回204状态码
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('sections:field')")
    @PostMapping("/fields")
    public ResponseEntity<SectionFieldVO> createField(@RequestBody SectionFieldDTO dto) {
        SectionFieldVO vo = sectionService.createField(dto);
        return ResponseEntity.ok(vo);
    }

    /**
     * Create data.
     *
     * @param dto the request body.
     * @return 查询的数据集，异常时返回204状态码
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('sections:data')")
    @PostMapping("/datas")
    public ResponseEntity<SectionDataVO> createData(@RequestBody SectionDataDTO dto) {
        SectionDataVO vo = sectionService.createData(dto);
        return ResponseEntity.ok(vo);
    }

    /**
     * modify.
     *
     * @param id  The pk.
     * @param dto the request body.
     * @return the result.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('sections:modify')")
    @PutMapping("/{id}")
    public ResponseEntity<SectionVO> modify(@PathVariable Long id, @RequestBody SectionDTO dto) {
        SectionVO vo = sectionService.modify(id, dto);
        return ResponseEntity.accepted().body(vo);
    }

    /**
     * Modify field.
     *
     * @param id  the pk of field.
     * @param dto the request body.
     * @return 查询的数据集，异常时返回204状态码
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('sections:field')")
    @PutMapping("/fields/{id}")
    public ResponseEntity<SectionFieldVO> modifyField(@PathVariable Long id, @RequestBody SectionFieldDTO dto) {
        SectionFieldVO vo = sectionService.modifyField(id, dto);
        return ResponseEntity.ok(vo);
    }

    /**
     * Modify data.
     *
     * @param id  the pk of field.
     * @param dto the request body.
     * @return 查询的数据集，异常时返回204状态码
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('sections:data')")
    @PutMapping("/datas/{id}")
    public ResponseEntity<SectionDataVO> modifyData(@PathVariable Long id, @RequestBody SectionDataDTO dto) {
        SectionDataVO vo = sectionService.modifyData(id, dto);
        return ResponseEntity.ok(vo);
    }

    /**
     * Remove.
     *
     * @param id The pk.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('sections:remove')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        sectionService.remove(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Remove section field.
     *
     * @param id the pk of section field.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('sections:remove')")
    @DeleteMapping("/fields/{id}")
    public ResponseEntity<Void> removeField(@PathVariable Long id) {
        sectionService.removeField(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Remove section data.
     *
     * @param id the pk of section data.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('sections:remove')")
    @DeleteMapping("/datas/{id}")
    public ResponseEntity<Void> removeData(@PathVariable Long id) {
        sectionService.removeData(id);
        return ResponseEntity.ok().build();
    }

}
