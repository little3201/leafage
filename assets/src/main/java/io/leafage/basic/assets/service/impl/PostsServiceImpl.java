/*
 * Copyright (c) 2024.  little3201.
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
package io.leafage.basic.assets.service.impl;

import io.leafage.basic.assets.domain.Post;
import io.leafage.basic.assets.domain.PostContent;
import io.leafage.basic.assets.domain.Tag;
import io.leafage.basic.assets.domain.TagPosts;
import io.leafage.basic.assets.dto.PostDTO;
import io.leafage.basic.assets.repository.PostContentRepository;
import io.leafage.basic.assets.repository.PostRepository;
import io.leafage.basic.assets.repository.TagPostsRepository;
import io.leafage.basic.assets.repository.TagRepository;
import io.leafage.basic.assets.service.PostsService;
import io.leafage.basic.assets.vo.PostVO;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * posts service impl.
 *
 * @author wq li
 */
@Service
public class PostsServiceImpl implements PostsService {

    private final PostRepository postRepository;
    private final PostContentRepository postContentRepository;
    private final TagRepository tagRepository;
    private final TagPostsRepository tagPostsRepository;

    /**
     * <p>Constructor for PostsServiceImpl.</p>
     *
     * @param postRepository        a {@link io.leafage.basic.assets.repository.PostRepository} object
     * @param postContentRepository a {@link io.leafage.basic.assets.repository.PostContentRepository} object
     * @param tagRepository         a {@link io.leafage.basic.assets.repository.TagRepository} object
     * @param tagPostsRepository    a {@link io.leafage.basic.assets.repository.TagPostsRepository} object
     */
    public PostsServiceImpl(PostRepository postRepository, PostContentRepository postContentRepository,
                            TagRepository tagRepository, TagPostsRepository tagPostsRepository) {
        this.postRepository = postRepository;
        this.postContentRepository = postContentRepository;
        this.tagRepository = tagRepository;
        this.tagPostsRepository = tagPostsRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PostVO> retrieve(int page, int size, String sortBy, boolean descending) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        return postRepository.findAll(pageable).map(this::convert);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PostVO fetch(Long id) {
        Assert.notNull(id, "id must not be null.");
        //查询基本信息
        PostVO vo = postRepository.findById(id).map(this::convert).orElse(null);
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
        Assert.hasText(title, "title must not be blank.");
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

        // sava tag
        this.relation(post.getId(), dto.getTags());
        //转换结果
        return this.convert(post);
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

        // sava tag
        this.relation(post.getId(), dto.getTags());

        //转换结果
        return this.convert(post);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Long id) {
        Assert.notNull(id, "id must not be null.");

        postRepository.deleteById(id);
    }

    /**
     * 对象转换为输出结果对象
     *
     * @param post 信息
     * @return 输出转换后的vo对象
     */
    private PostVO convert(Post post) {
        PostVO vo = convertToVO(post, PostVO.class);

        List<TagPosts> tagPostsList = tagPostsRepository.findAllByPostId(post.getId());
        // 转换 tags
        if (!CollectionUtils.isEmpty(tagPostsList)) {
            Set<String> tags = tagPostsList.stream().map(tagPosts -> {
                Optional<Tag> optional = tagRepository.findById(tagPosts.getId());
                return optional.map(Tag::getName).orElse(null);
            }).collect(Collectors.toSet());

            vo.setTags(tags);
        }
        return vo;
    }

    private void relation(Long id, Set<String> tags) {
        tagPostsRepository.deleteByPostId(id);
        // 设置新的关联
        if (!CollectionUtils.isEmpty(tags)) {
            List<TagPosts> tagPostsList = new ArrayList<>(tags.size());
            tags.forEach(t -> {
                Tag tag = tagRepository.getByName(t);
                if (tag == null) {
                    Tag newTag = new Tag();
                    newTag.setName(t);
                    tag = tagRepository.saveAndFlush(newTag);
                }

                TagPosts tagPosts = new TagPosts();
                tagPosts.setPostId(id);
                tagPosts.setTagId(tag.getId());
                tagPostsList.add(tagPosts);
            });
            tagPostsRepository.saveAll(tagPostsList);
        }
    }

}
