/*
 *  Copyright 2018-2024 little3201.
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

package io.leafage.basic.assets.repository;

import io.leafage.basic.assets.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * comment repository.
 *
 * @author wq li  2021-09-29 14:19
 **/
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 根据postsId查询
     *
     * @param postId 帖子ID
     * @return 关联的数据
     */
    List<Comment> findByPostIdAndReplierIsNull(Long postId);

    /**
     * 根据replier查询
     *
     * @param replier 回复信息
     * @return 关联的数据
     */
    List<Comment> findByReplier(Long replier);

    /**
     * 查询回复记录数
     *
     * @param replier 回复id
     * @return 记录数
     */
    Long countByReplier(Long replier);

}
