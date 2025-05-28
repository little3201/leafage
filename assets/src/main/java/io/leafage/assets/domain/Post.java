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
package io.leafage.assets.domain;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import top.leafage.common.reactive.audit.ReactiveAuditMetadata;

import java.util.Set;

/**
 * model class for post
 *
 * @author wq li
 */
@Table(name = "posts")
public class Post extends ReactiveAuditMetadata {

    /**
     * category 主键
     */
    @Column(value = "category_id")
    private Long categoryId;

    /**
     * 标题
     */
    private String title;

    /**
     * 封面
     */
    private String cover;

    /**
     * 标签
     */
    private Set<String> tags;

    /**
     * 是否启用
     */
    @Column(value = "is_enabled")
    private boolean enabled = true;

    /**
     * <p>Getter for the field <code>categoryId</code>.</p>
     *
     * @return a {@link java.lang.Long} object
     */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * <p>Setter for the field <code>categoryId</code>.</p>
     *
     * @param categoryId a {@link java.lang.Long} object
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * <p>Getter for the field <code>title</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getTitle() {
        return title;
    }

    /**
     * <p>Setter for the field <code>title</code>.</p>
     *
     * @param title a {@link java.lang.String} object
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * <p>Getter for the field <code>cover</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getCover() {
        return cover;
    }

    /**
     * <p>Setter for the field <code>cover</code>.</p>
     *
     * @param cover a {@link java.lang.String} object
     */
    public void setCover(String cover) {
        this.cover = cover;
    }

    /**
     * <p>Getter for the field <code>tags</code>.</p>
     *
     * @return a {@link java.util.Set} object
     */
    public Set<String> getTags() {
        return tags;
    }

    /**
     * <p>Setter for the field <code>tags</code>.</p>
     *
     * @param tags a {@link java.util.Set} object
     */
    public void setTags(Set<String> tags) {
        this.tags = tags;
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
