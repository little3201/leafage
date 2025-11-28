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

package top.leafage.assets.service.impl;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import top.leafage.assets.domain.Comment;
import top.leafage.assets.domain.dto.CommentDTO;
import top.leafage.assets.domain.vo.CommentVO;
import top.leafage.assets.repository.CommentRepository;
import top.leafage.assets.service.CommentService;

import java.util.List;

/**
 * comment service impl.
 *
 * @author wq li
 */
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    /**
     * Constructor for CommentServiceImpl.
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
    public Page<@NonNull CommentVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<@NonNull Comment> spec = (root, query, cb) ->
                buildPredicate(filters, cb, root).orElse(null);

        return commentRepository.findAll(spec, pageable).map(comment -> {
            Long count = commentRepository.countByReplier(comment.getId());
            return CommentVO.from(comment, count);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CommentVO> relation(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        return commentRepository.findAllByPostIdAndReplierIsNull(id)
                .stream().map(CommentVO::from)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CommentVO> replies(Long replier) {
        return commentRepository.findAllByReplier(replier)
                .stream().map(entity -> {
                    Long count = commentRepository.countByReplier(entity.getId());
                    return CommentVO.from(entity, count);
                }).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommentVO create(CommentDTO dto) {
        Comment entity = commentRepository.saveAndFlush(CommentDTO.toEntity(dto));
        return CommentVO.from(entity);
    }

}
