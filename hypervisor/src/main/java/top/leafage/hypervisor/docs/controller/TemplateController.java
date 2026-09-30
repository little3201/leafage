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
import top.leafage.hypervisor.docs.domain.dto.TemplateDTO;
import top.leafage.hypervisor.docs.domain.vo.TemplateVO;
import top.leafage.hypervisor.docs.service.TemplateService;

import java.io.IOException;
import java.util.List;

/**
 * Template controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/templates")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
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
    @PreAuthorize("hasRole('USER') || hasAuthority('templates')")
    @GetMapping
    public ResponseEntity<Page<TemplateVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                     String sortBy, boolean descending, String filters) {
        Page<TemplateVO> voPage = templateService.retrieve(page, size, sortBy, descending, filters);
        return ResponseEntity.ok(voPage);
    }

    /**
     * Fetch.
     *
     * @param id The pk.
     * @return the result.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('templates')")
    @GetMapping("/{id}")
    public ResponseEntity<TemplateVO> fetch(@PathVariable Long id) {
        TemplateVO vo = templateService.fetch(id);
        return ResponseEntity.ok(vo);
    }

    /**
     * create.
     *
     * @param dto the request body.
     * @return the result.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('templates:create')")
    @PostMapping
    public ResponseEntity<TemplateVO> create(@Valid @RequestBody TemplateDTO dto) {
        TemplateVO vo = templateService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    /**
     * modify.
     *
     * @param id  The pk.
     * @param dto the request body.
     * @return the result.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('templates:modify')")
    @PutMapping("/{id}")
    public ResponseEntity<TemplateVO> modify(@PathVariable Long id, @RequestBody TemplateDTO dto) {
        TemplateVO vo = templateService.modify(id, dto);
        return ResponseEntity.accepted().body(vo);
    }

    /**
     * Remove.
     *
     * @param id The pk.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('templates:remove')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        templateService.remove(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Enable.
     *
     * @param id The pk.
     * @return the result.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('templates:enable')")
    @PatchMapping("/{id}/enable")
    public ResponseEntity<Boolean> enable(@PathVariable Long id) {
        boolean enabled = templateService.enable(id);
        return ResponseEntity.ok(enabled);
    }

    /**
     * Disable.
     *
     * @param id The pk.
     * @return the result.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('templates:disable')")
    @PatchMapping("/{id}/disable")
    public ResponseEntity<Boolean> disable(@PathVariable Long id) {
        boolean disable = templateService.disable(id);
        return ResponseEntity.ok(disable);
    }

    /**
     * Publish.
     *
     * @param id The pk.
     * @return the result.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('templates:enable')")
    @PatchMapping("/{id}/publish")
    public ResponseEntity<Boolean> publish(@PathVariable Long id) {
        boolean published = templateService.publish(id);
        return ResponseEntity.ok(published);
    }

    /**
     * Archive.
     *
     * @param id The pk.
     * @return the result.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('templates:disable')")
    @PatchMapping("/{id}/archive")
    public ResponseEntity<Boolean> archive(@PathVariable Long id) {
        boolean archived = templateService.archive(id);
        return ResponseEntity.ok(archived);
    }

    /**
     * import.
     *
     * @return the result.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('templates:import')")
    @PostMapping("/import")
    public ResponseEntity<List<TemplateVO>> importFromFile(MultipartFile file) throws IOException {
        List<TemplateDTO> dtoList = ExcelReader.read(file.getInputStream(), TemplateDTO.class);
        List<TemplateVO> voList = templateService.createAll(dtoList);
        return ResponseEntity.ok().body(voList);
    }

}
