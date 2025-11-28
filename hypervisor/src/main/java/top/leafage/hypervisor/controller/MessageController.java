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

package top.leafage.hypervisor.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.leafage.hypervisor.domain.dto.MessageDTO;
import top.leafage.hypervisor.domain.vo.MessageVO;
import top.leafage.hypervisor.service.MessageService;

import java.security.Principal;

/**
 * messages controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/messages")
public class MessageController {

    private final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final MessageService messageService;

    /**
     * Constructor for MessageController.
     *
     * @param messageService a {@link MessageService} object
     */
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }


    /**
     * Retrieves a paginated list of records.
     *
     * @param page       The page number.
     * @param size       The number of records per page.
     * @param sortBy     The field to sort by.
     * @param descending Whether sorting should be in descending order.
     * @param principal  The principal.
     * @return A paginated list of records, or 204 status code if an error occurs.
     */
    @GetMapping
    public ResponseEntity<Page<MessageVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                    String sortBy, boolean descending, Principal principal) {
        Page<MessageVO> voPage;
        try {
            voPage = messageService.retrieve(page, size, sortBy, descending, String.format("receiver:eq:%s", principal.getName()));
        } catch (Exception e) {
            logger.info("Retrieve message error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voPage);
    }

    /**
     * 根据 id 查询
     *
     * @param id 主键
     * @return 查询的数据，异常时返回204状态码
     */
    @GetMapping("/{id}")
    public ResponseEntity<MessageVO> fetch(@PathVariable Long id) {
        MessageVO vo;
        try {
            vo = messageService.fetch(id);
        } catch (Exception e) {
            logger.info("Fetch message error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(vo);
    }

    /**
     * 新增信息
     *
     * @param dto 要添加的数据
     * @return 如果添加数据成功，返回添加后的信息，否则返回417状态码
     */
    @PostMapping
    public ResponseEntity<MessageVO> create(@Valid @RequestBody MessageDTO dto) {
        MessageVO vo;
        try {
            boolean existed = messageService.exists(dto.getTitle(), null);
            if (existed) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            vo = messageService.create(dto);
        } catch (Exception e) {
            logger.info("Create message error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    /**
     * 修改信息
     *
     * @param id  主键
     * @param dto 要修改的数据
     * @return 如果修改数据成功，返回修改后的信息，否则返回304状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_groups:modify')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageVO> modify(@PathVariable Long id, @RequestBody MessageDTO dto) {
        MessageVO vo;
        try {
            boolean existed = messageService.exists(dto.getTitle(), id);
            if (existed) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            vo = messageService.modify(id, dto);
        } catch (Exception e) {
            logger.error("Modify message error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(vo);
    }

    /**
     * 删除信息
     *
     * @param id 主键
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_groups:remove')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        try {
            messageService.remove(id);
        } catch (Exception e) {
            logger.error("Remove message error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().build();
    }

}
