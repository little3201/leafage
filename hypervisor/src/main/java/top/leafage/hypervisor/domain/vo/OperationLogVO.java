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

package top.leafage.hypervisor.domain.vo;

import top.leafage.hypervisor.domain.OperationLog;

/**
 * vo class for operation log.
 *
 * @author wq li
 */
public record OperationLogVO(
        Long id,
        String module,
        String action,
        String params,
        String body,
        String ip,
        String sessionId,
        String userAgent,
        int statusCode
) {
    public static OperationLogVO from(OperationLog entity) {
        return new OperationLogVO(
                entity.getId(),
                entity.getModule(),
                entity.getAction(),
                entity.getParams(),
                entity.getBody(),
                entity.getIp() == null ? null : entity.getIp().getHostAddress(),
                entity.getSessionId(),
                entity.getUserAgent(),
                entity.getStatusCode()
        );
    }
}