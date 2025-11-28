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

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.leafage.hypervisor.domain.vo.AccessLogVO;
import top.leafage.hypervisor.service.AccessLogService;

/**
 * access log controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/access-logs")
public class AccessLogController {

    private final AccessLogService accessLogService;

    /**
     * Constructor for AccessLogController.
     *
     * @param accessLogService a {@link AccessLogService} object
     */
    public AccessLogController(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
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
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_access_logs')")
    @GetMapping
    public ResponseEntity<Page<AccessLogVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                      String sortBy, boolean descending, String filters) {
        Page<AccessLogVO> voPage = accessLogService.retrieve(page, size, sortBy, descending, filters);
        return ResponseEntity.ok(voPage);
    }

    /**
     * fetch by id.
     *
     * @param id the pk.
     * @return 如果查询到数据，返回查询到的信息，否则返回204状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_access_logs')")
    @GetMapping("/{id}")
    public ResponseEntity<AccessLogVO> fetch(@PathVariable Long id) {
        AccessLogVO vo = accessLogService.fetch(id);
        return ResponseEntity.ok(vo);
    }

    /**
     * remove.
     *
     * @param id the pk.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_access_logs:remove')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        accessLogService.remove(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 清空信息
     *
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_access_logs:clear')")
    @DeleteMapping
    public ResponseEntity<Void> clear() {
        accessLogService.clear();
        return ResponseEntity.noContent().build();
    }
}
