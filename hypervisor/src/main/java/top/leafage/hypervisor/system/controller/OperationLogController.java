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

package top.leafage.hypervisor.system.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.leafage.hypervisor.system.domain.vo.OperationLogVO;
import top.leafage.hypervisor.system.service.OperationLogService;

/**
 * operation log controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/operation-logs")
public class OperationLogController {

    private final OperationLogService operationLogService;

    /**
     * Constructor for OperationLogController.
     *
     * @param operationLogService a {@link OperationLogService} object
     */
    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
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
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_operation_logs')")
    @GetMapping
    public ResponseEntity<Page<OperationLogVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                         String sortBy, boolean descending, String filters) {
        Page<OperationLogVO> voPage = operationLogService.retrieve(page, size, sortBy, descending, filters);
        return ResponseEntity.ok(voPage);
    }

    /**
     * fetch by id.
     *
     * @param id the pk.
     * @return 如果查询到数据，返回查询到的信息，否则返回204状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_operation_logs')")
    @GetMapping("/{id}")
    public ResponseEntity<OperationLogVO> fetch(@PathVariable Long id) {
        OperationLogVO vo = operationLogService.fetch(id);
        return ResponseEntity.ok(vo);
    }

    /**
     * remove.
     *
     * @param id the pk.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_operation_logs:remove')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        operationLogService.remove(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 清空信息
     *
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_operation_logs:clear')")
    @DeleteMapping
    public ResponseEntity<Void> clear() {
        operationLogService.clear();
        return ResponseEntity.noContent().build();
    }
}
