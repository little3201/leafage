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

package io.leafage.hypervisor.controller;

import io.leafage.hypervisor.service.AuditLogService;
import io.leafage.hypervisor.vo.AuditLogVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * audit log controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/audit-logs")
public class AuditLogController {

    private final Logger logger = LoggerFactory.getLogger(AuditLogController.class);

    private final AuditLogService auditLogService;

    /**
     * <p>Constructor for AccessLogController.</p>
     *
     * @param auditLogService a {@link AuditLogService} object
     */
    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    /**
     * 查询
     *
     * @param page       页码
     * @param size       大小
     * @param sortBy     排序字段
     * @param descending 排序方向
     * @return 查询到数据集，异常时返回204
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_audit_logs')")
    @GetMapping
    public ResponseEntity<Page<AuditLogVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                     String sortBy, boolean descending, String url) {
        Page<AuditLogVO> voPage;
        try {
            voPage = auditLogService.retrieve(page, size, sortBy, descending, url);
        } catch (Exception e) {
            logger.error("Retrieve record error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voPage);
    }

    /**
     * 查询信息
     *
     * @param id 主键
     * @return 如果查询到数据，返回查询到的信息，否则返回204状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_audit_logs')")
    @GetMapping("/{id}")
    public ResponseEntity<AuditLogVO> fetch(@PathVariable Long id) {
        AuditLogVO vo;
        try {
            vo = auditLogService.fetch(id);
        } catch (Exception e) {
            logger.info("Fetch audit log error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(vo);
    }

    /**
     * 删除信息
     *
     * @param id 主键
     * @return 如果删除成功，返回200状态码，否则返回417状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_audit_logs:remove')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        try {
            auditLogService.remove(id);
        } catch (Exception e) {
            logger.error("Remove audit log error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().build();
    }

}
