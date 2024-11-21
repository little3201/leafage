/*
 * Copyright (c) 2024.  little3201.
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
package io.leafage.basic.assets.repository;

import io.leafage.basic.assets.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * tag repository.
 *
 * @author wq li
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * get
     *
     * @param name 名称
     * @return 结果
     */
    Tag getByName(String name);

    /**
     * 是否已存在
     *
     * @param name 名称
     * @return true-是，false-否
     */
    boolean existsByName(String name);
}
