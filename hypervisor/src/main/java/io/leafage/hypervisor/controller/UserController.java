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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.leafage.common.poi.ExcelReader;

import java.security.Principal;
import java.util.List;

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
     * Retrieves a paginated list of records.
     *
     * @param page       The page number.
     * @param size       The number of records per page.
     * @param sortBy     The field to sort by.
     * @param descending Whether sorting should be in descending order.
     * @param filters    The filters.
     * @return A paginated list of records, or 204 status code if an error occurs.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_users')")
    @GetMapping
    public ResponseEntity<Page<UserVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                 String sortBy, boolean descending, String filters) {
        Page<UserVO> voPage;
        try {
            voPage = userService.retrieve(page, size, sortBy, descending, filters);
        } catch (Exception e) {
            logger.info("Retrieve user error: ", e);
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
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_users')")
    @GetMapping("/{id}")
    public ResponseEntity<UserVO> fetch(@PathVariable Long id) {
        UserVO vo;
        try {
            vo = userService.fetch(id);
        } catch (Exception e) {
            logger.info("Fetch user error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(vo);
    }

    /**
     * 是否存在
     *
     * @param username 名称
     * @param id       主键
     * @return 如果查询到数据，返回查询到的信息，否则返回204状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_users')")
    @GetMapping("/exists")
    public ResponseEntity<Boolean> exists(@RequestParam String username, Long id) {
        boolean exists;
        try {
            exists = userService.exists(username, id);
        } catch (Exception e) {
            logger.info("Check user exists error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(exists);
    }

    /**
     * 查询当前用户
     *
     * @param principal 当前用户
     * @return 如果查询到数据，返回查询到的信息，否则返回204状态码
     */
    @GetMapping("/me")
    public ResponseEntity<UserVO> fetchMe(Principal principal) {
        UserVO vo;
        try {
            vo = userService.findByUsername(principal.getName());
        } catch (Exception e) {
            logger.info("Fetch me error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(vo);
    }

    /**
     * 添加信息.
     *
     * @param dto 要修改的数据
     * @return 如果添加数据成功，返回添加后的信息，否则返回417状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_users:create')")
    @PostMapping
    public ResponseEntity<UserVO> create(@Valid @RequestBody UserDTO dto) {
        UserVO vo;
        try {
            vo = userService.create(dto);
        } catch (Exception e) {
            logger.error("Create user error: ", e);
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
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_users:modify')")
    @PutMapping("/{id}")
    public ResponseEntity<UserVO> modify(@PathVariable Long id,
                                         @Valid @RequestBody UserDTO dto) {
        UserVO vo;
        try {
            vo = userService.modify(id, dto);
        } catch (Exception e) {
            logger.error("Modify user error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(vo);
    }

    /**
     * Enable a record when enabled is false or disable when enabled is ture.
     *
     * @param id The record ID.
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_users:enable')")
    @PatchMapping("/{id}")
    public ResponseEntity<Boolean> enable(@PathVariable Long id) {
        boolean enabled;
        try {
            enabled = userService.enable(id);
        } catch (Exception e) {
            logger.error("Toggle enabled error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(enabled);
    }

    /**
     * Import the records.
     *
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_users:import')")
    @PostMapping("/import")
    public ResponseEntity<List<UserVO>> importFromFile(MultipartFile file) {
        List<UserVO> voList;
        try {
            List<UserDTO> dtoList = ExcelReader.read(file.getInputStream(), UserDTO.class);
            voList = userService.createAll(dtoList);
        } catch (Exception e) {
            logger.error("Import user error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().body(voList);
    }

}
