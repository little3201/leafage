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

package io.leafage.hypervisor.bo;

import java.net.InetAddress;

/**
 * bo class for operation log
 *
 * @author wq li
 */
public abstract class OperationLogBO {

    /**
     * IP地址
     */
    private InetAddress ip;

    /**
     * 地理位置
     */
    private String location;

    /**
     * 用户代理信息
     */
    private String userAgent;

    private String content;

    private String operation;

    /**
     * HTTP状态码
     */
    private Integer statusCode;

    /**
     * 响应时间
     */
    private Long operatedTimes;

    /**
     * 来源页面
     */
    private String referer;

    /**
     * 会话标识符
     */
    private String sessionId;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 浏览器
     */
    private String browser;


    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
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

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }
}
