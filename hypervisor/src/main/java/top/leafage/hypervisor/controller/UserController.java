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

import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import top.leafage.common.poi.reactive.ReactiveExcelReader;
import top.leafage.hypervisor.domain.dto.UserDTO;
import top.leafage.hypervisor.service.UserService;

/**
 * user controller
 *
 * @author wq li
 */
@Validated
@RestController
@RequestMapping("/users")
public class UserController {

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
    public Mono<ServerResponse> retrieve(@RequestParam int page, @RequestParam int size,
                                         String sortBy, boolean descending, String filters) {
        return userService.retrieve(page, size, sortBy, descending, filters)
                .flatMap(voPage -> ServerResponse.ok().bodyValue(voPage));
    }

    /**
     * 根据 id 查询
     *
     * @param id user 主键
     * @return 查询的数据
     */
    @GetMapping("/{id}")
    public Mono<ServerResponse> fetch(@PathVariable Long id) {
        return userService.fetch(id)
                .flatMap(vo -> ServerResponse.ok().bodyValue(vo))
                .onErrorResume(ResponseStatusException.class,
                        e -> ServerResponse.notFound().build());
    }

    /**
     * 添加信息
     *
     * @param dto 要添加的数据
     * @return 修改后的信息
     */
    @PostMapping
    public Mono<ServerResponse> create(@RequestBody @Validated UserDTO dto) {
        return userService.create(dto)
                .flatMap(vo -> ServerResponse.status(HttpStatus.CREATED).bodyValue(vo))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    /**
     * 修改信息
     *
     * @param id  user 主键
     * @param dto 要修改的数据
     * @return 修改后的信息
     */
    @PutMapping("/{id}")
    public Mono<ServerResponse> modify(@PathVariable Long id, @RequestBody @Validated UserDTO dto) {
        return userService.modify(id, dto)
                .flatMap(vo -> ServerResponse.ok().bodyValue(vo))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    /**
     * 删除信息
     *
     * @param id user 主键
     * @return 200状态码
     */
    @DeleteMapping("/{id}")
    public Mono<ServerResponse> remove(@PathVariable Long id) {
        return userService.remove(id)
                .then(ServerResponse.noContent().build())
                .onErrorResume(ResponseStatusException.class,
                        e -> ServerResponse.notFound().build());
    }

    /**
     * Enable a record when enabled is false or disable when enabled is ture.
     *
     * @param id The record ID.
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_users:enable')")
    @PatchMapping("/{id}")
    public Mono<ServerResponse> enable(@PathVariable Long id) {
        return userService.enable(id)
                .flatMap(b -> ServerResponse.ok().bodyValue(b))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    /**
     * Unlock a record when account is lock.
     *
     * @param id The record ID.
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_users:unlock')")
    @PatchMapping("/{id}/unlock")
    public Mono<ServerResponse> unlock(@PathVariable Long id) {
        return userService.unlock(id)
                .flatMap(b -> ServerResponse.ok().bodyValue(b))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    /**
     * Import the records.
     *
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_users:import')")
    @PostMapping("/import")
    public Mono<ServerResponse> importFromFile(FilePart file) {
        return ReactiveExcelReader.read(file, UserDTO.class)
                .flatMapMany(userService::createAll)
                .collectList()
                .flatMap(vo -> ServerResponse.ok().bodyValue(vo))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

}
