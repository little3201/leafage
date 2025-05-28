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

package io.leafage.assets.bo;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

/**
 * bo class for post.
 *
 * @author wq li
 */
public abstract class PostBO {

    /**
     * 标题
     */
    @NotBlank(message = "title must not be empty.")
    private String title;

    /**
     * 概述
     */
    private String excerpt;

    /**
     * 内容
     */
    @NotBlank(message = "content must not be empty.")
    private String content;

    /**
     * 标签
     */
    @NotEmpty(message = "tags must not be empty.")
    private Set<String> tags;


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

}
