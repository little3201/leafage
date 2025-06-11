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

package io.leafage.assets.domain.superclass;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * bo class for comment
 *
 * @author wq li
 */
public abstract class CommentModel {

    /**
     * 帖子
     */
    @NotNull
    private Long postId;

    /**
     * 内容
     */
    @NotBlank
    private String context;

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
