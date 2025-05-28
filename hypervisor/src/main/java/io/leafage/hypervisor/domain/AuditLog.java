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

package io.leafage.hypervisor.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import top.leafage.common.servlet.audit.AuditMetadata;

import java.net.InetAddress;

/**
 * model class for audit log.
 *
 * @author wq li
 */
@Entity
@Table(name = "audit_logs")
public class AuditLog extends AuditMetadata {

    private String operator;

    private String operation;

    private String resource;

    @Column(name = "old_value")
    private String oldValue;

    @Column(name = "new_value")
    private String newValue;

    private InetAddress ip;

    @Column(name = "location", length = 50)
    private String location;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "operated_times")
    private Long operatedTimes;


    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

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

    /**
     * <p>Getter for the field <code>ip</code>.</p>
     *
     * @return a {@link String} object
     */
    public InetAddress getIp() {
        return ip;
    }

    /**
     * <p>Setter for the field <code>ip</code>.</p>
     *
     * @param ip a {@link String} object
     */
    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    /**
     * <p>Getter for the field <code>location</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getLocation() {
        return location;
    }

    /**
     * <p>Setter for the field <code>location</code>.</p>
     *
     * @param location a {@link String} object
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * <p>Getter for the field <code>statusCode</code>.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getStatusCode() {
        return statusCode;
    }

    /**
     * <p>Setter for the field <code>statusCode</code>.</p>
     *
     * @param statusCode a {@link Integer} object
     */
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * <p>Getter for the field <code>operatedTime</code>.</p>
     *
     * @return a {@link Long object
     */
    public Long getOperatedTimes() {
        return operatedTimes;
    }

    /**
     * <p>Setter for the field <code>operatedTime</code>.</p>
     *
     * @param operatedTime a {@link Long} object
     */
    public void setOperatedTimes(Long operatedTime) {
        this.operatedTimes = operatedTime;
    }

}
