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

import io.leafage.assets.domain.Comment;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * comment repository
 *
 * @author wq li
 */
@Repository
public interface CommentRepository extends R2dbcRepository<Comment, Long> {

    /**
     * 根据帖子ID查询
     *
     * @param postId 帖子ID
     * @return 关联的数据
     */
    Flux<Comment> findByPostIdAndReplierIsNull(Long postId);

    /**
     * 统计回复
     *
     * @param replier 关联代码
     * @return 关联的数据记录数
     */
    Mono<Long> countByReplier(Long replier);

    /**
     * 查询回复
     *
     * @param replier 关联代码
     * @return 关联的数据
     */
    Flux<Comment> findByReplier(Long replier);
}
