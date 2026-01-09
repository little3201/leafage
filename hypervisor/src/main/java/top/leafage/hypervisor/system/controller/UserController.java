/*
 * Copyright (c) 2026.  little3201.
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
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.poi.reactive.ReactiveExcelReader;
import top.leafage.hypervisor.system.domain.dto.UserDTO;
import top.leafage.hypervisor.system.domain.vo.UserVO;
import top.leafage.hypervisor.system.service.UserService;

/**
 * user controller
 *
 * @author wq li
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    /**
     * Constructor for UserController.
     *
     * @param userService a {@link UserService} object
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * retrieve.
     *
     * @param page a int
     * @param size a int
     * @return a {@link org.springframework.http.ResponseEntity} object
     */
    @GetMapping
    public Mono<Page<UserVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                       String sortBy, boolean descending, String filters) {
        return userService.retrieve(page, size, sortBy, descending, filters);
    }

    /**
     * 根据 id 查询
     *
     * @param id user the pk.
     * @return 查询的数据
     */
    @GetMapping("/{id}")
    public Mono<UserVO> fetch(@PathVariable Long id) {
        return userService.fetch(id);
    }

    /**
     * 添加信息
     *
     * @param dto 要添加的数据
     * @return 修改后的信息
     */
    @PostMapping
    public Mono<UserVO> create(@RequestBody @Valid UserDTO dto) {
        return userService.create(dto);
    }

    /**
     * 修改信息
     *
     * @param id  user the pk.
     * @param dto 要修改的数据
     * @return 修改后的信息
     */
    @PutMapping("/{id}")
    public Mono<UserVO> modify(@PathVariable Long id, @RequestBody @Valid UserDTO dto) {
        return userService.modify(id, dto);
    }

    /**
     * 删除信息
     *
     * @param id user the pk.
     * @return 200状态码
     */
    @DeleteMapping("/{id}")
    public Mono<Void> remove(@PathVariable Long id) {
        return userService.remove(id);
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
        return userService.enable(id);
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
        return userService.unlock(id);
    }

    /**
     * Import the records.
     *
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_users:import')")
    @PostMapping("/import")
    public Flux<UserVO> importFromFile(FilePart file) {
        return ReactiveExcelReader.read(file, UserDTO.class)
                .flatMapMany(userService::createAll);
    }

}
