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

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import top.leafage.assets.domain.Post;
import top.leafage.assets.domain.dto.PostDTO;
import top.leafage.assets.domain.vo.PostVO;
import top.leafage.assets.repository.PostRepository;
import top.leafage.assets.service.PostService;

import java.util.NoSuchElementException;

/**
 * post service impl
 *
 * @author wq li
 */
@Service
public class PostServiceImpl implements PostService {

    private static final BeanCopier copier = BeanCopier.create(PostDTO.class, Post.class, false);
    private final PostRepository postRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;


    /**
     * Constructor for PostServiceImpl.
     *
     * @param postRepository a {@link PostRepository} object
     */
    public PostServiceImpl(PostRepository postRepository, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.postRepository = postRepository;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Page<PostVO>> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        Criteria criteria = buildCriteria(filters, Post.class);

        return r2dbcEntityTemplate.select(Post.class)
                .matching(Query.query(criteria).with(pageable))
                .all()
                .map(PostVO::from)
                .collectList()
                .zipWith(r2dbcEntityTemplate.count(Query.query(criteria), Post.class))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<PostVO> fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return postRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .map(PostVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Mono<PostVO> create(PostDTO dto) {
        return postRepository.existsByTitle(dto.getTitle())
                .flatMap(exists -> {
                    if (exists) {
                        throw new IllegalArgumentException("title already exists: " + dto.getTitle());
                    }
                    return postRepository.save(PostDTO.toEntity(dto))
                            .map(PostVO::from);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Mono<PostVO> modify(Long id, PostDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return postRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .flatMap(existing -> {
                    if (!existing.getTitle().equals(dto.getTitle())) {
                        return Mono.just(existing);
                    }

                    return postRepository.existsByTitle(dto.getTitle())
                            .flatMap(exists -> {
                                if (exists) {
                                    return Mono.error(new IllegalArgumentException("post title already exists: " + dto.getTitle()));
                                }
                                return Mono.just(existing);
                            });
                })
                .flatMap(existing -> {
                    copier.copy(dto, existing, null);
                    return postRepository.save(existing);
                })
                .map(PostVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        return postRepository.existsById(id)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new NoSuchElementException("post not found: " + id));
                    }
                    return postRepository.deleteById(id);
                });
    }

}
