/*
 * Copyright (c) 2024-2025.  little3201.
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.leafage.common.TreeNode;
import top.leafage.common.poi.ExcelReader;

import java.security.Principal;
import java.util.List;

/**
 * privilege controller.
 *
 * @author wq li
 */
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
     * Retrieves a paginated list of records.
     *
     * @param page       The page number.
     * @param size       The number of records per page.
     * @param sortBy     The field to sort by.
     * @param descending Whether sorting should be in descending order.
     * @param filters    The filters.
     * @return A paginated list of records, or 204 status code if an error occurs.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_privileges')")
    @GetMapping
    public ResponseEntity<Page<PrivilegeVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                      String sortBy, boolean descending, String filters) {
        Page<PrivilegeVO> voPage;
        try {
            voPage = privilegeService.retrieve(page, size, sortBy, descending, filters);
        } catch (Exception e) {
            logger.info("Retrieve privilege error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voPage);
    }

    /**
     * 查询树形数据
     *
     * @return 查询到的数据，否则返回空
     */
    @GetMapping("/tree")
    public ResponseEntity<List<TreeNode<Long>>> tree(Principal principal) {
        List<TreeNode<Long>> treeNodes;
        try {
            treeNodes = privilegeService.tree(principal.getName());
        } catch (Exception e) {
            logger.info("Retrieve privilege tree error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(treeNodes);
    }

    /**
     * 查询信息
     *
     * @param superiorId 主键
     * @return 查询到的信息，否则返回204状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_privileges')")
    @GetMapping("/{superiorId}/subset")
    public ResponseEntity<List<PrivilegeVO>> subset(@PathVariable Long superiorId) {
        List<PrivilegeVO> voList;
        try {
            voList = privilegeService.subset(superiorId);
        } catch (Exception e) {
            logger.info("Retrieve privilege subset error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voList);
    }

    /**
     * 查询信息
     *
     * @param id 主键
     * @return 查询到的信息，否则返回204状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_privileges')")
    @GetMapping("/{id}")
    public ResponseEntity<PrivilegeVO> fetch(@PathVariable Long id) {
        PrivilegeVO vo;
        try {
            vo = privilegeService.fetch(id);
        } catch (Exception e) {
            logger.info("Fetch privilege error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(vo);
    }

    /**
     * 修改信息
     *
     * @param id  主键
     * @param dto 要添加的数据
     * @return 编辑后的信息，否则返回417状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_privileges:modify')")
    @PutMapping("/{id}")
    public ResponseEntity<PrivilegeVO> modify(@PathVariable Long id, @Valid @RequestBody PrivilegeDTO dto) {
        PrivilegeVO vo;
        try {
            boolean existed = privilegeService.exists(dto.getName(), id);
            if (existed) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            vo = privilegeService.modify(id, dto);
        } catch (Exception e) {
            logger.error("Modify privilege error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(vo);
    }

    /**
     * 启用、停用
     *
     * @param id 主键
     * @return 编辑后的信息，否则返回417状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_privileges:enable')")
    @PatchMapping("/{id}")
    public ResponseEntity<Boolean> enable(@PathVariable Long id) {
        boolean enabled;
        try {
            enabled = privilegeService.enable(id);
        } catch (Exception e) {
            logger.error("Modify privilege error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(enabled);
    }

    /**
     * Import the records.
     *
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_privileges:import')")
    @PostMapping("/import")
    public ResponseEntity<List<PrivilegeVO>> importFromFile(MultipartFile file) {
        List<PrivilegeVO> voList;
        try {
            List<PrivilegeDTO> dtoList = ExcelReader.read(file.getInputStream(), PrivilegeDTO.class);
            voList = privilegeService.createAll(dtoList);
        } catch (Exception e) {
            logger.error("Import privilege error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().body(voList);
    }

}
