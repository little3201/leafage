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
import top.leafage.common.r2dbc.audit.ReactiveAuditMetadata;

import java.util.Set;

/**
 * model class for privileges
 *
 * @author wq li
 */
@Table(name = "privileges")
public class Privilege extends ReactiveAuditMetadata {

    /**
     * 名称
     */
    @Column(value = "name")
    private String name;
    /**
     * 上级
     */
    @Column(value = "superior_id")
    private Long superiorId;
    /**
     * 图标
     */
    private String icon;
    /**
     * 路径
     */
    private String path;
    /**
     * redirect
     */
    private String redirect;
    /**
     * component
     */
    private String component;
    /**
     * actions
     */
    private Set<String> actions;
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
     * <p>Getter for the field <code>superiorId</code>.</p>
     *
     * @return a {@link java.lang.Long} object
     */
    public Long getSuperiorId() {
        return superiorId;
    }

    /**
     * <p>Setter for the field <code>superiorId</code>.</p>
     *
     * @param superiorId a {@link java.lang.Long} object
     */
    public void setSuperiorId(Long superiorId) {
        this.superiorId = superiorId;
    }

    /**
     * <p>Getter for the field <code>icon</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getIcon() {
        return icon;
    }

    /**
     * <p>Setter for the field <code>icon</code>.</p>
     *
     * @param icon a {@link java.lang.String} object
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * <p>Getter for the field <code>path</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getPath() {
        return path;
    }

    /**
     * <p>Setter for the field <code>path</code>.</p>
     *
     * @param path a {@link java.lang.String} object
     */
    public void setPath(String path) {
        this.path = path;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public Set<String> getActions() {
        return actions;
    }

    public void setActions(Set<String> actions) {
        this.actions = actions;
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
