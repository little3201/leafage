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

import io.leafage.hypervisor.dto.MessageDTO;
import io.leafage.hypervisor.service.MessageService;
import io.leafage.hypervisor.vo.MessageVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * message controller
 *
 * @author wq li
 */
@Validated
@RestController
@RequestMapping("/messages")
public class MessageController {

    private final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final MessageService messageService;

    /**
     * <p>Constructor for MessageController.</p>
     *
     * @param messageService a {@link MessageService} object
     */
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 大小
     * @return 查询的数据集，异常时返回204状态码
     */
    @GetMapping
    public ResponseEntity<Mono<Page<MessageVO>>> retrieve(@RequestParam int page, @RequestParam int size,
                                                          String sortBy, boolean descending, Authentication authentication) {
        Mono<Page<MessageVO>> pageMono;
        try {
            pageMono = messageService.retrieve(page, size, sortBy, descending, authentication.getName());
        } catch (Exception e) {
            logger.error("Retrieve messages occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pageMono);
    }

    /**
     * 根据 id 查询
     *
     * @param id 主键
     * @return 查询的数据，异常时返回204状态码
     */
    @GetMapping("/{id}")
    public ResponseEntity<Mono<MessageVO>> fetch(@PathVariable Long id) {
        Mono<MessageVO> voMono;
        try {
            voMono = messageService.fetch(id);
        } catch (Exception e) {
            logger.error("Fetch message occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voMono);
    }

    /**
     * 添加
     *
     * @param messageDTO 要添加的数据
     * @return 添加后的信息，异常时返回417状态码
     */
    @PostMapping
    public ResponseEntity<Mono<MessageVO>> create(@RequestBody @Valid MessageDTO messageDTO) {
        Mono<MessageVO> voMono;
        try {
            voMono = messageService.create(messageDTO);
        } catch (Exception e) {
            logger.error("Create message occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(voMono);
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return 200状态码，异常时返回417状态码
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<Void>> remove(@PathVariable Long id) {
        Mono<Void> voidMono;
        try {
            voidMono = messageService.remove(id);
        } catch (Exception e) {
            logger.error("Remove message occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok(voidMono);
    }

}
