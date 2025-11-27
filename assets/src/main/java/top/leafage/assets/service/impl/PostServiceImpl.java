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

import top.leafage.assets.domain.Post;
import top.leafage.assets.domain.dto.PostDTO;
import top.leafage.assets.domain.vo.PostVO;
import top.leafage.assets.repository.PostRepository;
import top.leafage.assets.service.PostService;
import org.jspecify.annotations.NonNull;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


/**
 * posts service impl.
 *
 * @author wq li
 */
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private static final BeanCopier copier = BeanCopier.create(PostDTO.class, Post.class, false);

    /**
     * Constructor for PostsServiceImpl.
     *
     * @param postRepository a {@link PostRepository} object
     */
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<@NonNull PostVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<@NonNull Post> spec = (root, query, cb) ->
                buildPredicate(filters, cb, root).orElse(null);

        return postRepository.findAll(spec, pageable)
                .map(PostVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PostVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return postRepository.findById(id)
                .map(PostVO::from)
                .orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String title, Long id) {
        Assert.hasText(title, String.format(_MUST_NOT_BE_EMPTY, "title"));
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
        Post entity = postRepository.saveAndFlush(PostDTO.toEntity(dto));
        return PostVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PostVO modify(Long id, PostDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return postRepository.findById(id)
                .map(existing -> {
                    copier.copy(dto, existing, null);
                    return postRepository.save(existing);
                })
                .map(PostVO::from)
                .orElseThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        postRepository.deleteById(id);
    }

}
