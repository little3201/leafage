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

package io.leafage.assets.bo;

import jakarta.validation.constraints.NotBlank;

/**
 * bo class for region
 *
 * @author wq li
 */
public abstract class RegionBO {

    /**
     * 名称
     */
    @NotBlank
    private String name;

    /**
     * 邮编
     */
    private Integer postalCode;

    /**
     * 区号
     */
    private String areaCode;

    /**
     * 描述
     */
    private String description;


    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>postalCode</code>.</p>
     *
     * @return a {@link java.lang.Integer} object
     */
    public Integer getPostalCode() {
        return postalCode;
    }

    /**
     * <p>Setter for the field <code>postalCode</code>.</p>
     *
     * @param postalCode a {@link java.lang.Integer} object
     */
    public void setPostalCode(Integer postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * <p>Getter for the field <code>areaCode</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getAreaCode() {
        return areaCode;
    }

    /**
     * <p>Setter for the field <code>areaCode</code>.</p>
     *
     * @param areaCode a {@link java.lang.String} object
     */
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
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
