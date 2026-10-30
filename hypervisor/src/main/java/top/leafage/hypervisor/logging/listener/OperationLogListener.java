/*
 *
 *  * Copyright(c) 2019-present the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *       https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package top.leafage.hypervisor.logging.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import top.leafage.common.logging.event.OperationLogEvent;
import top.leafage.hypervisor.system.domain.OperationLog;
import top.leafage.hypervisor.system.repository.OperationLogRepository;

@Component
public class OperationLogListener {

    private final OperationLogRepository operationLogRepository;

    public OperationLogListener(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    @Async
    @EventListener
    public void handle(OperationLogEvent event) {
        OperationLog entity = new OperationLog(
                event.getModule(),
                event.getAction(),
                event.getTargetId(),
                event.getParams(),
                event.getResponse(),
                event.getStatus(),
                event.getDuration(),
                event.getMessage()
        );
    }
}
