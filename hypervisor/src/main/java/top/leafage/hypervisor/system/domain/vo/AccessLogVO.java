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

package top.leafage.hypervisor.system.domain.vo;

import top.leafage.hypervisor.system.domain.AccessLog;

/**
 * vo class for access log.
 *
 * @author wq li
 */
public record AccessLogVO(
        Long id,
        String url,
        String httpMethod,
        String ip,
        String params,
        String body,
        Integer statusCode,
        Long responseTimes,
        String responseMessage
) {
    public static AccessLogVO from(AccessLog entity) {
        return new AccessLogVO(
                entity.getId(),
                entity.getUrl(),
                entity.getHttpMethod(),
                entity.getIp() == null ? null : entity.getIp().getHostAddress(),
                entity.getParams(),
                entity.getBody(),
                entity.getStatusCode(),
                entity.getResponseTimes(),
                entity.getResponseMessage()
        );
    }
}
