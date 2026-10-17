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
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import top.leafage.hypervisor.assets.domain.Post;
import top.leafage.hypervisor.assets.domain.dto.PostDTO;
import top.leafage.hypervisor.assets.domain.vo.PostVO;
import top.leafage.hypervisor.assets.repository.PostRepository;
import top.leafage.hypervisor.assets.service.PostService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.data.relational.core.query.Query.query;


/**
 * posts service impl.
 *
 * @author wq li
 */
@Service
public class PostServiceImpl implements PostService {

    private static final BeanCopier copier = BeanCopier.create(PostDTO.class, Post.class, false);
    private final PostRepository postRepository;
    private final JdbcAggregateTemplate jdbcAggregateTemplate;

    /**
     * Constructor for PostsServiceImpl.
     *
     * @param postRepository a {@link PostRepository} object
     */
    public PostServiceImpl(PostRepository postRepository, JdbcAggregateTemplate jdbcAggregateTemplate) {
        this.postRepository = postRepository;
        this.jdbcAggregateTemplate = jdbcAggregateTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<@NonNull PostVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        Criteria criteria = buildCriteria(filters, Post.class);

        List<PostVO> voList = jdbcAggregateTemplate.findAll(query(criteria).with(pageable), Post.class)
                .stream().map(PostVO::from)
                .toList();
        return new PageImpl<>(voList, pageable, jdbcAggregateTemplate.count(query(criteria), Post.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PostVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return postRepository.findById(id)
                .map(PostVO::from)
                .orElseThrow(() -> new NoSuchElementException("post not found: " + id));
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public PostVO create(PostDTO dto) {
        if (postRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("title already exists: " + dto.getTitle());
        }
        Post entity = postRepository.save(PostDTO.toEntity(dto));
        return PostVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public PostVO modify(Long id, PostDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        Post existing = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("post not found: " + id));
        if (!existing.getTitle().equals(dto.getTitle()) &&
                postRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("title already exists: " + dto.getTitle());
        }
        copier.copy(dto, existing, null);
        Post entity = postRepository.save(existing);
        return PostVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        if (!postRepository.existsById(id)) {
            throw new NoSuchElementException("post not found: " + id);
        }
        postRepository.deleteById(id);
    }

}
