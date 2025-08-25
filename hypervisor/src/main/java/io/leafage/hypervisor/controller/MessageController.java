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
    public Mono<ResponseEntity<Page<MessageVO>>> retrieve(@RequestParam int page, @RequestParam int size,
                                                          String sortBy, boolean descending, Authentication authentication) {
        return messageService.retrieve(page, size, sortBy, descending, authentication.getName())
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.error("Retrieve messages error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    /**
     * 根据 id 查询
     *
     * @param id 主键
     * @return 查询的数据，异常时返回204状态码
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MessageVO>> fetch(@PathVariable Long id) {
        return messageService.fetch(id)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.error("Fetch message error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    /**
     * 是否已存在
     *
     * @param title 标题
     * @return true-是，false-否
     */
    @GetMapping("/exists")
    public Mono<ResponseEntity<Boolean>> exists(@RequestParam String title, Long id) {
        return messageService.exists(title, id)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.error("Check is exists error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    /**
     * 添加
     *
     * @param dto 要添加的数据
     * @return 添加后的信息，异常时返回417状态码
     */
    @PostMapping
    public Mono<ResponseEntity<MessageVO>> create(@RequestBody @Valid MessageDTO dto) {
        return messageService.create(dto)
                .map(vo -> ResponseEntity.status(HttpStatus.CREATED).body(vo))
                .onErrorResume(e -> {
                    logger.error("Create message occurred an error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build());
                });
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return 200状态码，异常时返回417状态码
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> remove(@PathVariable Long id) {
        return messageService.remove(id)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorResume(e -> {
                    logger.error("Remove message error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build());
                });
    }

}
