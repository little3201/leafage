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

package top.leafage.hypervisor.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.domain.vo.OperationLogVO;
import top.leafage.hypervisor.service.OperationLogService;

/**
 * operation log controller
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
     * 查询
     *
     * @param page a int
     * @param size a int
     * @return 查询到数据集，异常时返回204
     */
    @GetMapping
    public Mono<Page<OperationLogVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                               String sortBy, boolean descending, String filters) {
        return operationLogService.retrieve(page, size, sortBy, descending, filters);
    }

    /**
     * 根据 id 查询
     *
     * @param id the pk. ID
     * @return 查询的数据
     */
    @GetMapping("/{id}")
    public Mono<OperationLogVO> fetch(@PathVariable Long id) {
        return operationLogService.fetch(id);
    }

    /**
     * 删除信息
     *
     * @param id user the pk.
     * @return 200状态码
     */
    @DeleteMapping("/{id}")
    public Mono<Void> remove(@PathVariable Long id) {
        return operationLogService.remove(id);
    }

    /**
     * 清空信息
     *
     * @return 200状态码
     */
    @DeleteMapping("/clear")
    public Mono<Void> clear() {
        return operationLogService.clear();
    }
}
