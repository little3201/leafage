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

package top.leafage.hypervisor.system.domain.vo;

import top.leafage.common.logging.event.OperationLogEvent;
import top.leafage.hypervisor.system.domain.OperationLog;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * vo class for operation log.
 *
 * @author wq li
 */
public record OperationLogVO(
        Long id,
        String module,
        String action,
        Map<String, Object> params,
        String response,
        OperationLogEvent.Status status,
        long duration,
        String message,
        String operator,
        LocalDateTime operatedAt
) {
    public static OperationLogVO from(OperationLog entity) {
        return new OperationLogVO(
                entity.getId(),
                entity.getModule(),
                entity.getAction(),
                entity.getParams(),
                entity.getResponse(),
                entity.getStatus(),
                entity.getDuration(),
                entity.getMessage(),
                entity.getCreatedBy().orElse(null),
                entity.getCreatedDate().orElse(null)
        );
    }
}
