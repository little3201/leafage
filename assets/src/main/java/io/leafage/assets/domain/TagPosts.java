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

import jakarta.persistence.*;

/**
 * model class for tag_posts.
 *
 * @author wq li
 */
@Entity
@Table(name = "tag_posts")
public class TagPosts {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * tag id
     */
    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    /**
     * post id
     */
    @Column(name = "post_id", nullable = false)
    private Long postId;


    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link Long} object
     */
    public Long getId() {
        return id;
    }

    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a {@link Long} object
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * <p>Getter for the field <code>tagId</code>.</p>
     *
     * @return a {@link Long} object
     */
    public Long getTagId() {
        return tagId;
    }

    /**
     * <p>Setter for the field <code>tagId</code>.</p>
     *
     * @param tagId a {@link Long} object
     */
    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    /**
     * <p>Getter for the field <code>postId</code>.</p>
     *
     * @return a {@link Long} object
     */
    public Long getPostId() {
        return postId;
    }

    /**
     * <p>Setter for the field <code>postId</code>.</p>
     *
     * @param postId a {@link Long} object
     */
    public void setPostId(Long postId) {
        this.postId = postId;
    }
}
