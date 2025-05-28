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
     * 分页查询
     *
     * @param pageable 分页参数
     * @return 有效帖子
     */
    Flux<Post> findAllBy(Pageable pageable);

    /**
     * 根据分类分页查询
     *
     * @param categoryId 分类ID
     * @param pageable   分页参数
     * @return 有效帖子
     */
    Flux<Post> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * 关联统计
     *
     * @param categoryId 分类ID
     * @return 帖子数
     */
    Mono<Long> countByCategoryId(Long categoryId);

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
     * @param title 名称
     * @return true-是，false-否
     */
    Mono<Boolean> existsByTitle(String title);
}
