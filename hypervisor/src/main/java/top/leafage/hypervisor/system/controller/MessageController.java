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
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.system.domain.dto.MessageDTO;
import top.leafage.hypervisor.system.domain.vo.MessageVO;
import top.leafage.hypervisor.system.service.MessageService;

import java.security.Principal;

/**
 * message controller
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
     * 分页查询
     *
     * @param page 页码
     * @param size 大小
     * @return 查询的数据集
     */
    @GetMapping
    public Mono<Page<MessageVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                          String sortBy, boolean descending, Principal principal) {
        return messageService.retrieve(page, size, sortBy, descending, String.format("receiver:eq:%s", principal.getName()));
    }

    /**
     * 根据 id 查询
     *
     * @param id the pk.
     * @return 查询的数据
     */
    @GetMapping("/{id}")
    public Mono<MessageVO> fetch(@PathVariable Long id) {
        return messageService.fetch(id);
    }

    /**
     * 添加
     *
     * @param dto 要添加的数据
     * @return 添加后的信息
     */
    @PostMapping
    public Mono<MessageVO> create(@RequestBody @Valid MessageDTO dto) {
        return messageService.create(dto);
    }

    /**
     * 删除
     *
     * @param id the pk.
     * @return 200状态码
     */
    @DeleteMapping("/{id}")
    public Mono<Void> remove(@PathVariable Long id) {
        return messageService.remove(id);
    }

}
