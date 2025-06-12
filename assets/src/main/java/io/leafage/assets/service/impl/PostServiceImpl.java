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

package io.leafage.assets.service.impl;

import io.leafage.assets.domain.Post;
import io.leafage.assets.domain.PostContent;
import io.leafage.assets.dto.PostDTO;
import io.leafage.assets.repository.PostContentRepository;
import io.leafage.assets.repository.PostRepository;
import io.leafage.assets.service.PostService;
import io.leafage.assets.vo.PostVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.DomainConverter;

import java.util.NoSuchElementException;

/**
 * post service impl
 *
 * @author wq li
 */
@Service
public class PostServiceImpl extends DomainConverter implements PostService {

    private final PostRepository postRepository;
    private final PostContentRepository postContentRepository;


    /**
     * <p>Constructor for PostServiceImpl.</p>
     *
     * @param postRepository        a {@link PostRepository} object
     * @param postContentRepository a {@link PostContentRepository} object
     */
    public PostServiceImpl(PostRepository postRepository, PostContentRepository postContentRepository) {
        this.postRepository = postRepository;
        this.postContentRepository = postContentRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Page<PostVO>> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        return postRepository.findAllBy(pageable)
                .map(post -> convertToVO(post, PostVO.class))
                .collectList()
                .zipWith(postRepository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<PostVO> fetch(Long id) {
        Assert.notNull(id, "id must not be null.");

        return postRepository.findById(id)
                .flatMap(post -> postContentRepository.getByPostId(post.getId())
                        .map(postContent -> {
                            PostVO vo = convertToVO(post, PostVO.class);
                            vo.setContent(postContent.getContent());
                            return vo;
                        })
                );
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Mono<PostVO> create(PostDTO dto) {
        return postRepository.save(convertToDomain(dto, Post.class))
                .flatMap(p -> {
                    PostContent postContent = new PostContent();
                    postContent.setPostId(p.getId());
                    postContent.setContent(dto.getContent());
                    return postContentRepository.save(postContent)
                            .map(pc -> convertToVO(p, PostVO.class));
                });
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Mono<PostVO> modify(Long id, PostDTO dto) {
        Assert.notNull(id, "id must not be null.");

        return postRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .map(post -> convert(dto, post))
                .flatMap(postRepository::save)
                .flatMap(p -> postContentRepository.getByPostId(p.getId())
                        .defaultIfEmpty(new PostContent())
                        .doOnNext(postContent -> {
                            postContent.setPostId(p.getId());
                            postContent.setContent(dto.getContent());
                        })
                        .flatMap(postContentRepository::save)
                        .map(pc -> convertToVO(p, PostVO.class))
                );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> remove(Long id) {
        Assert.notNull(id, "id must not be null.");

        return postContentRepository.getByPostId(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .flatMap(postContent -> postContentRepository.deleteById(postContent.getId()))
                .then(postRepository.deleteById(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<PostVO> search(String keyword) {
        Assert.hasText(keyword, "keyword must not be empty.");

        return postRepository.findAllByTitle(keyword)
                .map(post -> convertToVO(post, PostVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Boolean> exists(String title, Long id) {
        Assert.hasText(title, "title must not be empty.");

        if (id == null) {
            return postRepository.existsByTitle(title);
        }
        return postRepository.existsByTitleAndIdNot(title, id);
    }

}
