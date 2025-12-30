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

package top.leafage.hypervisor.assets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import top.leafage.hypervisor.assets.domain.Comment;

import java.util.List;

/**
 * comment repository.
 *
 * @author wq li
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

    /**
     * 根据postsId查询
     *
     * @param postId 帖子ID
     * @return 关联的数据
     */
    List<Comment> findAllByPostIdAndSuperiorIdIsNull(Long postId);

    /**
     * 根据superior id查询
     *
     * @param superiorId 回复信息
     * @return 关联的数据
     */
    List<Comment> findAllBySuperiorId(Long superiorId);

    /**
     * 记录数
     *
     * @param superiorId 回复id
     * @return 记录数
     */
    long countBySuperiorId(Long superiorId);

}
