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

package io.leafage.assets.controller;

import io.leafage.assets.dto.RegionDTO;
import io.leafage.assets.service.RegionService;
import io.leafage.assets.vo.RegionVO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.leafage.common.poi.ExcelReader;

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
     * <p>Constructor for RegionController.</p>
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
        Page<RegionVO> voPage;
        try {
            voPage = regionService.retrieve(page, size, sortBy, descending, filters);
        } catch (Exception e) {
            logger.error("Retrieve region error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voPage);
    }

    /**
     * 根据 id 查询
     *
     * @param id 业务id
     * @return 查询的数据，异常时返回204状态码
     */
    @GetMapping("/{id}")
    public ResponseEntity<RegionVO> fetch(@PathVariable Long id) {
        RegionVO vo;
        try {
            vo = regionService.fetch(id);
        } catch (Exception e) {
            logger.error("Fetch region error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(vo);
    }

    /**
     * 是否存在
     *
     * @param name 名称
     * @param id   主键
     * @return 如果查询到数据，返回查询到的信息，否则返回204状态码
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> exists(@RequestParam String name, Long id) {
        boolean exists;
        try {
            exists = regionService.exists(name, id);
        } catch (Exception e) {
            logger.info("Check region exists error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(exists);
    }

    /**
     * 添加信息
     *
     * @param dto 要添加的数据
     * @return 如果添加数据成功，返回添加后的信息，否则返回417状态码
     */
    @PostMapping
    public ResponseEntity<RegionVO> create(@Valid @RequestBody RegionDTO dto) {
        RegionVO vo;
        try {
            vo = regionService.create(dto);
        } catch (Exception e) {
            logger.error("Create region error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    /**
     * 修改信息
     *
     * @param id  主键
     * @param dto 要修改的数据
     * @return 如果修改数据成功，返回修改后的信息，否则返回304状态码
     */
    @PutMapping("/{id}")
    public ResponseEntity<RegionVO> modify(@PathVariable Long id, @RequestBody RegionDTO dto) {
        RegionVO regionVO;
        try {
            regionVO = regionService.modify(id, dto);
        } catch (Exception e) {
            logger.error("Modify region error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(regionVO);
    }

    /**
     * 删除信息
     *
     * @param id 主键
     * @return 如果删除成功，返回200状态码，否则返回417状态码
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        try {
            regionService.remove(id);
        } catch (Exception e) {
            logger.error("Remove region error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Enable a record when enabled is false or disable when enabled is ture.
     *
     * @param id The record ID.
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_regions:enable')")
    @PatchMapping("/{id}")
    public ResponseEntity<Boolean> enable(@PathVariable Long id) {
        boolean enabled;
        try {
            enabled = regionService.enable(id);
        } catch (Exception e) {
            logger.error("Toggle enabled error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(enabled);
    }

    /**
     * Import the records.
     *
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_regions:import')")
    @PostMapping("/import")
    public ResponseEntity<List<RegionVO>> importFromFile(MultipartFile file) {
        List<RegionVO> voList;
        try {
            List<RegionDTO> dtoList = ExcelReader.read(file.getInputStream(), RegionDTO.class);
            voList = regionService.createAll(dtoList);
        } catch (Exception e) {
            logger.error("Import region error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().body(voList);
    }

}
