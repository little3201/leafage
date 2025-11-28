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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.leafage.common.data.domain.TreeNode;
import top.leafage.common.poi.ExcelReader;
import top.leafage.hypervisor.domain.dto.PrivilegeDTO;
import top.leafage.hypervisor.domain.vo.PrivilegeVO;
import top.leafage.hypervisor.service.PrivilegeService;

import java.io.IOException;
import java.security.Principal;
import java.util.List;


/**
 * privilege controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/privileges")
public class PrivilegeController {

    private final PrivilegeService privilegeService;

    /**
     * Constructor for PrivilegeController.
     *
     * @param privilegeService a {@link PrivilegeService} object
     */
    public PrivilegeController(PrivilegeService privilegeService) {
        this.privilegeService = privilegeService;
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
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_privileges')")
    @GetMapping
    public ResponseEntity<Page<PrivilegeVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                      String sortBy, boolean descending, String filters) {
        Page<PrivilegeVO> voPage = privilegeService.retrieve(page, size, sortBy, descending, filters);
        return ResponseEntity.ok(voPage);
    }

    /**
     * 查询树形数据
     *
     * @return th result.
     */
    @GetMapping("/tree")
    public ResponseEntity<List<TreeNode<Long>>> tree(Principal principal) {
        List<TreeNode<Long>> treeNodes = privilegeService.tree(principal.getName());
        return ResponseEntity.ok(treeNodes);
    }

    /**
     * fetch by id.
     *
     * @param superiorId the pk.
     * @return 查询到的信息，否则返回204状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_privileges')")
    @GetMapping("/{superiorId}/subset")
    public ResponseEntity<List<PrivilegeVO>> subset(@PathVariable Long superiorId) {
        List<PrivilegeVO> voList = privilegeService.subset(superiorId);
        return ResponseEntity.ok(voList);
    }

    /**
     * fetch by id.
     *
     * @param id the pk.
     * @return 查询到的信息，否则返回204状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_privileges')")
    @GetMapping("/{id}")
    public ResponseEntity<PrivilegeVO> fetch(@PathVariable Long id) {
        PrivilegeVO vo = privilegeService.fetch(id);
        return ResponseEntity.ok(vo);
    }

    /**
     * modify.
     *
     * @param id  the pk.
     * @param dto the request body.
     * @return 编辑后的信息，否则返回417状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_privileges:modify')")
    @PutMapping("/{id}")
    public ResponseEntity<PrivilegeVO> modify(@PathVariable Long id, @Valid @RequestBody PrivilegeDTO dto) {
        PrivilegeVO vo = privilegeService.modify(id, dto);
        return ResponseEntity.accepted().body(vo);
    }

    /**
     * 启用、停用
     *
     * @param id the pk.
     * @return 编辑后的信息，否则返回417状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_privileges:enable')")
    @PatchMapping("/{id}")
    public ResponseEntity<Boolean> enable(@PathVariable Long id) {
        boolean enabled = privilegeService.enable(id);
        return ResponseEntity.ok(enabled);
    }

    /**
     * import..
     *
     * @return the result.
     */
    @PreAuthorize("hasAuthority('SCOPE_privileges:import')")
    @PostMapping("/import")
    public ResponseEntity<List<PrivilegeVO>> importFromFile(MultipartFile file) throws IOException {
        List<PrivilegeDTO> dtoList = ExcelReader.read(file.getInputStream(), PrivilegeDTO.class);
        List<PrivilegeVO> voList = privilegeService.createAll(dtoList);

        return ResponseEntity.ok().body(voList);
    }

}
