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

import io.leafage.hypervisor.domain.RoleMembers;
import io.leafage.hypervisor.domain.RolePrivileges;
import io.leafage.hypervisor.dto.RoleDTO;
import io.leafage.hypervisor.service.RoleMembersService;
import io.leafage.hypervisor.service.RolePrivilegesService;
import io.leafage.hypervisor.service.RoleService;
import io.leafage.hypervisor.vo.RoleVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.leafage.common.poi.ExcelReader;

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
     * Retrieves a paginated list of records.
     *
     * @param page       The page number.
     * @param size       The number of records per page.
     * @param sortBy     The field to sort by.
     * @param descending Whether sorting should be in descending order.
     * @param filters    The filters.
     * @return A paginated list of records, or 204 status code if an error occurs.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_roles')")
    @GetMapping
    public ResponseEntity<Page<RoleVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                 String sortBy, boolean descending, String filters) {
        Page<RoleVO> voPage;
        try {
            voPage = roleService.retrieve(page, size, sortBy, descending, filters);
        } catch (Exception e) {
            logger.info("Retrieve role error: ", e);
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
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_roles')")
    @GetMapping("/{id}")
    public ResponseEntity<RoleVO> fetch(@PathVariable Long id) {
        RoleVO vo;
        try {
            vo = roleService.fetch(id);
        } catch (Exception e) {
            logger.info("Fetch role error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(vo);
    }

    /**
     * 添加信息
     *
     * @param dto 要添加的数据
     * @return 如果添加数据成功，返回添加后的信息，否则返回417状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_roles:create')")
    @PostMapping
    public ResponseEntity<RoleVO> create(@Valid @RequestBody RoleDTO dto) {
        RoleVO vo;
        try {
            boolean existed = roleService.exists(dto.getName(), null);
            if (existed) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            vo = roleService.create(dto);
        } catch (Exception e) {
            logger.error("Create role error: ", e);
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
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_roles:modify')")
    @PutMapping("/{id}")
    public ResponseEntity<RoleVO> modify(@PathVariable Long id, @Valid @RequestBody RoleDTO dto) {
        RoleVO vo;
        try {
            boolean existed = roleService.exists(dto.getName(), id);
            if (existed) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            vo = roleService.modify(id, dto);
        } catch (Exception e) {
            logger.error("Modify role error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(vo);
    }

    /**
     * 删除信息
     *
     * @param id 主键
     * @return 如果删除成功，返回200状态码，否则返回417状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_roles:remove')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        try {
            roleService.remove(id);
        } catch (Exception e) {
            logger.error("Remove role error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Enable a record when enabled is false or disable when enabled is ture.
     *
     * @param id The record ID.
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_roles:enable')")
    @PatchMapping("/{id}")
    public ResponseEntity<Boolean> enable(@PathVariable Long id) {
        boolean enabled;
        try {
            enabled = roleService.enable(id);
        } catch (Exception e) {
            logger.error("Toggle enabled error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(enabled);
    }

    /**
     * Import the records.
     *
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_roles:import')")
    @PostMapping("/import")
    public ResponseEntity<List<RoleVO>> importFromFile(MultipartFile file) {
        List<RoleVO> voList;
        try {
            List<RoleDTO> dtoList = ExcelReader.read(file.getInputStream(), RoleDTO.class);
            voList = roleService.createAll(dtoList);
        } catch (Exception e) {
            logger.error("Import role error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().body(voList);
    }

    /**
     * 保存role-privilege关联
     *
     * @param id        role id
     * @param usernames 账号
     * @return 操作结果
     */
    @PatchMapping("/{id}/members")
    public ResponseEntity<List<RoleMembers>> relation(@PathVariable Long id, @RequestBody Set<String> usernames) {
        List<RoleMembers> list;
        try {
            list = roleMembersService.relation(id, usernames);
        } catch (Exception e) {
            logger.error("Relation role members error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.accepted().body(list);
    }

    /**
     * 删除 role-privilege关联
     *
     * @param id        role主键
     * @param usernames username集合
     * @return 操作结果
     */
    @DeleteMapping("/{id}/members")
    public ResponseEntity<Void> removeRelation(@PathVariable Long id, @RequestParam Set<String> usernames) {
        try {
            roleMembersService.removeRelation(id, usernames);
        } catch (Exception e) {
            logger.error("Remove relation role members error: ", e);
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
            logger.error("Retrieve role members error: ", e);
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
            logger.error("Retrieve role privileges error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voList);
    }

    /**
     * 保存role-privilege关联
     *
     * @param id          role id
     * @param privilegeId privilege id
     * @param action      操作
     * @return 操作结果
     */
    @PatchMapping("/{id}/privileges/{privilegeId}")
    public ResponseEntity<RolePrivileges> authorization(@PathVariable Long id, @PathVariable Long privilegeId,
                                                        String action) {
        RolePrivileges rp;
        try {
            rp = rolePrivilegesService.relation(id, privilegeId, action);
        } catch (Exception e) {
            logger.error("Relation role privileges error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.accepted().body(rp);
    }

    /**
     * 删除 role-privilege关联
     *
     * @param id          role id
     * @param privilegeId privilege id
     * @param action      操作
     * @return 操作结果
     */
    @DeleteMapping("/{id}/privileges/{privilegeId}")
    public ResponseEntity<Void> removeAuthorization(@PathVariable Long id, @PathVariable Long privilegeId,
                                                    String action) {
        try {
            rolePrivilegesService.removeRelation(id, privilegeId, action);
        } catch (Exception e) {
            logger.error("Remove relation role privileges error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().build();
    }
}
