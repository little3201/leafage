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

import io.leafage.hypervisor.service.AccessLogService;
import io.leafage.hypervisor.vo.AccessLogVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * access log controller
 *
 * @author wq li
 */
@Validated
@RestController
@RequestMapping("/access-logs")
public class AccessLogController {

    private final Logger logger = LoggerFactory.getLogger(AccessLogController.class);

    private final AccessLogService accessLogService;

    /**
     * <p>Constructor for AccessLogController.</p>
     *
     * @param accessLogService a {@link AccessLogService} object
     */
    public AccessLogController(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    /**
     * 查询
     *
     * @param page a int
     * @param size a int
     * @return 查询到数据集，异常时返回204
     */
    @GetMapping
    public Mono<Page<AccessLogVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                            String sortBy, boolean descending, String filters) {
        return accessLogService.retrieve(page, size, sortBy, descending, filters)
                .doOnError(e -> logger.error("Retrieve access logs error: ", e));
    }

    /**
     * 根据 id 查询
     *
     * @param id 主键 ID
     * @return 查询的数据
     */
    @GetMapping("/{id}")
    public Mono<AccessLogVO> fetch(@PathVariable Long id) {
        return accessLogService.fetch(id)
                .doOnError(e -> logger.error("Fetch access log error: ", e));
    }

}
