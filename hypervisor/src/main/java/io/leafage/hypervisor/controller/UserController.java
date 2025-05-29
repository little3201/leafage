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

package io.leafage.hypervisor.controller;

import io.leafage.hypervisor.dto.UserDTO;
import io.leafage.hypervisor.service.UserService;
import io.leafage.hypervisor.vo.UserVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * user controller
 *
 * @author wq li
 */
@Validated
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
     * <p>retrieve.</p>
     *
     * @param page a int
     * @param size a int
     * @return a {@link org.springframework.http.ResponseEntity} object
     */
    @GetMapping
    public ResponseEntity<Mono<Page<UserVO>>> retrieve(@RequestParam int page, @RequestParam int size,
                                                       String sortBy, boolean descending, String filters) {
        Mono<Page<UserVO>> pageMono;
        try {
            pageMono = userService.retrieve(page, size, sortBy, descending, filters);
        } catch (Exception e) {
            logger.error("Retrieve users occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pageMono);
    }

    /**
     * 根据 id 查询
     *
     * @param id user 主键
     * @return 查询的数据，异常时返回204状态码
     */
    @GetMapping("/{id}")
    public ResponseEntity<Mono<UserVO>> fetch(@PathVariable Long id) {
        Mono<UserVO> voMono;
        try {
            voMono = userService.fetch(id);
        } catch (Exception e) {
            logger.error("Fetch user occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voMono);
    }

    /**
     * 是否已存在
     *
     * @param username 用户名
     * @return true-是，false-否
     */
    @GetMapping("/exists")
    public ResponseEntity<Mono<Boolean>> exists(@RequestParam String username, Long id) {
        Mono<Boolean> existsMono;
        try {
            existsMono = userService.exists(username, id);
        } catch (Exception e) {
            logger.error("Check user is exists occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(existsMono);
    }

    /**
     * 查询当前用户
     *
     * @param principal 当前用户
     * @return 查询的数据，异常时返回204状态码
     */
    @GetMapping("/me")
    public ResponseEntity<Mono<UserVO>> fetchMe(Principal principal) {
        Mono<UserVO> voMono;
        try {
            voMono = userService.findByUsername(principal.getName());
        } catch (Exception e) {
            logger.error("Fetch me occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voMono);
    }

    /**
     * 添加信息
     *
     * @param userDTO 要修改的数据
     * @return 修改后的信息，异常时返回304状态码
     */
    @PostMapping
    public ResponseEntity<Mono<UserVO>> create(@RequestBody @Valid UserDTO userDTO) {
        Mono<UserVO> voMono;
        try {
            voMono = userService.create(userDTO);
        } catch (Exception e) {
            logger.error("Create user occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(voMono);
    }

    /**
     * 修改信息
     *
     * @param id      user 主键
     * @param userDTO 要修改的数据
     * @return 修改后的信息，异常时返回304状态码
     */
    @PutMapping("/{id}")
    public ResponseEntity<Mono<UserVO>> modify(@PathVariable Long id, @RequestBody @Valid UserDTO userDTO) {
        Mono<UserVO> voMono;
        try {
            voMono = userService.modify(id, userDTO);
        } catch (Exception e) {
            logger.error("Modify user occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(voMono);
    }

    /**
     * 删除信息
     *
     * @param id user 主键
     * @return 200状态码，异常时返回417状态码
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<Void>> remove(@PathVariable Long id) {
        Mono<Void> voidMono;
        try {
            voidMono = userService.remove(id);
        } catch (Exception e) {
            logger.error("Remove user occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok(voidMono);
    }

}
