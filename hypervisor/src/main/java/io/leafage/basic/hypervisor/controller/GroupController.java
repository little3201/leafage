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

package io.leafage.basic.hypervisor.controller;

import io.leafage.basic.hypervisor.domain.GroupMembers;
import io.leafage.basic.hypervisor.domain.GroupPrivileges;
import io.leafage.basic.hypervisor.dto.GroupDTO;
import io.leafage.basic.hypervisor.service.GroupMembersService;
import io.leafage.basic.hypervisor.service.GroupPrivilegesService;
import io.leafage.basic.hypervisor.service.GroupService;
import io.leafage.basic.hypervisor.vo.GroupVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

/**
 * group controller
 *
 * @author wq li
 */
@Validated
@RestController
@RequestMapping("/groups")
public class GroupController {

    private final Logger logger = LoggerFactory.getLogger(GroupController.class);

    private final GroupMembersService groupMembersService;
    private final GroupService groupService;
    private final GroupPrivilegesService groupPrivilegesService;

    /**
     * <p>Constructor for GroupController.</p>
     *
     * @param groupMembersService    a {@link GroupMembersService} object
     * @param groupService           a {@link GroupService} object
     * @param groupPrivilegesService a {@link GroupPrivilegesService} object
     */
    public GroupController(GroupMembersService groupMembersService, GroupService groupService, GroupPrivilegesService groupPrivilegesService) {
        this.groupMembersService = groupMembersService;
        this.groupService = groupService;
        this.groupPrivilegesService = groupPrivilegesService;
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 大小
     * @return 查询的数据集，异常时返回204状态码
     */
    @GetMapping
    public ResponseEntity<Mono<Page<GroupVO>>> retrieve(@RequestParam int page, @RequestParam int size,
                                                        String sortBy, boolean descending) {
        Mono<Page<GroupVO>> pageMono;
        try {
            pageMono = groupService.retrieve(page, size, sortBy, descending);
        } catch (Exception e) {
            logger.error("Retrieve groups occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pageMono);
    }

    /**
     * 根据 id 查询
     *
     * @param id 业务id
     * @return 查询的数据，异常时返回204状态码
     */
    @GetMapping("/{id}")
    public ResponseEntity<Mono<GroupVO>> fetch(@PathVariable Long id) {
        Mono<GroupVO> voMono;
        try {
            voMono = groupService.fetch(id);
        } catch (Exception e) {
            logger.error("Fetch group occurred an error: ", e);
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
            existsMono = groupService.exists(name, id);
        } catch (Exception e) {
            logger.error("Check group is exists occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(existsMono);
    }

    /**
     * 添加
     *
     * @param groupDTO 要添加的数据
     * @return 添加后的信息，异常时返回417状态码
     */
    @PostMapping
    public ResponseEntity<Mono<GroupVO>> create(@RequestBody @Valid GroupDTO groupDTO) {
        Mono<GroupVO> voMono;
        try {
            voMono = groupService.create(groupDTO);
        } catch (Exception e) {
            logger.error("Create group occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(voMono);
    }

    /**
     * 修改
     *
     * @param id       主键
     * @param groupDTO 要修改的数据
     * @return 修改后的信息，否则返回304状态码
     */
    @PutMapping("/{id}")
    public ResponseEntity<Mono<GroupVO>> modify(@PathVariable Long id, @RequestBody @Valid GroupDTO groupDTO) {
        Mono<GroupVO> voMono;
        try {
            voMono = groupService.modify(id, groupDTO);
        } catch (Exception e) {
            logger.error("Modify group occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(voMono);
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return 200状态码，异常时返回417状态码
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<Void>> remove(@PathVariable Long id) {
        Mono<Void> voidMono;
        try {
            voidMono = groupService.remove(id);
        } catch (Exception e) {
            logger.error("Remove group occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok(voidMono);
    }

    /**
     * 查询关联user
     *
     * @param id 组id
     * @return 查询到的数据集，异常时返回204状态码
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<Mono<List<GroupMembers>>> members(@PathVariable Long id) {
        Mono<List<GroupMembers>> listMono;
        try {
            listMono = groupMembersService.members(id);
        } catch (Exception e) {
            logger.error("Retrieve group members occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(listMono);
    }

    /**
     * 关联权限
     *
     * @param id 组id
     * @return 查询到的数据集，异常时返回204状态码
     */
    @PatchMapping("/{id}/privileges/{privilegeId}")
    public ResponseEntity<Mono<GroupPrivileges>> relation(@PathVariable Long id, @PathVariable Long privilegeId, @RequestBody Set<String> actions) {
        Mono<GroupPrivileges> mono;
        try {
            mono = groupPrivilegesService.relation(id, privilegeId, actions);
        } catch (Exception e) {
            logger.error("Relation group privileges occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(mono);
    }

    /**
     * 关联权限
     *
     * @param id 组id
     * @return 查询到的数据集，异常时返回204状态码
     */
    @DeleteMapping("/{id}/privileges/{privilegeId}")
    public ResponseEntity<Mono<Void>> removeRelation(@PathVariable Long id, @PathVariable Long privilegeId, Set<String> actions) {
        Mono<Void> voidMono;
        try {
            voidMono = groupPrivilegesService.removeRelation(id, privilegeId, actions);
        } catch (Exception e) {
            logger.error("Remove group privileges occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voidMono);
    }
}
