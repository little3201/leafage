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

package io.leafage.hypervisor.bo;

/**
 * bo class for access log
 *
 * @author wq li
 */
public abstract class AccessLogBO {

    /**
     * ip
     */
    private String ip;

    /**
     * 位置
     */
    private String location;

    /**
     * 描述
     */
    private String description;


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
     * <p>Getter for the field <code>description</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>Setter for the field <code>description</code>.</p>
     *
     * @param description a {@link java.lang.String} object
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
