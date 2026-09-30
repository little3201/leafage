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

package top.leafage.hypervisor.regions.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.leafage.common.poi.excel.ExcelReader;
import top.leafage.hypervisor.regions.domain.dto.RegionDTO;
import top.leafage.hypervisor.regions.domain.vo.RegionVO;
import top.leafage.hypervisor.regions.service.RegionService;

import java.io.IOException;
import java.util.List;

/**
 * Region controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/regions")
public class RegionController {

    private final RegionService regionService;

    /**
     * Constructor for RegionController.
     *
     * @param regionService a {@link RegionService} object
     */
    public RegionController(RegionService regionService) {
        this.regionService = regionService;
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
    @GetMapping
    public ResponseEntity<Page<RegionVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                   String sortBy, boolean descending, String filters) {
        Page<RegionVO> voPage = regionService.retrieve(page, size, sortBy, descending, filters);
        return ResponseEntity.ok(voPage);
    }

    /**
     * Fetch.
     *
     * @param id The pk.
     * @return the result.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RegionVO> fetch(@PathVariable Long id) {
        RegionVO vo = regionService.fetch(id);
        return ResponseEntity.ok(vo);
    }

    /**
     * subset.
     *
     * @param id The pk.
     * @return the result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('regions')")
    @GetMapping("/subset")
    public ResponseEntity<List<RegionVO>> subset(Long id) {
        List<RegionVO> voList = regionService.subset(id);
        return ResponseEntity.ok(voList);
    }

    /**
     * create.
     *
     * @param dto the request body.
     * @return the result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('regions:create')")
    @PostMapping
    public ResponseEntity<RegionVO> create(@Valid @RequestBody RegionDTO dto) {
        RegionVO vo = regionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    /**
     * modify.
     *
     * @param id  The pk.
     * @param dto the request body.
     * @return the result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('regions:modify')")
    @PutMapping("/{id}")
    public ResponseEntity<RegionVO> modify(@PathVariable Long id, @RequestBody RegionDTO dto) {
        RegionVO vo = regionService.modify(id, dto);
        return ResponseEntity.accepted().body(vo);
    }

    /**
     * Remove.
     *
     * @param id The pk.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('regions:remove')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        regionService.remove(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Enable.
     *
     * @param id The pk.
     * @return the result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('regions:enable')")
    @PatchMapping("/{id}/enable")
    public ResponseEntity<Boolean> enable(@PathVariable Long id) {
        boolean enabled = regionService.enable(id);
        return ResponseEntity.ok(enabled);
    }

    /**
     * Disable.
     *
     * @param id The pk.
     * @return the result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('regions:disable')")
    @PatchMapping("/{id}/disable")
    public ResponseEntity<Boolean> disable(@PathVariable Long id) {
        boolean disable = regionService.disable(id);
        return ResponseEntity.ok(disable);
    }

    /**
     * import.
     *
     * @return the result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('regions:import')")
    @PostMapping("/import")
    public ResponseEntity<List<RegionVO>> importFromFile(MultipartFile file) throws IOException {
        List<RegionDTO> dtoList = ExcelReader.read(file.getInputStream(), RegionDTO.class);
        List<RegionVO> voList = regionService.createAll(dtoList);
        return ResponseEntity.ok().body(voList);
    }

}
