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

package io.leafage.assets.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import top.leafage.common.servlet.audit.AuditMetadata;

/**
 * model class for region.
 *
 * @author wq li
 */
@Entity
@Table(name = "regions")
public class Region extends AuditMetadata {


    @Column(name = "name", nullable = false, length = 50)
    private String name;

    /**
     * 上级
     */
    @Column(name = "superior_id")
    private Long superiorId;

    /**
     * 区号
     */
    @Column(name = "area_code", length = 4)
    private String areaCode;

    /**
     * 邮编
     */
    @Column(name = "postal_code", length = 6)
    private Integer postalCode;

    private boolean enabled = true;

    /**
     * 描述
     */
    private String description;


    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link String} object
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>superiorId</code>.</p>
     *
     * @return a {@link Long} object
     */
    public Long getSuperiorId() {
        return superiorId;
    }

    /**
     * <p>Setter for the field <code>superiorId</code>.</p>
     *
     * @param superiorId a {@link Long} object
     */
    public void setSuperiorId(Long superiorId) {
        this.superiorId = superiorId;
    }

    /**
     * <p>Getter for the field <code>areaCode</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getAreaCode() {
        return areaCode;
    }

    /**
     * <p>Setter for the field <code>areaCode</code>.</p>
     *
     * @param areaCode a {@link String} object
     */
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    /**
     * <p>Getter for the field <code>postalCode</code>.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getPostalCode() {
        return postalCode;
    }

    /**
     * <p>Setter for the field <code>postalCode</code>.</p>
     *
     * @param postalCode a {@link Integer} object
     */
    public void setPostalCode(Integer postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * <p>isEnabled.</p>
     *
     * @return a boolean
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * <p>Setter for the field <code>enabled</code>.</p>
     *
     * @param enabled a boolean
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * <p>Getter for the field <code>description</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>Setter for the field <code>description</code>.</p>
     *
     * @param description a {@link String} object
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
