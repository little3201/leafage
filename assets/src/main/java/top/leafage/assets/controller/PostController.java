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

package top.leafage.assets.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import top.leafage.assets.domain.dto.PostDTO;
import top.leafage.assets.service.PostService;
import top.leafage.common.poi.reactive.ReactiveExcelReader;


/**
 * posts controller
 *
 * @author wq li
 */
@RestController
@RequestMapping("/posts")
public class PostController {

    private final Logger logger = LoggerFactory.getLogger(PostController.class);

    private final PostService postService;

    /**
     * <p>Constructor for PostController.</p>
     *
     * @param postService a {@link PostService} object
     */
    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * 分页查询
     *
     * @param page       页码
     * @param size       大小
     * @param sortBy     排序字段
     * @param descending 是否倒序
     * @param filters    filters
     * @return 查询到数据集，异常时返回204
     */
    @GetMapping
    public Mono<ServerResponse> retrieve(@RequestParam int page, @RequestParam int size,
                                         String sortBy, boolean descending, String filters) {
        return postService.retrieve(page, size, sortBy, descending, filters)
                .flatMap(voPage -> ServerResponse.ok().bodyValue(voPage));
    }

    /**
     * 根据 id 查询
     *
     * @param id the pk.
     * @return 查询到数据，异常时返回204
     */
    @GetMapping("/{id}")
    public Mono<ServerResponse> fetch(@PathVariable Long id) {
        return postService.fetch(id)
                .flatMap(vo -> ServerResponse.ok().bodyValue(vo))
                .onErrorResume(ResponseStatusException.class,
                        e -> ServerResponse.notFound().build());
    }

    /**
     * 添加信息
     *
     * @param dto 要添加的数据
     * @return 添加后的信息，否则返回417状态码
     */
    @PostMapping
    public Mono<ServerResponse> create(@RequestBody @Valid PostDTO dto) {
        return postService.create(dto)
                .flatMap(vo -> ServerResponse.status(HttpStatus.CREATED).bodyValue(vo))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    /**
     * 修改信息
     *
     * @param id  the pk.
     * @param dto 要修改的数据
     * @return 修改后的信息，否则返回417状态码
     */
    @PutMapping("/{id}")
    public Mono<ServerResponse> modify(@PathVariable Long id, @RequestBody @Valid PostDTO dto) {
        return postService.modify(id, dto)
                .flatMap(vo -> ServerResponse.status(HttpStatus.CREATED).bodyValue(vo))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }


    /**
     * 删除
     *
     * @param id the pk.
     * @return 查询到数据，异常时返回204
     */
    @DeleteMapping("/{id}")
    public Mono<ServerResponse> remove(@PathVariable Long id) {
        return postService.remove(id)
                .then(ServerResponse.noContent().build())
                .onErrorResume(ResponseStatusException.class,
                        e -> ServerResponse.notFound().build());
    }

    /**
     * Import the records.
     *
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_schemas:import')")
    @PostMapping("/import")
    public Mono<ServerResponse> importFromFile(FilePart file) {
        return ReactiveExcelReader.read(file, PostDTO.class)
                .flatMapMany(postService::createAll)
                .collectList()
                .flatMap(vo -> ServerResponse.ok().bodyValue(vo))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
}
