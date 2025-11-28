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

package top.leafage.hypervisor.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.leafage.common.poi.ExcelReader;
import top.leafage.hypervisor.domain.dto.DictionaryDTO;
import top.leafage.hypervisor.domain.vo.DictionaryVO;
import top.leafage.hypervisor.service.DictionaryService;

import java.io.IOException;
import java.util.List;

/**
 * dictionary controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/dictionaries")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    /**
     * Constructor for DictionaryController.
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
        Page<DictionaryVO> voPage = dictionaryService.retrieve(page, size, sortBy, descending, filters);
        return ResponseEntity.ok(voPage);
    }

    /**
     * subset.
     *
     * @param id th pk.
     * @return th result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_dictionaries')")
    @GetMapping("/{id}/subset")
    public ResponseEntity<List<DictionaryVO>> subset(@PathVariable Long id) {
        List<DictionaryVO> voList = dictionaryService.subset(id);
        return ResponseEntity.ok(voList);
    }

    /**
     * fetch.
     *
     * @param id th pk.
     * @return th result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_dictionaries')")
    @GetMapping("/{id}")
    public ResponseEntity<DictionaryVO> fetch(@PathVariable Long id) {
        DictionaryVO vo = dictionaryService.fetch(id);
        return ResponseEntity.ok(vo);
    }

    /**
     * create.
     *
     * @param dto the request body.
     * @return the result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_dictionaries:create')")
    @PostMapping
    public ResponseEntity<DictionaryVO> create(@Valid @RequestBody DictionaryDTO dto) {
        DictionaryVO vo = dictionaryService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    /**
     * modify.
     *
     * @param dto the request body.
     * @param id  th pk.
     * @return the result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_dictionaries:modify')")
    @PutMapping("/{id}")
    public ResponseEntity<DictionaryVO> modify(@PathVariable Long id, @Valid @RequestBody DictionaryDTO dto) {
        DictionaryVO vo = dictionaryService.modify(id, dto);
        return ResponseEntity.accepted().body(vo);
    }

    /**
     * remove.
     *
     * @param id the pk.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_dictionaries:remove')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        dictionaryService.remove(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * enable/disable..
     *
     * @param id the pk..
     * @return the result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_dictionaries:enable')")
    @PatchMapping("/{id}")
    public ResponseEntity<Boolean> enable(@PathVariable Long id) {
        boolean enabled = dictionaryService.enable(id);
        return ResponseEntity.ok(enabled);
    }

    /**
     * import..
     *
     * @return the imported data.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_dictionaries:import')")
    @PostMapping("/import")
    public ResponseEntity<List<DictionaryVO>> importFromFile(MultipartFile file) throws IOException {
        List<DictionaryDTO> dtoList = ExcelReader.read(file.getInputStream(), DictionaryDTO.class);
        List<DictionaryVO> voList = dictionaryService.createAll(dtoList);

        return ResponseEntity.ok().body(voList);
    }

}
