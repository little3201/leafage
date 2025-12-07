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

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.domain.dto.MessageDTO;
import top.leafage.hypervisor.service.MessageService;

import java.security.Principal;

/**
 * message controller
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
    public Mono<ServerResponse> retrieve(@RequestParam int page, @RequestParam int size,
                                         String sortBy, boolean descending, Principal principal) {
        return messageService.retrieve(page, size, sortBy, descending, String.format("receiver:eq:%s", principal.getName()))
                .flatMap(voPage -> ServerResponse.ok().bodyValue(voPage));
    }

    /**
     * 根据 id 查询
     *
     * @param id the pk.
     * @return 查询的数据
     */
    @GetMapping("/{id}")
    public Mono<ServerResponse> fetch(@PathVariable Long id) {
        return messageService.fetch(id)
                .flatMap(vo -> ServerResponse.ok().bodyValue(vo))
                .onErrorResume(ResponseStatusException.class,
                        e -> ServerResponse.notFound().build());
    }

    /**
     * 添加
     *
     * @param dto 要添加的数据
     * @return 添加后的信息
     */
    @PostMapping
    public Mono<ServerResponse> create(@RequestBody @Valid MessageDTO dto) {
        return messageService.create(dto)
                .flatMap(vo -> ServerResponse.status(HttpStatus.CREATED).bodyValue(vo))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    /**
     * 删除
     *
     * @param id the pk.
     * @return 200状态码
     */
    @DeleteMapping("/{id}")
    public Mono<ServerResponse> remove(@PathVariable Long id) {
        return messageService.remove(id)
                .then(ServerResponse.noContent().build())
                .onErrorResume(ResponseStatusException.class,
                        e -> ServerResponse.notFound().build());
    }

}
