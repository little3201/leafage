/*
 *
 *  * Copyright(c) 2019-present the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *       https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package top.leafage.hypervisor.assets.service.impl;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import top.leafage.hypervisor.assets.domain.Comment;
import top.leafage.hypervisor.assets.domain.dto.CommentDTO;
import top.leafage.hypervisor.assets.domain.vo.CommentVO;
import top.leafage.hypervisor.assets.repository.CommentRepository;
import top.leafage.hypervisor.assets.service.CommentService;

import java.util.List;

import static org.springframework.data.relational.core.query.Query.query;

/**
 * comment service impl.
 *
 * @author wq li
 */
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final JdbcAggregateTemplate jdbcAggregateTemplate;

    /**
     * Constructor for CommentServiceImpl.
     *
     * @param commentRepository a {@link CommentRepository} object
     */
    public CommentServiceImpl(CommentRepository commentRepository, JdbcAggregateTemplate jdbcAggregateTemplate) {
        this.commentRepository = commentRepository;
        this.jdbcAggregateTemplate = jdbcAggregateTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<@NonNull CommentVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        Criteria criteria = buildCriteria(filters, Comment.class);

        List<CommentVO> voList = jdbcAggregateTemplate.findAll(query(criteria).with(pageable), Comment.class)
                .stream().map(CommentVO::from)
                .toList();
        return new PageImpl<>(voList, pageable, jdbcAggregateTemplate.count(query(criteria), Comment.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CommentVO> relation(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        return commentRepository.findAllByPostIdAndSuperiorIdIsNull(id)
                .stream().map(CommentVO::from)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CommentVO> replies(Long replier) {
        return commentRepository.findAllBySuperiorId(replier)
                .stream().map(entity -> {
                    long count = commentRepository.countBySuperiorId(entity.getId());
                    return CommentVO.from(entity, count);
                }).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public CommentVO create(CommentDTO dto) {
        Comment entity = commentRepository.save(CommentDTO.toEntity(dto));
        return CommentVO.from(entity);
    }

}
