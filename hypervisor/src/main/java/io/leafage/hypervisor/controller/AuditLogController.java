/*
 *  Copyright 2018-2025 little3201.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.leafage.hypervisor.controller;

import io.leafage.hypervisor.service.AuditLogService;
import io.leafage.hypervisor.vo.AuditLogVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * audit log controller
 *
 * @author wq li
 */
@Validated
@RestController
@RequestMapping("/audit-logs")
public class AuditLogController {

    private final Logger logger = LoggerFactory.getLogger(AuditLogController.class);

    private final AuditLogService auditLogService;

    /**
     * <p>Constructor for AuditLogController.</p>
     *
     * @param auditLogService a {@link AuditLogService} object
     */
    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    /**
     * 查询
     *
     * @param page a int
     * @param size a int
     * @return 查询到数据集，异常时返回204
     */
    @GetMapping
    public ResponseEntity<Mono<Page<AuditLogVO>>> retrieve(@RequestParam int page, @RequestParam int size,
                                                           String sortBy, boolean descending, String filters) {
        Mono<Page<AuditLogVO>> pageMono;
        try {
            pageMono = auditLogService.retrieve(page, size, sortBy, descending, filters);
        } catch (Exception e) {
            logger.error("Retrieve audit logs occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pageMono);
    }

    /**
     * 根据 id 查询
     *
     * @param id 主键 ID
     * @return 查询的数据，异常时返回204状态码
     */
    @GetMapping("/{id}")
    public ResponseEntity<Mono<AuditLogVO>> fetch(@PathVariable Long id) {
        Mono<AuditLogVO> voMono;
        try {
            voMono = auditLogService.fetch(id);
        } catch (Exception e) {
            logger.error("Fetch audit log occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voMono);
    }

}
