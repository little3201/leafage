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

package top.leafage.hypervisor.system.domain;


import org.jspecify.annotations.NonNull;
import org.springframework.data.relational.core.mapping.Table;
import top.leafage.common.data.domain.AbstractAuditable;

import java.net.InetAddress;

/**
 * entity class for audit log.
 *
 * @author wq li
 */
@Table(name = "audit_logs")
public class AuditLog extends AbstractAuditable<@NonNull String, @NonNull Long> {

    private String operation;

    private String resource;

    private String oldValue;

    private String newValue;

    private InetAddress ip;

    private Integer statusCode;

    private Long operatedTimes;


    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Long getOperatedTimes() {
        return operatedTimes;
    }

    public void setOperatedTimes(Long operatedTimes) {
        this.operatedTimes = operatedTimes;
    }
}
