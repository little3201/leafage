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
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.poi.ExcelReader;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.nio.file.FileAlreadyExistsException;
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
    public Mono<Page<UserVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                       String sortBy, boolean descending, String filters) {
        return userService.retrieve(page, size, sortBy, descending, filters)
                .doOnError(e -> logger.error("Retrieve users error: ", e));
    }

    /**
     * 根据 id 查询
     *
     * @param id user 主键
     * @return 查询的数据
     */
    @GetMapping("/{id}")
    public Mono<UserVO> fetch(@PathVariable Long id) {
        return userService.fetch(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
                .doOnError(e -> {
                    if (!(e instanceof ResponseStatusException)) {
                        logger.error("Fetch user error, id: {}", id, e);
                    }
                });
    }

    /**
     * 是否已存在
     *
     * @param username 用户名
     * @return true-是，false-否
     */
    @GetMapping("/exists")
    public Mono<Boolean> exists(@RequestParam String username, Long id) {
        return userService.exists(username, id)
                .doOnError(e -> logger.error("Check user exists error, username: {}", username, e));
    }

    /**
     * 查询当前用户
     *
     * @param principal 当前用户
     * @return 查询的数据
     */
    @GetMapping("/me")
    public Mono<UserVO> fetchMe(Principal principal) {
        return userService.findByUsername(principal.getName())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found")))
                .doOnError(e -> {
                    if (!(e instanceof ResponseStatusException)) {
                        logger.error("Fetch me error: ", e);
                    }
                });
    }

    /**
     * 添加信息
     *
     * @param dto 要添加的数据
     * @return 修改后的信息
     */
    @PostMapping
    public Mono<UserVO> create(@RequestBody @Valid UserDTO dto) {
        return userService.exists(dto.getUsername(), null).flatMap(exists -> {
            if (exists) {
                return Mono.error(new KeyAlreadyExistsException("Already exists: " + dto.getUsername()));
            } else {
                return userService.create(dto);
            }
        }).doOnError(e -> logger.error("Create user error: ", e));
    }

    /**
     * 修改信息
     *
     * @param id  user 主键
     * @param dto 要修改的数据
     * @return 修改后的信息
     */
    @PutMapping("/{id}")
    public Mono<UserVO> modify(@PathVariable Long id, @RequestBody @Valid UserDTO dto) {
        return userService.exists(dto.getUsername(), id).flatMap(exists -> {
            if (exists) {
                return Mono.error(new KeyAlreadyExistsException("Already exists: " + dto.getUsername()));
            } else {
                return userService.modify(id, dto);
            }
        }).doOnError(e -> logger.error("Modify user error: ", e));
    }

    /**
     * 删除信息
     *
     * @param id user 主键
     * @return 200状态码
     */
    @DeleteMapping("/{id}")
    public Mono<Void> remove(@PathVariable Long id) {
        return userService.remove(id)
                .doOnError(e -> logger.error("Remove user error, id: {}", id, e));
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
        return userService.enable(id)
                .doOnSuccess(result -> logger.debug("User enabled state toggled, id: {}, result: {}", id, result))
                .doOnError(e -> logger.error("Toggle enabled error, id: {}", id, e));
    }

    /**
     * Unlock a record when account is lock.
     *
     * @param id The record ID.
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_users:unlock')")
    @PatchMapping("/{id}/unlock")
    public Mono<Boolean> unlock(@PathVariable Long id) {
        return userService.unlock(id)
                .doOnSuccess(result -> logger.debug("User unlocked, id: {}, result: {}", id, result))
                .doOnError(e -> logger.error("Unlock user error, id: {}", id, e));
    }

    /**
     * Import the records.
     *
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_users:import')")
    @PostMapping("/import")
    public Flux<UserVO> importFromFile(FilePart file) {
        return ExcelReader.read(file, UserDTO.class)
                .flatMapMany(userService::createAll)
                .onErrorMap(e -> {
                    logger.error("Failed import from file: ", e);
                    return new RuntimeException("Failed import from file", e);
                });
    }

}
