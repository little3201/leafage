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

package top.leafage.assets.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.assets.domain.dto.CommentDTO;
import top.leafage.assets.domain.vo.CommentVO;
import top.leafage.assets.repository.CommentRepository;
import top.leafage.assets.service.CommentService;


/**
 * comment service impl
 *
 * @author wq li
 */
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    /**
     * <p>Constructor for CommentServiceImpl.</p>
     *
     * @param commentRepository a {@link CommentRepository} object
     */
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<CommentVO> comments(Long postId) {
        Assert.notNull(postId, String.format(_MUST_NOT_BE_NULL, "postId"));

        return commentRepository.findByPostIdAndReplierIsNull(postId)
                .map(CommentVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<CommentVO> replies(Long replier) {
        Assert.notNull(replier, String.format(_MUST_NOT_BE_NULL, "replier"));

        return commentRepository.findByReplier(replier)
                .map(CommentVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Mono<CommentVO> create(CommentDTO dto) {
        return commentRepository.save(CommentDTO.toEntity(dto))
                .map(CommentVO::from);
    }

}
