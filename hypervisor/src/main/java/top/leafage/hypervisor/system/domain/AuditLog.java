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

package top.leafage.hypervisor.system.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import top.leafage.common.data.jpa.domain.JpaAbstractAuditable;

import java.net.InetAddress;

/**
 * entity class for audit log.
 *
 * @author wq li
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "audit_logs")
public class AuditLog extends JpaAbstractAuditable<@NonNull String, @NonNull Long> {

    private String resource;

    private String action;

    private Long targetId;

    @Column(columnDefinition = "text")
    private String oldValue;

    @Column(columnDefinition = "text")
    private String newValue;

    @Column(columnDefinition = "inet")
    private InetAddress ip;

    private Integer statusCode;

    private Long duration;


    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String operation) {
        this.action = operation;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long objPk) {
        this.targetId = objPk;
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

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long operatedTimes) {
        this.duration = operatedTimes;
    }
}
