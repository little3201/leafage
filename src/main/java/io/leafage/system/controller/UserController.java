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

import io.leafage.system.dto.UserDTO;
import io.leafage.system.service.UserService;
import io.leafage.system.vo.UserVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * user controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    /**
     * <p>Constructor for UserController.</p>
     *
     * @param userService a {@link UserService} object
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 分页查询
     *
     * @param page       页码
     * @param size       大小
     * @param sortBy     排序字段
     * @param descending 排序方向
     * @param username   username
     * @return 如果查询到数据，返回查询到的分页后的信息列表，否则返回空
     */
    @PreAuthorize("hasAuthority('SCOPE_users:read')")
    @GetMapping
    public ResponseEntity<Page<UserVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                 String sortBy, boolean descending, String username) {
        Page<UserVO> voPage;
        try {
            voPage = userService.retrieve(page, size, sortBy, descending, username);
        } catch (Exception e) {
            logger.info("Retrieve user occurred an error: ", e);
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
    @PreAuthorize("hasAuthority('SCOPE_users:read')")
    @GetMapping("/{id}")
    public ResponseEntity<UserVO> fetch(@PathVariable Long id) {
        UserVO vo;
        try {
            vo = userService.fetch(id);
        } catch (Exception e) {
            logger.info("Fetch user occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(vo);
    }

    /**
     * 查询信息
     *
     * @return 如果查询到数据，返回查询到的信息，否则返回204状态码
     */
    @GetMapping("/me")
    public ResponseEntity<UserVO> me(Principal principal) {
        UserVO vo;
        try {
            vo = userService.findByUsername(principal.getName());
        } catch (Exception e) {
            logger.info("Fetch user occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(vo);
    }

    /**
     * 是否存在
     *
     * @param username username
     * @return 如果查询到数据，返回查询到的信息，否则返回204状态码
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> exists(@RequestParam String username, Long id) {
        boolean exists;
        try {
            exists = userService.exists(username, id);
        } catch (Exception e) {
            logger.info("Query dictionary exists occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(exists);
    }

    /**
     * 添加信息.
     *
     * @param dto 要修改的数据
     * @return 如果添加数据成功，返回添加后的信息，否则返回417状态码
     */
    @PreAuthorize("hasAuthority('SCOPE_users:write')")
    @PostMapping
    public ResponseEntity<UserVO> create(@RequestBody @Valid UserDTO dto) {
        UserVO vo;
        try {
            boolean existed = userService.exists(dto.getUsername(), null);
            if (existed) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            vo = userService.create(dto);
        } catch (Exception e) {
            logger.error("Create user occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    /**
     * 修改信息.
     *
     * @param id  主键
     * @param dto 要修改的数据
     * @return 如果修改数据成功，返回修改后的信息，否则返回304状态码
     */
    @PreAuthorize("hasAuthority('SCOPE_users:write')")
    @PutMapping("/{id}")
    public ResponseEntity<UserVO> modify(@PathVariable Long id,
                                         @RequestBody @Valid UserDTO dto) {
        UserVO vo;
        try {
            boolean existed = userService.exists(dto.getUsername(), id);
            if (existed) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            vo = userService.modify(id, dto);
        } catch (Exception e) {
            logger.error("Modify user occurred an error: ", e);
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
    @PreAuthorize("hasAuthority('SCOPE_users:write')")
    @PatchMapping("/{id}")
    public ResponseEntity<Boolean> enable(@PathVariable Long id) {
        boolean enabled;
        try {
            enabled = userService.enable(id);
        } catch (Exception e) {
            logger.error("Modify user occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(enabled);
    }

}
