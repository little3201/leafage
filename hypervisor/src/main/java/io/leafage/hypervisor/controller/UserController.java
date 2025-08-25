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
    public Mono<ResponseEntity<Page<UserVO>>> retrieve(@RequestParam int page, @RequestParam int size,
                                                       String sortBy, boolean descending, String filters) {
        return userService.retrieve(page, size, sortBy, descending, filters)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.error("Retrieve users records error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    /**
     * 根据 id 查询
     *
     * @param id user 主键
     * @return 查询的数据，异常时返回204状态码
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserVO>> fetch(@PathVariable Long id) {
        return userService.fetch(id)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.error("Fetch user error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    /**
     * 是否已存在
     *
     * @param username 用户名
     * @return true-是，false-否
     */
    @GetMapping("/exists")
    public Mono<ResponseEntity<Boolean>> exists(@RequestParam String username, Long id) {
        return userService.exists(username, id)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.error("Check user is exists user error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    /**
     * 查询当前用户
     *
     * @param principal 当前用户
     * @return 查询的数据，异常时返回204状态码
     */
    @GetMapping("/me")
    public Mono<ResponseEntity<UserVO>> fetchMe(Principal principal) {
        return userService.findByUsername(principal.getName())
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.error("Fetch me error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    /**
     * 添加信息
     *
     * @param userDTO 要修改的数据
     * @return 修改后的信息，异常时返回304状态码
     */
    @PostMapping
    public Mono<ResponseEntity<UserVO>> create(@RequestBody @Valid UserDTO userDTO) {
        return userService.create(userDTO)
                .map(vo -> ResponseEntity.status(HttpStatus.CREATED).body(vo))
                .onErrorResume(e -> {
                    logger.error("Create user occurred an error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build());
                });
    }

    /**
     * 修改信息
     *
     * @param id      user 主键
     * @param userDTO 要修改的数据
     * @return 修改后的信息，异常时返回304状态码
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserVO>> modify(@PathVariable Long id, @RequestBody @Valid UserDTO userDTO) {
        return userService.modify(id, userDTO)
                .map(vo -> ResponseEntity.accepted().body(vo))
                .onErrorResume(e -> {
                    logger.error("Modify user occurred an error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build());
                });
    }

    /**
     * 删除信息
     *
     * @param id user 主键
     * @return 200状态码，异常时返回417状态码
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> remove(@PathVariable Long id) {
        return userService.remove(id).then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorResume(e -> {
                    logger.error("Remove user error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build());
                });
    }

}
