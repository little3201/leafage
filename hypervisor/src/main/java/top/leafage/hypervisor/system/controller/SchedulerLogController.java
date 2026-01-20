/*
 * Copyright (c) 2025.  little3201.
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
import top.leafage.hypervisor.system.domain.vo.SchedulerLogVO;
import top.leafage.hypervisor.system.service.SchedulerLogService;

/**
 * controller for scheduler_logs.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/scheduler-logs")
public class SchedulerLogController {

    private final SchedulerLogService schedulerLogService;

    public SchedulerLogController(SchedulerLogService schedulerLogService) {
        this.schedulerLogService = schedulerLogService;
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
    @PreAuthorize("hasAuthority('SCOPE_scheduler_logs')")
    @GetMapping
    public ResponseEntity<Page<SchedulerLogVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                         String sortBy, boolean descending, String filters) {
        Page<SchedulerLogVO> voPage = schedulerLogService.retrieve(page, size, sortBy, descending, filters);
        return ResponseEntity.ok(voPage);
    }

    /**
     * Fetches a record by ID.
     *
     * @param id the pk.
     * @return The record data, or 204 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_scheduler_logs')")
    @GetMapping("/{id}")
    public ResponseEntity<SchedulerLogVO> fetch(@PathVariable Long id) {
        SchedulerLogVO vo = schedulerLogService.fetch(id);
        return ResponseEntity.ok(vo);
    }

    /**
     * Removes a record by ID.
     *
     * @param id the pk.
     * @return the result.
     */
    @PreAuthorize("hasAuthority('SCOPE_scheduler_logs:remove')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        schedulerLogService.remove(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 清空信息
     *
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_operation_logs:clear')")
    @DeleteMapping
    public ResponseEntity<Void> clear() {
        schedulerLogService.clear();
        return ResponseEntity.noContent().build();
    }

}
