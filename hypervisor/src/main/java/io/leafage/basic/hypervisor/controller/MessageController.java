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

package io.leafage.basic.hypervisor.controller;

import io.leafage.basic.hypervisor.dto.MessageDTO;
import io.leafage.basic.hypervisor.service.MessageService;
import io.leafage.basic.hypervisor.vo.MessageVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * <p>Constructor for MessageController.</p>
     *
     * @param messageService a {@link io.leafage.basic.hypervisor.service.MessageService} object
     */
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * 分页查询
     *
     * @param page       页码
     * @param size       大小
     * @param sortBy     排序字段
     * @param descending 排序方向
     * @return 查询的数据，异常时返回204状态码
     */
    @GetMapping
    public ResponseEntity<Page<MessageVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                    String sortBy, boolean descending, String name) {
        Page<MessageVO> voPage;
        try {
            voPage = messageService.retrieve(page, size, sortBy, descending, name);
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
    public ResponseEntity<MessageVO> create(@RequestBody @Valid MessageDTO dto) {
        MessageVO vo;
        try {
            vo = messageService.create(dto);
        } catch (Exception e) {
            logger.info("Create message error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }
}
