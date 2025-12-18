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
package top.leafage.hypervisor.assets.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.leafage.hypervisor.assets.domain.dto.PostDTO;
import top.leafage.hypervisor.assets.domain.vo.PostVO;
import top.leafage.hypervisor.assets.service.PostService;
import top.leafage.common.poi.ExcelReader;

import java.io.IOException;
import java.util.List;

/**
 * posts controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/posts")
public class PostController {

    private final Logger logger = LoggerFactory.getLogger(PostController.class);

    private final PostService postService;

    /**
     * Constructor for PostController.
     *
     * @param postService a {@link PostService} object
     */
    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * retrieve with page .
     *
     * @param page       页码
     * @param size       大小
     * @param sortBy     排序字段
     * @param descending a boolean
     * @return 分页结果集
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_posts')")
    @GetMapping
    public ResponseEntity<Page<PostVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                 String sortBy, boolean descending, String filters) {
        Page<PostVO> voPage = postService.retrieve(page, size, sortBy, descending, filters);
        return ResponseEntity.ok(voPage);
    }

    /**
     * fetch with id .
     *
     * @param id the pk.
     * @return 帖子信息，不包括内容
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_posts')")
    @GetMapping("/{id}")
    public ResponseEntity<PostVO> fetch(@PathVariable Long id) {
        PostVO vo = postService.fetch(id);
        return ResponseEntity.ok(vo);
    }

    /**
     * 保存文章信息
     *
     * @param dto 文章内容
     * @return 帖子信息
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_posts:create')")
    @PostMapping
    public ResponseEntity<PostVO> create(@Valid @RequestBody PostDTO dto) {
        PostVO vo = postService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    /**
     * 修改帖子信息
     *
     * @param id  the pk.
     * @param dto 帖子信息
     * @return 修改后的帖子信息
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_posts:modify')")
    @PutMapping("/{id}")
    public ResponseEntity<PostVO> modify(@PathVariable Long id, @Valid @RequestBody PostDTO dto) {
        PostVO vo = postService.modify(id, dto);
        return ResponseEntity.accepted().body(vo);
    }

    /**
     * 删除帖子信息
     *
     * @param id the pk.
     * @return 删除结果
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_posts:remove')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        postService.remove(id);
        return ResponseEntity.ok().build();
    }

    /**
     * enable.
     *
     * @param id the pk..
     * @return the result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_posts:enable')")
    @PatchMapping("/{id}")
    public ResponseEntity<Boolean> enable(@PathVariable Long id) {
        boolean enabled = postService.enable(id);
        return ResponseEntity.ok(enabled);
    }

    /**
     * import..
     *
     * @return the result.
     */
    @PreAuthorize("hasAuthority('SCOPE_posts:import')")
    @PostMapping("/import")
    public ResponseEntity<List<PostVO>> importFromFile(MultipartFile file) throws IOException {
        List<PostDTO> dtoList = ExcelReader.read(file.getInputStream(), PostDTO.class);
        List<PostVO> voList = postService.createAll(dtoList);
        return ResponseEntity.ok().body(voList);
    }

}
