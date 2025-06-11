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

package io.leafage.assets.service;

import io.leafage.assets.dto.CommentDTO;
import io.leafage.assets.vo.CommentVO;
import reactor.core.publisher.Flux;
import top.leafage.common.r2dbc.R2dbcCrudService;

/**
 * comment service
 *
 * @author wq li
 */
public interface CommentService extends R2dbcCrudService<CommentDTO, CommentVO> {

    /**
     * 查询评论
     *
     * @param postId 帖子ID
     * @return 关联的评论
     */
    Flux<CommentVO> comments(Long postId);

    /**
     * 查询回复
     *
     * @param id 主键
     * @return 回复的评论
     */
    Flux<CommentVO> replies(Long id);

}
