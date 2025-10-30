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

package io.leafage.assets.repository;

import io.leafage.assets.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * posts repository
 *
 * @author wq li
 */
@Repository
public interface PostRepository extends R2dbcRepository<Post, Long> {

    /**
     * 关键词查询
     *
     * @param keyword 关键词
     * @return 匹配结果
     */
    Flux<Post> findAllByTitle(String keyword);

    /**
     * 是否已存在
     *
     * @param title 标题
     * @return true-是，false-否
     */
    Mono<Boolean> existsByTitle(String title);

    /**
     * 是否已存在
     *
     * @param title 标题
     * @param id    主键
     * @return true-是，false-否
     */
    Mono<Boolean> existsByTitleAndIdNot(String title, Long id);
}
