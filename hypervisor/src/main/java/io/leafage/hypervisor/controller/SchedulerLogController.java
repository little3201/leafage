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

import io.leafage.hypervisor.service.SchedulerLogService;
import io.leafage.hypervisor.vo.SchedulerLogVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * scheduler log controller
 *
 * @author wq li
 */
@Validated
@RestController
@RequestMapping("/scheduler-logs")
public class SchedulerLogController {

    private final Logger logger = LoggerFactory.getLogger(SchedulerLogController.class);

    private final SchedulerLogService schedulerLogService;

    /**
     * <p>Constructor for SchedulerLogController.</p>
     *
     * @param schedulerLogService a {@link SchedulerLogService} object
     */
    public SchedulerLogController(SchedulerLogService schedulerLogService) {
        this.schedulerLogService = schedulerLogService;
    }

    /**
     * 查询
     *
     * @param page a int
     * @param size a int
     * @return 查询到数据集，异常时返回204
     */
    @GetMapping
    public ResponseEntity<Mono<Page<SchedulerLogVO>>> retrieve(@RequestParam int page, @RequestParam int size,
                                                               String sortBy, boolean descending, String filters) {
        Mono<Page<SchedulerLogVO>> pageMono;
        try {
            pageMono = schedulerLogService.retrieve(page, size, sortBy, descending, filters);
        } catch (Exception e) {
            logger.error("Retrieve scheduler logs occurred an error: ", e);
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
    public ResponseEntity<Mono<SchedulerLogVO>> fetch(@PathVariable Long id) {
        Mono<SchedulerLogVO> voMono;
        try {
            voMono = schedulerLogService.fetch(id);
        } catch (Exception e) {
            logger.error("Fetch scheduler log occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voMono);
    }

}
