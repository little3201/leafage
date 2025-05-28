/*
 *  Copyright 2018-2025 little3201.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.leafage.hypervisor.domain;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import top.leafage.common.reactive.audit.ReactiveAuditMetadata;

/**
 * model class for access log
 *
 * @author wq li
 */
@Table(name = "access_logs")
public class AccessLog extends ReactiveAuditMetadata {

    /**
     * IP地址
     */
    private String ip;

    /**
     * 地理位置
     */
    private String location;

    /**
     * 用户代理信息
     */
    @Column(value = "user_agent")
    private String userAgent;

    /**
     * HTTP方法
     */
    @Column(value = "http_method")
    private String httpMethod;

    /**
     * 请求URL
     */
    private String url;

    /**
     * HTTP状态码
     */
    @Column(value = "status_code")
    private Integer statusCode;

    /**
     * 响应时间
     */
    @Column(value = "response_time")
    private Long responseTime;

    /**
     * 来源页面
     */
    private String referer;

    /**
     * 会话标识符
     */
    @Column(value = "session_id")
    private String sessionId;

    /**
     * 设备类型
     */
    @Column(value = "device_type")
    private String deviceType;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 浏览器
     */
    private String browser;


    /**
     * <p>Getter for the field <code>ip</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getIp() {
        return ip;
    }

    /**
     * <p>Setter for the field <code>ip</code>.</p>
     *
     * @param ip a {@link java.lang.String} object
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * <p>Getter for the field <code>location</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getLocation() {
        return location;
    }

    /**
     * <p>Setter for the field <code>location</code>.</p>
     *
     * @param location a {@link java.lang.String} object
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * <p>Getter for the field <code>userAgent</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * <p>Setter for the field <code>userAgent</code>.</p>
     *
     * @param userAgent a {@link java.lang.String} object
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * <p>Getter for the field <code>httpMethod</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getHttpMethod() {
        return httpMethod;
    }

    /**
     * <p>Setter for the field <code>httpMethod</code>.</p>
     *
     * @param httpMethod a {@link java.lang.String} object
     */
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    /**
     * <p>Getter for the field <code>url</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getUrl() {
        return url;
    }

    /**
     * <p>Setter for the field <code>url</code>.</p>
     *
     * @param url a {@link java.lang.String} object
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * <p>Getter for the field <code>statusCode</code>.</p>
     *
     * @return a {@link java.lang.Integer} object
     */
    public Integer getStatusCode() {
        return statusCode;
    }

    /**
     * <p>Setter for the field <code>statusCode</code>.</p>
     *
     * @param statusCode a {@link java.lang.Integer} object
     */
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * <p>Getter for the field <code>responseTime</code>.</p>
     *
     * @return a {@link java.lang.Long} object
     */
    public Long getResponseTime() {
        return responseTime;
    }

    /**
     * <p>Setter for the field <code>responseTime</code>.</p>
     *
     * @param responseTime a {@link java.lang.Long} object
     */
    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }

    /**
     * <p>Getter for the field <code>referer</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getReferer() {
        return referer;
    }

    /**
     * <p>Setter for the field <code>referer</code>.</p>
     *
     * @param referer a {@link java.lang.String} object
     */
    public void setReferer(String referer) {
        this.referer = referer;
    }

    /**
     * <p>Getter for the field <code>sessionId</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * <p>Setter for the field <code>sessionId</code>.</p>
     *
     * @param sessionId a {@link java.lang.String} object
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * <p>Getter for the field <code>deviceType</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * <p>Setter for the field <code>deviceType</code>.</p>
     *
     * @param deviceType a {@link java.lang.String} object
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * <p>Getter for the field <code>os</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getOs() {
        return os;
    }

    /**
     * <p>Setter for the field <code>os</code>.</p>
     *
     * @param os a {@link java.lang.String} object
     */
    public void setOs(String os) {
        this.os = os;
    }

    /**
     * <p>Getter for the field <code>browser</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getBrowser() {
        return browser;
    }

    /**
     * <p>Setter for the field <code>browser</code>.</p>
     *
     * @param browser a {@link java.lang.String} object
     */
    public void setBrowser(String browser) {
        this.browser = browser;
    }
}
