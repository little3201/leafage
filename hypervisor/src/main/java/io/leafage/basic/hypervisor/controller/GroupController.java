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

import io.leafage.basic.hypervisor.domain.GroupMembers;
import io.leafage.basic.hypervisor.dto.GroupDTO;
import io.leafage.basic.hypervisor.service.GroupMembersService;
import io.leafage.basic.hypervisor.service.GroupService;
import io.leafage.basic.hypervisor.vo.GroupVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.leafage.common.TreeNode;

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

    private final GroupMembersService groupMembersService;
    private final GroupService groupService;

    /**
     * <p>Constructor for GroupController.</p>
     *
     * @param groupMembersService a {@link io.leafage.basic.hypervisor.service.GroupMembersService} object
     * @param groupService        a {@link io.leafage.basic.hypervisor.service.GroupService} object
     */
    public GroupController(GroupMembersService groupMembersService, GroupService groupService) {
        this.groupMembersService = groupMembersService;
        this.groupService = groupService;
    }

    /**
     * 分页查询
     *
     * @param page       页码
     * @param size       大小
     * @param sortBy     排序字段
     * @param descending 排序方向
     * @param superiorId superior id
     * @return 如果查询到数据，返回查询到的分页后的信息列表，否则返回空
     */
    @GetMapping
    public ResponseEntity<Page<GroupVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                  String sortBy, boolean descending, Long superiorId, String name) {
        Page<GroupVO> voPage;
        try {
            voPage = groupService.retrieve(page, size, sortBy, descending, superiorId, name);
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
    @GetMapping("/tree")
    public ResponseEntity<List<TreeNode>> tree() {
        List<TreeNode> treeNodes;
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
     * 是否存在
     *
     * @param name 名称
     * @param id   主键
     * @return 如果查询到数据，返回查询到的信息，否则返回204状态码
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> exists(@RequestParam String name, Long id) {
        boolean exists;
        try {
            exists = groupService.exists(name, id);
        } catch (Exception e) {
            logger.info("Check group exists error: ", e);
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
    @PostMapping
    public ResponseEntity<GroupVO> create(@RequestBody @Valid GroupDTO dto) {
        GroupVO groupVO;
        try {
            groupVO = groupService.create(dto);
        } catch (Exception e) {
            logger.error("Create group error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(groupVO);
    }

    /**
     * 修改信息
     *
     * @param id  主键
     * @param dto 要修改的数据
     * @return 如果修改数据成功，返回修改后的信息，否则返回304状态码
     */
    @PutMapping("/{id}")
    public ResponseEntity<GroupVO> modify(@PathVariable Long id, @RequestBody GroupDTO dto) {
        GroupVO groupVO;
        try {
            groupVO = groupService.modify(id, dto);
        } catch (Exception e) {
            logger.error("Modify group error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(groupVO);
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
            groupService.remove(id);
        } catch (Exception e) {
            logger.error("Remove group error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 保存group-privilege关联
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
     * 删除 group-privilege关联
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
}
