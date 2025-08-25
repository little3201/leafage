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

package io.leafage.assets.controller;

import io.leafage.assets.dto.PostDTO;
import io.leafage.assets.service.PostService;
import io.leafage.assets.vo.PostVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * posts controller
 *
 * @author wq li
 */
@Validated
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
    public ResponseEntity<Mono<Page<PostVO>>> retrieve(@RequestParam int page, @RequestParam int size,
                                                       String sortBy, boolean descending, String filters) {
        Mono<Page<PostVO>> pageMono;
        try {
            pageMono = postService.retrieve(page, size, sortBy, descending, filters);
        } catch (Exception e) {
            logger.error("Retrieve posts occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pageMono);
    }

    /**
     * title 关键字查询
     *
     * @param keyword 关键字
     * @return 查询到数据集，异常时返回204
     */
    @GetMapping("/search")
    public ResponseEntity<Flux<PostVO>> search(@RequestParam String keyword) {
        Flux<PostVO> voFlux;
        try {
            voFlux = postService.search(keyword);
        } catch (Exception e) {
            logger.error("Search posts occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voFlux);
    }

    /**
     * 根据 id 查询
     *
     * @param id 主键
     * @return 查询到数据，异常时返回204
     */
    @GetMapping("/{id}")
    public ResponseEntity<Mono<PostVO>> fetch(@PathVariable Long id) {
        Mono<PostVO> voMono;
        try {
            voMono = postService.fetch(id);
        } catch (Exception e) {
            logger.error("Fetch post occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voMono);
    }

    /**
     * 是否已存在
     *
     * @param title 标题
     * @return true-是，false-否
     */
    @GetMapping("/exists")
    public ResponseEntity<Mono<Boolean>> exists(@RequestParam String title, Long id) {
        Mono<Boolean> existsMono;
        try {
            existsMono = postService.exists(title, id);
        } catch (Exception e) {
            logger.error("Check post is exists occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(existsMono);
    }

    /**
     * 添加信息
     *
     * @param postDTO 要添加的数据
     * @return 添加后的信息，否则返回417状态码
     */
    @PostMapping
    public ResponseEntity<Mono<PostVO>> create(@RequestBody @Valid PostDTO postDTO) {
        Mono<PostVO> voMono;
        try {
            voMono = postService.create(postDTO);
        } catch (Exception e) {
            logger.error("Create post occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(voMono);
    }

    /**
     * 修改信息
     *
     * @param id      主键
     * @param postDTO 要修改的数据
     * @return 修改后的信息，否则返回304状态码
     */
    @PutMapping("/{id}")
    public ResponseEntity<Mono<PostVO>> modify(@PathVariable Long id, @RequestBody @Valid PostDTO postDTO) {
        Mono<PostVO> voMono;
        try {
            voMono = postService.modify(id, postDTO);
        } catch (Exception e) {
            logger.error("Modify post occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.accepted().body(voMono);
    }


    /**
     * 删除
     *
     * @param id 主键
     * @return 查询到数据，异常时返回204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<Void>> remove(@PathVariable Long id) {
        Mono<Void> voMono;
        try {
            voMono = postService.remove(id);
        } catch (Exception e) {
            logger.error("Remove post occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok(voMono);
    }

}
