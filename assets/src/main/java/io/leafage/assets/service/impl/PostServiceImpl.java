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
package io.leafage.assets.service.impl;

import io.leafage.assets.domain.FileRecord;
import io.leafage.assets.domain.Post;
import io.leafage.assets.domain.PostContent;
import io.leafage.assets.dto.PostDTO;
import io.leafage.assets.repository.PostContentRepository;
import io.leafage.assets.repository.PostRepository;
import io.leafage.assets.service.PostService;
import io.leafage.assets.vo.PostVO;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import top.leafage.common.DomainConverter;

import java.util.Optional;

/**
 * posts service impl.
 *
 * @author wq li
 */
@Service
public class PostServiceImpl extends DomainConverter implements PostService {

    private final PostRepository postRepository;
    private final PostContentRepository postContentRepository;

    /**
     * <p>Constructor for PostsServiceImpl.</p>
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
    public Page<PostVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<Post> spec = (root, query, cb) ->
                buildPredicate(filters, cb, root).orElse(null);

        return postRepository.findAll(spec, pageable).map(post -> convertToVO(post, PostVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PostVO fetch(Long id) {
        Assert.notNull(id, "id must not be null.");
        //查询基本信息
        PostVO vo = postRepository.findById(id).map(post -> convertToVO(post, PostVO.class)).orElse(null);
        if (vo == null) {
            return null;
        }
        // 获取内容详情
        postContentRepository.getByPostId(id).ifPresent(postContent -> vo.setContent(postContent.getContent()));
        return vo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String title, Long id) {
        Assert.hasText(title, "title must not be empty.");
        if (id == null) {
            return postRepository.existsByTitle(title);
        }
        return postRepository.existsByTitleAndIdNot(title, id);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PostVO create(PostDTO dto) {
        Post post = new Post();
        BeanCopier copier = BeanCopier.create(PostDTO.class, Post.class, false);
        copier.copy(dto, post, null);

        // 保存并立即刷盘
        post = postRepository.saveAndFlush(post);
        //保存帖子内容
        Optional<PostContent> optional = postContentRepository.getByPostId(post.getId());
        PostContent postContent;
        if (optional.isPresent()) {
            postContent = optional.get();
        } else {
            postContent = new PostContent();
            postContent.setPostId(post.getId());
        }
        postContent.setContent(dto.getContent());
        postContentRepository.saveAndFlush(postContent);

        return convertToVO(post, PostVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PostVO modify(Long id, PostDTO dto) {
        Assert.notNull(id, "id must not be null.");
        //查询基本信息
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            return null;
        }
        BeanCopier copier = BeanCopier.create(PostDTO.class, Post.class, false);
        copier.copy(dto, post, null);

        post = postRepository.save(post);

        //保存文章内容
        Optional<PostContent> optional = postContentRepository.getByPostId(id);
        PostContent postContent;
        if (optional.isPresent()) {
            postContent = optional.get();
        } else {
            postContent = new PostContent();
            postContent.setPostId(id);
        }
        postContent.setContent(dto.getContent());
        postContentRepository.save(postContent);

        return convertToVO(post, PostVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Long id) {
        Assert.notNull(id, "id must not be null.");

        postRepository.deleteById(id);
    }

}
