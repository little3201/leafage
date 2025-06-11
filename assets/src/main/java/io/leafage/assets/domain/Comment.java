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
import top.leafage.common.r2dbc.audit.ReactiveAuditMetadata;


/**
 * model class for comment
 *
 * @author wq li
 */
@Table(name = "comments")
public class Comment extends ReactiveAuditMetadata {

    /**
     * 帖子ID
     */
    @Column(value = "post_id")
    private Long postId;
    /**
     * 内容
     */
    private String context;
    /**
     * 国家
     */
    private String country;
    /**
     * 位置
     */
    private String location;
    /**
     * 回复者
     */
    private Long replier;


    /**
     * <p>Getter for the field <code>postId</code>.</p>
     *
     * @return a {@link java.lang.Long} object
     */
    public Long getPostId() {
        return postId;
    }

    /**
     * <p>Setter for the field <code>postId</code>.</p>
     *
     * @param postId a {@link java.lang.Long} object
     */
    public void setPostId(Long postId) {
        this.postId = postId;
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
     * <p>Getter for the field <code>country</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getCountry() {
        return country;
    }

    /**
     * <p>Setter for the field <code>country</code>.</p>
     *
     * @param country a {@link java.lang.String} object
     */
    public void setCountry(String country) {
        this.country = country;
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
     * <p>Getter for the field <code>replier</code>.</p>
     *
     * @return a {@link java.lang.Long} object
     */
    public Long getReplier() {
        return replier;
    }

    /**
     * <p>Setter for the field <code>replier</code>.</p>
     *
     * @param replier a {@link java.lang.Long} object
     */
    public void setReplier(Long replier) {
        this.replier = replier;
    }

}
