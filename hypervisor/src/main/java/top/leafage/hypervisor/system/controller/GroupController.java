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
package top.leafage.hypervisor.system.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.leafage.common.data.domain.TreeNode;
import top.leafage.common.poi.ExcelReader;
import top.leafage.hypervisor.system.domain.GroupMembers;
import top.leafage.hypervisor.system.domain.GroupPrivileges;
import top.leafage.hypervisor.system.domain.GroupRoles;
import top.leafage.hypervisor.system.domain.dto.GroupDTO;
import top.leafage.hypervisor.system.domain.vo.GroupVO;
import top.leafage.hypervisor.system.service.GroupMembersService;
import top.leafage.hypervisor.system.service.GroupPrivilegesService;
import top.leafage.hypervisor.system.service.GroupRolesService;
import top.leafage.hypervisor.system.service.GroupService;

import java.io.IOException;
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
        Page<GroupVO> voPage = groupService.retrieve(page, size, sortBy, descending, filters);
        return ResponseEntity.ok(voPage);
    }

    /**
     * 查询树形数据
     *
     * @return th result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_groups')")
    @GetMapping("/tree")
    public ResponseEntity<List<TreeNode<Long>>> tree() {
        List<TreeNode<Long>> treeNodes = groupService.tree();
        return ResponseEntity.ok(treeNodes);
    }

    /**
     * fetch by id.
     *
     * @param id the pk.
     * @return 如果查询到数据，返回查询到的信息，否则返回204状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_groups')")
    @GetMapping("/{id}")
    public ResponseEntity<GroupVO> fetch(@PathVariable Long id) {
        GroupVO vo = groupService.fetch(id);
        return ResponseEntity.ok(vo);
    }

    /**
     * create.
     *
     * @param dto the request body.
     * @return the result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_groups:create')")
    @PostMapping
    public ResponseEntity<GroupVO> create(@Valid @RequestBody GroupDTO dto) {
        GroupVO vo = groupService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    /**
     * modify.
     *
     * @param id  the pk.
     * @param dto the request body.
     * @return the result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_groups:modify')")
    @PutMapping("/{id}")
    public ResponseEntity<GroupVO> modify(@PathVariable Long id, @RequestBody GroupDTO dto) {
        GroupVO vo = groupService.modify(id, dto);
        return ResponseEntity.accepted().body(vo);
    }

    /**
     * remove.
     *
     * @param id the pk.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_groups:remove')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        groupService.remove(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * enable.
     *
     * @param id the pk..
     * @return the result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_groups:enable')")
    @PatchMapping("/{id}")
    public ResponseEntity<Boolean> enable(@PathVariable Long id) {
        boolean enabled = groupService.enable(id);
        return ResponseEntity.ok(enabled);
    }

    /**
     * import..
     *
     * @return the result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_groups:import')")
    @PostMapping("/import")
    public ResponseEntity<List<GroupVO>> importFromFile(MultipartFile file) throws IOException {
        List<GroupDTO> dtoList = ExcelReader.read(file.getInputStream(), GroupDTO.class);
        List<GroupVO> voList = groupService.createAll(dtoList);

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
    public ResponseEntity<List<GroupMembers>> relationMembers(@PathVariable Long id, @RequestBody Set<String> usernames) {
        List<GroupMembers> groupMembers = groupMembersService.relation(id, usernames);
        return ResponseEntity.ok(groupMembers);
    }

    /**
     * 删除 group-users关联
     *
     * @param id        the pk of group.
     * @param usernames username集合
     * @return 操作结果
     */
    @DeleteMapping("/{id}/members")
    public ResponseEntity<Void> removeMembers(@PathVariable Long id, @RequestParam Set<String> usernames) {
        groupMembersService.removeRelation(id, usernames);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据group查询关联user
     *
     * @param id group id
     * @return 查询到的数据集，异常时返回204状态码
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<List<GroupMembers>> members(@PathVariable Long id) {
        List<GroupMembers> members = groupMembersService.members(id);
        return ResponseEntity.ok(members);
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
        List<GroupRoles> groupRoles = groupRolesService.relation(id, roleIds);
        return ResponseEntity.ok(groupRoles);
    }

    /**
     * 根据group查询关联roles
     *
     * @param id group id
     * @return 查询到的数据集，异常时返回204状态码
     */
    @GetMapping("/{id}/roles")
    public ResponseEntity<List<GroupRoles>> roles(@PathVariable Long id) {
        List<GroupRoles> roles = groupRolesService.roles(id);
        return ResponseEntity.ok(roles);
    }

    /**
     * 删除 group-roles关联
     *
     * @param id      group id
     * @param roleIds role ids
     * @return 操作结果
     */
    @DeleteMapping("/{id}/roles")
    public ResponseEntity<Void> removeRoles(@PathVariable Long id, @RequestParam Set<Long> roleIds) {
        groupRolesService.removeRelation(id, roleIds);
        return ResponseEntity.noContent().build();
    }

    /**
     * 查询role-privilege关联
     *
     * @param id role代码
     * @return 操作结果
     */
    @GetMapping("/{id}/privileges")
    public ResponseEntity<List<GroupPrivileges>> privileges(@PathVariable Long id) {
        List<GroupPrivileges> privileges = groupPrivilegesService.privileges(id);
        return ResponseEntity.ok(privileges);
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
    public ResponseEntity<GroupPrivileges> relationPrivileges(@PathVariable Long id, @PathVariable Long privilegeId,
                                                              String action) {
        GroupPrivileges groupPrivileges = groupPrivilegesService.relation(id, privilegeId, action);
        return ResponseEntity.ok(groupPrivileges);
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
    public ResponseEntity<Void> removePrivileges(@PathVariable Long id, @PathVariable Long privilegeId,
                                                 String action) {
        groupPrivilegesService.removeRelation(id, privilegeId, action);

        return ResponseEntity.noContent().build();
    }
}
