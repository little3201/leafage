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

import io.leafage.assets.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * posts repository.
 *
 * @author wq li
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 是否已存在
     *
     * @param title 名称
     * @return true-是，false-否
     */
    boolean existsByTitle(String title);

    /**
     * 是否已存在
     *
     * @param title 名称
     * @return true-是，false-否
     */
    boolean existsByTitleAndIdNot(String title, Long id);

}
