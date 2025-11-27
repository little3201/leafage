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
package top.leafage.hypervisor.controller;

import top.leafage.hypervisor.domain.GroupMembers;
import top.leafage.hypervisor.domain.GroupPrivileges;
import top.leafage.hypervisor.domain.GroupRoles;
import top.leafage.hypervisor.domain.dto.GroupDTO;
import top.leafage.hypervisor.domain.vo.GroupVO;
import top.leafage.hypervisor.service.GroupMembersService;
import top.leafage.hypervisor.service.GroupPrivilegesService;
import top.leafage.hypervisor.service.GroupRolesService;
import top.leafage.hypervisor.service.GroupService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.leafage.common.data.domain.TreeNode;
import top.leafage.common.poi.ExcelReader;

import java.util.List;
import java.util.Set;

/**
 * group controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/groups")
public class GroupController {

    private final Logger logger = LoggerFactory.getLogger(GroupController.class);

    private final GroupService groupService;
    private final GroupMembersService groupMembersService;
    private final GroupRolesService groupRolesService;
    private final GroupPrivilegesService groupPrivilegesService;

    /**
     * Constructor for GroupController.
     *
     * @param groupMembersService a {@link GroupMembersService} object
     * @param groupService        a {@link GroupService} object
     */
    public GroupController(GroupService groupService, GroupMembersService groupMembersService,
                           GroupRolesService groupRolesService, GroupPrivilegesService groupPrivilegesService) {
        this.groupService = groupService;
        this.groupMembersService = groupMembersService;
        this.groupRolesService = groupRolesService;
        this.groupPrivilegesService = groupPrivilegesService;
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
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_groups')")
    @GetMapping
    public ResponseEntity<Page<GroupVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                  String sortBy, boolean descending, String filters) {
        Page<GroupVO> voPage;
        try {
            voPage = groupService.retrieve(page, size, sortBy, descending, filters);
        } catch (Exception e) {
            logger.info("Retrieve group error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voPage);
    }

    /**
     * 查询树形数据
     *
     * @return 查询到的数据，否则返回空
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_groups')")
    @GetMapping("/tree")
    public ResponseEntity<List<TreeNode<Long>>> tree() {
        List<TreeNode<Long>> treeNodes;
        try {
            treeNodes = groupService.tree();
        } catch (Exception e) {
            logger.info("Retrieve privilege tree error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(treeNodes);
    }

    /**
     * 查询信息
     *
     * @param id 主键
     * @return 如果查询到数据，返回查询到的信息，否则返回204状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_groups')")
    @GetMapping("/{id}")
    public ResponseEntity<GroupVO> fetch(@PathVariable Long id) {
        GroupVO groupVO;
        try {
            groupVO = groupService.fetch(id);
        } catch (Exception e) {
            logger.info("Fetch group error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(groupVO);
    }

    /**
     * 添加信息
     *
     * @param dto 要添加的数据
     * @return 如果添加数据成功，返回添加后的信息，否则返回417状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_groups:create')")
    @PostMapping
    public ResponseEntity<GroupVO> create(@Valid @RequestBody GroupDTO dto) {
        GroupVO vo;
        try {
            boolean existed = groupService.exists(dto.getName(), null);
            if (existed) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            vo = groupService.create(dto);
        } catch (Exception e) {
            logger.error("Create group error: ", e);
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
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_groups:modify')")
    @PutMapping("/{id}")
    public ResponseEntity<GroupVO> modify(@PathVariable Long id, @RequestBody GroupDTO dto) {
        GroupVO vo;
        try {
            boolean existed = groupService.exists(dto.getName(), id);
            if (existed) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            vo = groupService.modify(id, dto);
        } catch (Exception e) {
            logger.error("Modify group error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(vo);
    }

    /**
     * 删除信息
     *
     * @param id 主键
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_groups:remove')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        try {
            groupService.remove(id);
        } catch (Exception e) {
            logger.error("Remove group error: ", e);
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
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_groups:enable')")
    @PatchMapping("/{id}")
    public ResponseEntity<Boolean> enable(@PathVariable Long id) {
        boolean enabled;
        try {
            enabled = groupService.enable(id);
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
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_groups:import')")
    @PostMapping("/import")
    public ResponseEntity<List<GroupVO>> importFromFile(MultipartFile file) {
        List<GroupVO> voList;
        try {
            List<GroupDTO> dtoList = ExcelReader.read(file.getInputStream(), GroupDTO.class);
            voList = groupService.createAll(dtoList);
        } catch (Exception e) {
            logger.error("Import groups error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().body(voList);
    }

    /**
     * 保存group-users关联
     *
     * @param id        group id
     * @param usernames 账号
     * @return 操作结果
     */
    @PatchMapping("/{id}/members")
    public ResponseEntity<List<GroupMembers>> relation(@PathVariable Long id, @RequestBody Set<String> usernames) {
        List<GroupMembers> list;
        try {
            list = groupMembersService.relation(id, usernames);
        } catch (Exception e) {
            logger.error("Relation group members error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.accepted().body(list);
    }

    /**
     * 删除 group-users关联
     *
     * @param id        group主键
     * @param usernames username集合
     * @return 操作结果
     */
    @DeleteMapping("/{id}/members")
    public ResponseEntity<Void> removeRelation(@PathVariable Long id, @RequestParam Set<String> usernames) {
        try {
            groupMembersService.removeRelation(id, usernames);
        } catch (Exception e) {
            logger.error("Remove relation group members error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 根据group查询关联user
     *
     * @param id group id
     * @return 查询到的数据集，异常时返回204状态码
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<List<GroupMembers>> members(@PathVariable Long id) {
        List<GroupMembers> voList;
        try {
            voList = groupMembersService.members(id);
        } catch (Exception e) {
            logger.error("Retrieve group users error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voList);
    }

    /**
     * 保存group-roles关联
     *
     * @param id      group id
     * @param roleIds role ids
     * @return 操作结果
     */
    @PatchMapping("/{id}/roles")
    public ResponseEntity<List<GroupRoles>> relationRoles(@PathVariable Long id, @RequestBody Set<Long> roleIds) {
        List<GroupRoles> list;
        try {
            list = groupRolesService.relation(id, roleIds);
        } catch (Exception e) {
            logger.error("Relation group roles error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.accepted().body(list);
    }

    /**
     * 删除 group-roles关联
     *
     * @param id      group id
     * @param roleIds role ids
     * @return 操作结果
     */
    @DeleteMapping("/{id}/roles")
    public ResponseEntity<Void> removeRelationRoles(@PathVariable Long id, @RequestParam Set<Long> roleIds) {
        try {
            groupRolesService.removeRelation(id, roleIds);
        } catch (Exception e) {
            logger.error("Remove relation group roles error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 根据group查询关联roles
     *
     * @param id group id
     * @return 查询到的数据集，异常时返回204状态码
     */
    @GetMapping("/{id}/roles")
    public ResponseEntity<List<GroupMembers>> roles(@PathVariable Long id) {
        List<GroupMembers> voList;
        try {
            voList = groupMembersService.members(id);
        } catch (Exception e) {
            logger.error("Retrieve group users error: ", e);
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
    public ResponseEntity<List<GroupPrivileges>> privileges(@PathVariable Long id) {
        List<GroupPrivileges> voList;
        try {
            voList = groupPrivilegesService.privileges(id);
        } catch (Exception e) {
            logger.error("Retrieve role privileges error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voList);
    }

    /**
     * 保存 group-privilege关联
     *
     * @param id          role id
     * @param privilegeId privilege id
     * @param action      操作
     * @return 操作结果
     */
    @PatchMapping("/{id}/privileges/{privilegeId}")
    public ResponseEntity<GroupPrivileges> authorization(@PathVariable Long id, @PathVariable Long privilegeId,
                                                         String action) {
        GroupPrivileges gp;
        try {
            gp = groupPrivilegesService.relation(id, privilegeId, action);
        } catch (Exception e) {
            logger.error("Relation group privileges error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.accepted().body(gp);
    }

    /**
     * 删除 group-privilege关联
     *
     * @param id          group id
     * @param privilegeId privilege id
     * @param action      操作
     * @return 操作结果
     */
    @DeleteMapping("/{id}/privileges/{privilegeId}")
    public ResponseEntity<Void> removeAuthorization(@PathVariable Long id, @PathVariable Long privilegeId,
                                                    String action) {
        try {
            groupPrivilegesService.removeRelation(id, privilegeId, action);
        } catch (Exception e) {
            logger.error("Remove relation group privileges error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().build();
    }
}
