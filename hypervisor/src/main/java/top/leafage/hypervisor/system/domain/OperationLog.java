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

package top.leafage.hypervisor.system.domain;

import org.springframework.data.relational.core.mapping.Table;
import top.leafage.common.data.jdbc.domain.AbstractAuditable;
import top.leafage.common.logging.event.OperationLogEvent;

import java.net.InetAddress;
import java.util.Map;

/**
 * entity class for operation log.
 *
 * @author wq li
 */
@Table(name = "operation_logs")
public class OperationLog extends AbstractAuditable<String, Long> {

    private String module;

    private String action;

    private Long targetId;

    private Map<String, Object> params;

    private String response;

    private OperationLogEvent.Status status;

    private long duration;

    private String message;

    public OperationLog() {
    }

    public OperationLog(String module, String action, Long targetId, Map<String, Object> params, String response, OperationLogEvent.Status status, long duration, String message) {
        this.module = module;
        this.action = action;
        this.targetId = targetId;
        this.params = params;
        this.response = response;
        this.status = status;
        this.duration = duration;
        this.message = message;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public OperationLogEvent.Status getStatus() {
        return status;
    }

    public void setStatus(OperationLogEvent.Status status) {
        this.status = status;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
