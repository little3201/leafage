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

package io.leafage.hypervisor.controller;

import io.leafage.hypervisor.domain.dto.DictionaryDTO;
import io.leafage.hypervisor.domain.vo.DictionaryVO;
import io.leafage.hypervisor.service.DictionaryService;
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
 * dictionary controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/dictionaries")
public class DictionaryController {

    private final Logger logger = LoggerFactory.getLogger(DictionaryController.class);

    private final DictionaryService dictionaryService;

    /**
     * <p>Constructor for DictionaryController.</p>
     *
     * @param dictionaryService a {@link DictionaryService} object
     */
    public DictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }


    /**
     * Retrieves a paginated list of records.
     *
     * @param page       The page number.
     * @param size       The number of records per page.
     * @param sortBy     The field to sort by.
     * @param descending Whether sorting should be in descending order.
     * @param filters    The filters.
     * @return A paginated list of records, or 204 status code if an error occurs.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_dictionaries')")
    @GetMapping
    public ResponseEntity<Page<DictionaryVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                       String sortBy, boolean descending, String filters) {
        Page<DictionaryVO> voPage;
        try {
            voPage = dictionaryService.retrieve(page, size, sortBy, descending, filters);
        } catch (Exception e) {
            logger.error("Retrieve dictionary error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voPage);
    }

    /**
     * 查询下级数据
     *
     * @param id a {@link Long} object
     * @return 查询到的数据，否则返回空
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_dictionaries')")
    @GetMapping("/{id}/subset")
    public ResponseEntity<List<DictionaryVO>> subset(@PathVariable Long id) {
        List<DictionaryVO> voList;
        try {
            voList = dictionaryService.subset(id);
        } catch (Exception e) {
            logger.info("Retrieve dictionary subset error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voList);
    }

    /**
     * 根据 id 查询
     *
     * @param id 业务id
     * @return 查询的数据，异常时返回204状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_dictionaries')")
    @GetMapping("/{id}")
    public ResponseEntity<DictionaryVO> fetch(@PathVariable Long id) {
        DictionaryVO vo;
        try {
            vo = dictionaryService.fetch(id);
        } catch (Exception e) {
            logger.error("Fetch dictionary error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(vo);
    }

    /**
     * 添加信息
     *
     * @param dto 要添加的数据
     * @return 如果添加数据成功，返回添加后的信息，否则返回417状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_dictionaries:create')")
    @PostMapping
    public ResponseEntity<DictionaryVO> create(@Valid @RequestBody DictionaryDTO dto) {
        DictionaryVO vo;
        try {
            boolean existed = dictionaryService.exists(dto.getName(), null);
            if (existed) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            vo = dictionaryService.create(dto);
        } catch (Exception e) {
            logger.error("Create dictionary error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    /**
     * 修改信息
     *
     * @param dto 要添加的数据
     * @param id  a {@link Long} object
     * @return 如果添加数据成功，返回添加后的信息，否则返回417状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_dictionaries:modify')")
    @PutMapping("/{id}")
    public ResponseEntity<DictionaryVO> modify(@PathVariable Long id, @Valid @RequestBody DictionaryDTO dto) {
        DictionaryVO vo;
        try {
            boolean existed = dictionaryService.exists(dto.getName(), id);
            if (existed) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            vo = dictionaryService.modify(id, dto);
        } catch (Exception e) {
            logger.error("Modify dictionary error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(vo);
    }

    /**
     * 删除信息
     *
     * @param id 主键
     * @return 如果删除成功，返回200状态码，否则返回417状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_dictionaries:remove')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        try {
            dictionaryService.remove(id);
        } catch (Exception e) {
            logger.error("Remove dictionary error: ", e);
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
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_dictionaries:enable')")
    @PatchMapping("/{id}")
    public ResponseEntity<Boolean> enable(@PathVariable Long id) {
        boolean enabled;
        try {
            enabled = dictionaryService.enable(id);
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
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_dictionaries:import')")
    @PostMapping("/import")
    public ResponseEntity<List<DictionaryVO>> importFromFile(MultipartFile file) {
        List<DictionaryVO> voList;
        try {
            List<DictionaryDTO> dtoList = ExcelReader.read(file.getInputStream(), DictionaryDTO.class);
            voList = dictionaryService.createAll(dtoList);
        } catch (Exception e) {
            logger.error("Import dictionary error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().body(voList);
    }

}
