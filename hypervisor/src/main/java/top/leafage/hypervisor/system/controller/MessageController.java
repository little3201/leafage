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

package top.leafage.hypervisor.system.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.leafage.hypervisor.system.domain.dto.MessageDTO;
import top.leafage.hypervisor.system.domain.vo.MessageVO;
import top.leafage.hypervisor.system.service.MessageService;

import java.security.Principal;

/**
 * messages controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/messages")
public class MessageController {

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
        Page<MessageVO> voPage = messageService.retrieve(page, size, sortBy, descending, String.format("receiver:eq:%s", principal.getName()));
        return ResponseEntity.ok(voPage);
    }

    /**
     * fetch.
     *
     * @param id the pk.
     * @return the result.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MessageVO> fetch(@PathVariable Long id) {
        MessageVO vo = messageService.fetch(id);
        return ResponseEntity.ok(vo);
    }

    /**
     * 新增信息
     *
     * @param dto the request body.
     * @return the result.
     */
    @PostMapping
    public ResponseEntity<MessageVO> create(@Valid @RequestBody MessageDTO dto) {
        MessageVO vo = messageService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    /**
     * modify.
     *
     * @param id  the pk.
     * @param dto the request body.
     * @return the result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_groups:modify')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageVO> modify(@PathVariable Long id, @RequestBody MessageDTO dto) {
        MessageVO vo = messageService.modify(id, dto);
        return ResponseEntity.accepted().body(vo);
    }

    /**
     * remove.
     *
     * @param id the pk.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_groups:remove')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        messageService.remove(id);
        return ResponseEntity.noContent().build();
    }

}
