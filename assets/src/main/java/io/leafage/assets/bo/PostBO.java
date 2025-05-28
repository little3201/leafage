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
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

/**
 * bo class for post
 *
 * @author wq li
 */
public abstract class PostBO {

    /**
     * 标题
     */
    @NotBlank
    private String title;

    /**
     * 封面
     */
    private String cover;

    /**
     * 内容
     */
    @NotBlank
    private String context;

    /**
     * 标签
     */
    @NotEmpty
    private Set<String> tags;


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
     * <p>Getter for the field <code>context</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getContext() {
        return context;
    }

    /**
     * <p>Setter for the field <code>context</code>.</p>
     *
     * @param context a {@link java.lang.String} object
     */
    public void setContext(String context) {
        this.context = context;
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
