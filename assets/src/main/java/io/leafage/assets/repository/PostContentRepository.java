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

import io.leafage.assets.domain.PostContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * posts content repository.
 *
 * @author wq li
 */
@Repository
public interface PostContentRepository extends JpaRepository<PostContent, String> {

    /**
     * 根据postId查询enabled信息
     *
     * @param postId 帖子ID
     * @return 查询结果
     */
    Optional<PostContent> getByPostId(Long postId);
}
