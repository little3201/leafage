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
import top.leafage.common.poi.reactive.ReactiveExcelReader;
import top.leafage.hypervisor.domain.GroupMembers;
import top.leafage.hypervisor.domain.GroupPrivileges;
import top.leafage.hypervisor.domain.dto.GroupDTO;
import top.leafage.hypervisor.domain.vo.GroupVO;
import top.leafage.hypervisor.service.GroupMembersService;
import top.leafage.hypervisor.service.GroupPrivilegesService;
import top.leafage.hypervisor.service.GroupService;

import java.util.Set;

/**
 * group controller
 *
 * @author wq li
 */
@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupMembersService groupMembersService;
    private final GroupService groupService;
    private final GroupPrivilegesService groupPrivilegesService;

    /**
     * Constructor for GroupController.
     *
     * @param groupMembersService    a {@link GroupMembersService} object
     * @param groupService           a {@link GroupService} object
     * @param groupPrivilegesService a {@link GroupPrivilegesService} object
     */
    public GroupController(GroupMembersService groupMembersService, GroupService groupService,
                           GroupPrivilegesService groupPrivilegesService) {
        this.groupMembersService = groupMembersService;
        this.groupService = groupService;
        this.groupPrivilegesService = groupPrivilegesService;
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 大小
     * @return 查询的数据集
     */
    @GetMapping
    public Mono<Page<GroupVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                        String sortBy, boolean descending, String filters) {
        return groupService.retrieve(page, size, sortBy, descending, filters);
    }

    /**
     * 根据 id 查询
     *
     * @param id the pk. ID
     * @return 查询的数据
     */
    @GetMapping("/{id}")
    public Mono<GroupVO> fetch(@PathVariable Long id) {
        return groupService.fetch(id);
    }

    /**
     * 添加
     *
     * @param dto 要添加的数据
     * @return 添加后的信息
     */
    @PostMapping
    public Mono<GroupVO> create(@RequestBody @Valid GroupDTO dto) {
        return groupService.create(dto);
    }

    /**
     * 修改
     *
     * @param id  the pk.
     * @param dto 要修改的数据
     * @return 修改后的信息，否则返回417状态码
     */
    @PutMapping("/{id}")
    public Mono<GroupVO> modify(@PathVariable Long id, @RequestBody @Valid GroupDTO dto) {
        return groupService.modify(id, dto);
    }

    /**
     * 删除
     *
     * @param id the pk.
     * @return 200状态码
     */
    @DeleteMapping("/{id}")
    public Mono<Void> remove(@PathVariable Long id) {
        return groupService.remove(id);
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
        return groupService.enable(id);
    }

    /**
     * 查询关联user
     *
     * @param id 组id
     * @return 查询到的数据集
     */
    @GetMapping("/{id}/members")
    public Flux<GroupMembers> members(@PathVariable Long id) {
        return groupMembersService.members(id);
    }

    /**
     * 关联权限
     *
     * @param id 组id
     * @return 查询到的数据集
     */
    @PatchMapping("/{id}/privileges/{privilegeId}")
    public Mono<GroupPrivileges> relation(@PathVariable Long id, @PathVariable Long privilegeId,
                                          @RequestBody Set<String> actions) {
        return groupPrivilegesService.relation(id, privilegeId, actions);
    }

    /**
     * 关联权限
     *
     * @param id 组id
     * @return 查询到的数据集
     */
    @DeleteMapping("/{id}/privileges/{privilegeId}")
    public Mono<Void> removeRelation(@PathVariable Long id, @PathVariable Long privilegeId,
                                     Set<String> actions) {
        return groupPrivilegesService.removeRelation(id, privilegeId, actions);
    }

    /**
     * Import the records.
     *
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_groups:import')")
    @PostMapping("/import")
    public Flux<GroupVO> importFromFile(FilePart file) {
        return ReactiveExcelReader.read(file, GroupDTO.class)
                .flatMapMany(groupService::createAll);
    }
}
