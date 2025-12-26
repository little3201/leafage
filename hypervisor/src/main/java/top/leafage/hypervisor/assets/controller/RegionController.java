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
import top.leafage.hypervisor.assets.domain.dto.RegionDTO;
import top.leafage.hypervisor.assets.domain.vo.RegionVO;
import top.leafage.hypervisor.assets.service.RegionService;
import top.leafage.common.poi.ExcelReader;

import java.io.IOException;
import java.util.List;

/**
 * region controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/regions")
public class RegionController {

    private final Logger logger = LoggerFactory.getLogger(RegionController.class);

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
     * @param size       大小
     * @param sortBy     排序字段
     * @param descending 排序方向
     * @return 查询的数据集，异常时返回204状态码
     */
    @GetMapping
    public ResponseEntity<Page<RegionVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                   String sortBy, boolean descending, String filters) {
        Page<RegionVO> voPage = regionService.retrieve(page, size, sortBy, descending, filters);
        return ResponseEntity.ok(voPage);
    }

    /**
     * fetch.
     *
     * @param id th pk.
     * @return the result.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RegionVO> fetch(@PathVariable Long id) {
        RegionVO vo = regionService.fetch(id);
        return ResponseEntity.ok(vo);
    }

    /**
     * create.
     *
     * @param dto the request body.
     * @return the result.
     */
    @PostMapping
    public ResponseEntity<RegionVO> create(@Valid @RequestBody RegionDTO dto) {
        RegionVO vo = regionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    /**
     * modify.
     *
     * @param id  the pk.
     * @param dto the request body.
     * @return the result.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RegionVO> modify(@PathVariable Long id, @RequestBody RegionDTO dto) {
        RegionVO vo = regionService.modify(id, dto);
        return ResponseEntity.accepted().body(vo);
    }

    /**
     * remove.
     *
     * @param id the pk.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        regionService.remove(id);
        return ResponseEntity.ok().build();
    }

    /**
     * enable.
     *
     * @param id the pk..
     * @return the result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_regions:enable')")
    @PatchMapping("/{id}")
    public ResponseEntity<Boolean> enable(@PathVariable Long id) {
        boolean enabled = regionService.enable(id);
        return ResponseEntity.ok(enabled);
    }

    /**
     * import..
     *
     * @return the result.
     */
    @PreAuthorize("hasAuthority('SCOPE_regions:import')")
    @PostMapping("/import")
    public ResponseEntity<List<RegionVO>> importFromFile(MultipartFile file) throws IOException {
        List<RegionDTO> dtoList = ExcelReader.read(file.getInputStream(), RegionDTO.class);
        List<RegionVO> voList = regionService.createAll(dtoList);
        return ResponseEntity.ok().body(voList);
    }

}
