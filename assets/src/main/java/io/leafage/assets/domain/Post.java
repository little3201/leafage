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
 * model class for posts.
 *
 * @author wq li
 */
@Entity
@Table(name = "posts")
public class Post extends AuditMetadata {

    /**
     * 标题
     */
    @Column(name = "title", nullable = false, unique = true)
    private String title;

    /**
     * 概述
     */
    private String excerpt;


    private boolean enabled = true;


    /**
     * <p>Getter for the field <code>title</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getTitle() {
        return title;
    }

    /**
     * <p>Setter for the field <code>title</code>.</p>
     *
     * @param title a {@link String} object
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * <p>Getter for the field <code>excerpt</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getExcerpt() {
        return excerpt;
    }

    /**
     * <p>Setter for the field <code>excerpt</code>.</p>
     *
     * @param excerpt a {@link String} object
     */
    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
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
}
