/*
 * Copyright(c) 2019-present the original author or authors.
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

package top.leafage.hypervisor.docs.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.leafage.common.poi.excel.ExcelReader;
import top.leafage.hypervisor.docs.domain.dto.ArchiveDTO;
import top.leafage.hypervisor.docs.domain.vo.ArchiveVO;
import top.leafage.hypervisor.docs.service.ArchiveService;

import java.io.IOException;
import java.util.List;

/**
 * Archive controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/archives")
public class ArchiveController {

    private final ArchiveService archiveService;

    public ArchiveController(ArchiveService archiveService) {
        this.archiveService = archiveService;
    }

    /**
     * 分页查询
     *
     * @param page       页码
     * @param size       分页数据大小，最大 500
     * @param sortBy     排序字段，若为 null，默认为主键 id
     * @param descending 排序方向，若为 false, 即正序排列
     * @param filters    过滤条件，格式：field:condition:value，如：name:like:test
     * @return 查询的数据集，异常时返回204状态码
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('archives')")
    @GetMapping
    public ResponseEntity<Page<ArchiveVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                    String sortBy, boolean descending, String filters) {
        Page<ArchiveVO> voPage = archiveService.retrieve(page, size, sortBy, descending, filters);
        return ResponseEntity.ok(voPage);
    }

    /**
     * Fetch.
     *
     * @param id The pk.
     * @return the result.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('archives')")
    @GetMapping("/{id}")
    public ResponseEntity<ArchiveVO> fetch(@PathVariable Long id) {
        ArchiveVO vo = archiveService.fetch(id);
        return ResponseEntity.ok(vo);
    }

    /**
     * create.
     *
     * @param dto the request body.
     * @return the result.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('archives:create')")
    @PostMapping
    public ResponseEntity<ArchiveVO> create(@Valid @RequestBody ArchiveDTO dto) {
        ArchiveVO vo = archiveService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    /**
     * modify.
     *
     * @param id  The pk.
     * @param dto the request body.
     * @return the result.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('archives:modify')")
    @PutMapping("/{id}")
    public ResponseEntity<ArchiveVO> modify(@PathVariable Long id, @RequestBody ArchiveDTO dto) {
        ArchiveVO vo = archiveService.modify(id, dto);
        return ResponseEntity.accepted().body(vo);
    }

    /**
     * Remove.
     *
     * @param id The pk.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('archives:remove')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        archiveService.remove(id);
        return ResponseEntity.ok().build();
    }

    /**
     * import.
     *
     * @return the result.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('archives:import')")
    @PostMapping("/import")
    public ResponseEntity<List<ArchiveVO>> importFromFile(MultipartFile file) throws IOException {
        List<ArchiveDTO> dtoList = ExcelReader.read(file.getInputStream(), ArchiveDTO.class);
        List<ArchiveVO> voList = archiveService.createAll(dtoList);
        return ResponseEntity.ok().body(voList);
    }

}
