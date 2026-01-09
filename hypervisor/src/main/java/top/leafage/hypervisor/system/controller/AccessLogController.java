/*
 * Copyright (c) 2026.  little3201.
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
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.system.domain.vo.AccessLogVO;
import top.leafage.hypervisor.system.service.AccessLogService;

/**
 * access log controller
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
     * 查询
     *
     * @param page a int
     * @param size a int
     * @return 查询到数据集，异常时返回204
     */
    @GetMapping
    public Mono<Page<AccessLogVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                            String sortBy, boolean descending, String filters) {
        return accessLogService.retrieve(page, size, sortBy, descending, filters);
    }

    /**
     * 根据 id 查询
     *
     * @param id the pk. ID
     * @return 查询的数据
     */
    @GetMapping("/{id}")
    public Mono<AccessLogVO> fetch(@PathVariable Long id) {
        return accessLogService.fetch(id);
    }

    /**
     * 删除信息
     *
     * @param id user the pk.
     * @return 200状态码
     */
    @DeleteMapping("/{id}")
    public Mono<Void> remove(@PathVariable Long id) {
        return accessLogService.remove(id);
    }

    /**
     * 清空信息
     *
     * @return 200状态码
     */
    @DeleteMapping("/clear")
    public Mono<Void> clear() {
        return accessLogService.clear();
    }
}
