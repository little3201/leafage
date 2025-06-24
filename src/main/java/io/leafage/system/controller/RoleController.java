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
package io.leafage.system.controller;

import io.leafage.system.domain.RoleMembers;
import io.leafage.system.domain.RolePrivileges;
import io.leafage.system.dto.RoleDTO;
import io.leafage.system.service.RoleMembersService;
import io.leafage.system.service.RolePrivilegesService;
import io.leafage.system.service.RoleService;
import io.leafage.system.vo.RoleVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * role controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/roles")
public class RoleController {

    private final Logger logger = LoggerFactory.getLogger(RoleController.class);

    private final RoleMembersService roleMembersService;
    private final RoleService roleService;
    private final RolePrivilegesService rolePrivilegesService;

    /**
     * <p>Constructor for RoleController.</p>
     *
     * @param roleMembersService    a {@link RoleMembersService} object
     * @param roleService           a {@link RoleService} object
     * @param rolePrivilegesService a {@link RolePrivilegesService} object
     */
    public RoleController(RoleMembersService roleMembersService, RoleService roleService, RolePrivilegesService rolePrivilegesService) {
        this.roleMembersService = roleMembersService;
        this.roleService = roleService;
        this.rolePrivilegesService = rolePrivilegesService;
    }

    /**
     * 分页查询
     *
     * @param page       页码
     * @param size       大小
     * @param sortBy     排序字段
     * @param descending 排序方向
     * @return 如果查询到数据，返回查询到的分页后的信息列表，否则返回空
     */
    @PreAuthorize("hasAuthority('SCOPE_roles:read')")
    @GetMapping
    public ResponseEntity<Page<RoleVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                 String sortBy, boolean descending, String name) {
        Page<RoleVO> voPage;
        try {
            voPage = roleService.retrieve(page, size, sortBy, descending, name);
        } catch (Exception e) {
            logger.info("Retrieve role occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voPage);
    }

    /**
     * 查询信息
     *
     * @param id 主键
     * @return 如果查询到数据，返回查询到的信息，否则返回204状态码
     */
    @PreAuthorize("hasAuthority('SCOPE_roles:read')")
    @GetMapping("/{id}")
    public ResponseEntity<RoleVO> fetch(@PathVariable Long id) {
        RoleVO vo;
        try {
            vo = roleService.fetch(id);
        } catch (Exception e) {
            logger.info("Fetch role occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(vo);
    }

    /**
     * 是否存在
     *
     * @param name 名称
     * @return 如果查询到数据，返回查询到的信息，否则返回204状态码
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> exists(@RequestParam String name, Long id) {
        boolean exists;
        try {
            exists = roleService.exists(name, id);
        } catch (Exception e) {
            logger.info("Query dictionary exists occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(exists);
    }

    /**
     * 添加信息
     *
     * @param dto 要添加的数据
     * @return 如果添加数据成功，返回添加后的信息，否则返回417状态码
     */
    @PreAuthorize("hasAuthority('SCOPE_roles:write')")
    @PostMapping
    public ResponseEntity<RoleVO> create(@RequestBody @Valid RoleDTO dto) {
        RoleVO vo;
        try {
            boolean existed = roleService.exists(dto.getName(), null);
            if (existed) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            vo = roleService.create(dto);
        } catch (Exception e) {
            logger.error("Create role occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    /**
     * 修改信息
     *
     * @param id  主键
     * @param dto 要修改的数据
     * @return 如果修改数据成功，返回修改后的信息，否则返回304状态码
     */
    @PreAuthorize("hasAuthority('SCOPE_roles:write')")
    @PutMapping("/{id}")
    public ResponseEntity<RoleVO> modify(@PathVariable Long id, @RequestBody @Valid RoleDTO dto) {
        RoleVO vo;
        try {
            boolean existed = roleService.exists(dto.getName(), id);
            if (existed) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            vo = roleService.modify(id, dto);
        } catch (Exception e) {
            logger.error("Modify role occurred an error: ", e);
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
        return ResponseEntity.accepted().body(vo);
    }

    /**
     * 启用、停用
     *
     * @param id 主键
     * @return 编辑后的信息，否则返回417状态码
     */
    @PreAuthorize("hasAuthority('SCOPE_roles:write')")
    @PatchMapping("/{id}")
    public ResponseEntity<Boolean> enable(@PathVariable Long id) {
        boolean enabled;
        try {
            enabled = roleService.enable(id);
        } catch (Exception e) {
            logger.error("Modify user occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(enabled);
    }

    /**
     * 删除信息
     *
     * @param id 主键
     * @return 如果删除成功，返回200状态码，否则返回417状态码
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        try {
            roleService.remove(id);
        } catch (Exception e) {
            logger.error("Remove role occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 根据id查询关联用户信息
     *
     * @param id roleid
     * @return 查询到的数据集，异常时返回204状态码
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<List<RoleMembers>> members(@PathVariable Long id) {
        List<RoleMembers> voList;
        try {
            voList = roleMembersService.members(id);
        } catch (Exception e) {
            logger.error("Retrieve role members occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voList);
    }

    /**
     * 查询role-privilege关联
     *
     * @param id role代码
     * @return 操作结果
     */
    @GetMapping("/{id}/privileges")
    public ResponseEntity<List<RolePrivileges>> privileges(@PathVariable Long id) {
        List<RolePrivileges> voList;
        try {
            voList = rolePrivilegesService.privileges(id);
        } catch (Exception e) {
            logger.error("Relation role privileges occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voList);
    }

    /**
     * 保存role-privilege关联
     *
     * @param id         role主键
     * @param privileges privilege信息
     * @return 操作结果
     */
    @PatchMapping("/{id}/privileges")
    public ResponseEntity<List<RolePrivileges>> relation(@PathVariable Long id, @RequestBody Set<Long> privileges) {
        List<RolePrivileges> voList;
        try {
            voList = rolePrivilegesService.relation(id, privileges);
        } catch (Exception e) {
            logger.error("Relation role privileges occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.accepted().body(voList);
    }

}
