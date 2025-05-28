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
package io.leafage.assets.repository;

import io.leafage.assets.domain.TagPosts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * tag repository.
 *
 * @author wq li
 */
@Repository
public interface TagPostsRepository extends JpaRepository<TagPosts, Long> {

    /**
     * 根据tag查post
     *
     * @param tagId tag主键
     * @return 数据量
     */
    Long countByTagId(Long tagId);

    /**
     * 根据post查tag
     *
     * @param postId post主键
     * @return 关联数据集
     */
    List<TagPosts> findAllByPostId(Long postId);

    /**
     * 根据post删除
     *
     * @param postId post主键
     */
    void deleteByPostId(Long postId);
}
