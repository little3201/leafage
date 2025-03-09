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
package io.leafage.basic.hypervisor.controller;

import io.leafage.basic.hypervisor.domain.RolePrivileges;
import io.leafage.basic.hypervisor.dto.PrivilegeDTO;
import io.leafage.basic.hypervisor.service.PrivilegeService;
import io.leafage.basic.hypervisor.service.RolePrivilegesService;
import io.leafage.basic.hypervisor.vo.PrivilegeVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.leafage.common.TreeNode;

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
    private final RolePrivilegesService rolePrivilegesService;

    /**
     * <p>Constructor for PrivilegeController.</p>
     *
     * @param privilegeService      a {@link io.leafage.basic.hypervisor.service.PrivilegeService} object
     * @param rolePrivilegesService a {@link io.leafage.basic.hypervisor.service.RolePrivilegesService} object
     */
    public PrivilegeController(PrivilegeService privilegeService, RolePrivilegesService rolePrivilegesService) {
        this.privilegeService = privilegeService;
        this.rolePrivilegesService = rolePrivilegesService;
    }

    /**
     * 分页查询
     *
     * @param page       页码
     * @param size       大小
     * @param sortBy     排序字段
     * @param descending 排序方向
     * @return 查询到的数据，否则返回空
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_privileges:read')")
    @GetMapping
    public ResponseEntity<Page<PrivilegeVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                      String sortBy, boolean descending, String name) {
        Page<PrivilegeVO> voPage;
        try {
            voPage = privilegeService.retrieve(page, size, sortBy, descending, name);
        } catch (Exception e) {
            logger.info("Retrieve privilege occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voPage);
    }

    /**
     * 查询树形数据
     *
     * @return 查询到的数据，否则返回空
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_privileges:read')")
    @GetMapping("/tree")
    public ResponseEntity<List<TreeNode>> tree(Principal principal) {
        List<TreeNode> treeNodes;
        try {
            treeNodes = privilegeService.tree(principal.getName());
        } catch (Exception e) {
            logger.info("Retrieve privilege tree occurred an error: ", e);
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
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_privileges:read')")
    @GetMapping("/{superiorId}/subset")
    public ResponseEntity<List<PrivilegeVO>> subset(@PathVariable Long superiorId) {
        List<PrivilegeVO> voList;
        try {
            voList = privilegeService.subset(superiorId);
        } catch (Exception e) {
            logger.info("Retrieve privilege subset occurred an error: ", e);
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
    @GetMapping("/{id}")
    public ResponseEntity<PrivilegeVO> fetch(@PathVariable Long id) {
        PrivilegeVO vo;
        try {
            vo = privilegeService.fetch(id);
        } catch (Exception e) {
            logger.info("Fetch privilege occurred an error: ", e);
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
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_privileges:write')")
    @PutMapping("/{id}")
    public ResponseEntity<PrivilegeVO> modify(@PathVariable Long id, @RequestBody @Valid PrivilegeDTO dto) {
        PrivilegeVO vo;
        try {
            vo = privilegeService.modify(id, dto);
        } catch (Exception e) {
            logger.error("Modify privilege occurred an error: ", e);
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
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_privileges:write')")
    @PatchMapping("/{id}")
    public ResponseEntity<Boolean> enable(@PathVariable Long id) {
        boolean enabled;
        try {
            enabled = privilegeService.enable(id);
        } catch (Exception e) {
            logger.error("Modify privilege occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(enabled);
    }

    /**
     * 查询关联role
     *
     * @param id 主键
     * @return 查询到的数据集，异常时返回204状态码
     */
    @GetMapping("/{id}/roles")
    public ResponseEntity<List<RolePrivileges>> roles(@PathVariable Long id) {
        List<RolePrivileges> voList;
        try {
            voList = rolePrivilegesService.roles(id);
        } catch (Exception e) {
            logger.error("Retrieve privilege roles occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voList);
    }


}
