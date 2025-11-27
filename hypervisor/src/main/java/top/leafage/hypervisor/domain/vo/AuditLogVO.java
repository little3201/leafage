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

import top.leafage.hypervisor.domain.AuditLog;

import java.net.InetAddress;

/**
 * vo class for audit log.
 *
 * @author wq li
 */
public record AuditLogVO(
        Long id,
        String operation,
        String resource,
        String oldValue,
        String newValue,
        InetAddress ip,
        Integer statusCode,
        Long operatedTimes
) {
    public static AuditLogVO from(AuditLog entity) {
        return new AuditLogVO(
                entity.getId(),
                entity.getOperation(),
                entity.getResource(),
                entity.getOldValue(),
                entity.getNewValue(),
                entity.getIp(),
                entity.getStatusCode(),
                entity.getOperatedTimes()
        );
    }
}
