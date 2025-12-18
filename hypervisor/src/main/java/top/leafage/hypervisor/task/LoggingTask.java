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

package top.leafage.hypervisor.task;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.leafage.hypervisor.system.service.OperationLogService;
import top.leafage.hypervisor.system.service.SchedulerLogService;

@Component
public class LoggingTask {

    private final OperationLogService operationLogService;
    private final SchedulerLogService schedulerLogService;

    public LoggingTask(OperationLogService operationLogService, SchedulerLogService schedulerLogService) {
        this.operationLogService = operationLogService;
        this.schedulerLogService = schedulerLogService;
    }

    @Scheduled(initialDelay = 10000, fixedDelay = 1000 * 60 * 60 * 24)
    public void clear() {
        operationLogService.clear();
        schedulerLogService.clear();
    }
}
