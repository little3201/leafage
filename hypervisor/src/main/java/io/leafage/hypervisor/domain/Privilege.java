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

import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import top.leafage.common.servlet.audit.AuditMetadata;

import java.util.Set;

/**
 * model class for privilege.
 *
 * @author wq li
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "privileges")
public class Privilege extends AuditMetadata {


    @Column(name = "name", nullable = false, unique = true)
    private String name;

    /**
     * 上级主键
     */
    @Column(name = "superior_id")
    private Long superiorId;

    private String path;

    /**
     * 跳转路径
     */
    private String redirect;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 操作
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "privilege_actions", joinColumns = @JoinColumn(name = "privilege_id"))
    @Column(name = "action")
    private Set<String> actions;

    /**
     * 描述
     */
    private String description;


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
     * <p>Getter for the field <code>path</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getPath() {
        return path;
    }

    /**
     * <p>Setter for the field <code>path</code>.</p>
     *
     * @param path a {@link String} object
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * <p>Getter for the field <code>redirect</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getRedirect() {
        return redirect;
    }

    /**
     * <p>Setter for the field <code>redirect</code>.</p>
     *
     * @param redirect a {@link String} object
     */
    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    /**
     * <p>Getter for the field <code>component</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getComponent() {
        return component;
    }

    /**
     * <p>Setter for the field <code>component</code>.</p>
     *
     * @param component a {@link String} object
     */
    public void setComponent(String component) {
        this.component = component;
    }

    /**
     * <p>Getter for the field <code>icon</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getIcon() {
        return icon;
    }

    /**
     * <p>Setter for the field <code>icon</code>.</p>
     *
     * @param icon a {@link String} object
     */
    public void setIcon(String icon) {
        this.icon = icon;
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
