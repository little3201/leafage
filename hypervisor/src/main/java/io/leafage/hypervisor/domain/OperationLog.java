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
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import top.leafage.common.servlet.audit.AuditMetadata;

import java.net.InetAddress;

/**
 * model class for operation log.
 *
 * @author wq li
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "operation_logs")
public class OperationLog extends AuditMetadata {

    private String operation;

    private InetAddress ip;

    @Column(name = "location", length = 50)
    private String location;

    @Column(name = "content", length = 1000)
    private String content;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "operated_times")
    private Long operatedTimes;

    private String referer;

    @Column(name = "session_id", length = 50)
    private String sessionId;

    @Column(name = "device_type", length = 20)
    private String deviceType;

    @Column(name = "os", length = 50)
    private String os;

    @Column(name = "browser", length = 50)
    private String browser;


    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
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
     * <p>Getter for the field <code>content</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getContent() {
        return content;
    }

    /**
     * <p>Setter for the field <code>content</code>.</p>
     *
     * @param content a {@link String} object
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * <p>Getter for the field <code>userAgent</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * <p>Setter for the field <code>userAgent</code>.</p>
     *
     * @param userAgent a {@link String} object
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
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

    /**
     * <p>Getter for the field <code>referer</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getReferer() {
        return referer;
    }

    /**
     * <p>Setter for the field <code>referer</code>.</p>
     *
     * @param referer a {@link String} object
     */
    public void setReferer(String referer) {
        this.referer = referer;
    }

    /**
     * <p>Getter for the field <code>sessionId</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * <p>Setter for the field <code>sessionId</code>.</p>
     *
     * @param sessionId a {@link String} object
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * <p>Getter for the field <code>deviceType</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * <p>Setter for the field <code>deviceType</code>.</p>
     *
     * @param deviceType a {@link String} object
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * <p>Getter for the field <code>os</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getOs() {
        return os;
    }

    /**
     * <p>Setter for the field <code>os</code>.</p>
     *
     * @param os a {@link String} object
     */
    public void setOs(String os) {
        this.os = os;
    }

    /**
     * <p>Getter for the field <code>browser</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getBrowser() {
        return browser;
    }

    /**
     * <p>Setter for the field <code>browser</code>.</p>
     *
     * @param browser a {@link String} object
     */
    public void setBrowser(String browser) {
        this.browser = browser;
    }
}
