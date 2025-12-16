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
package top.leafage.hypervisor.assets.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import top.leafage.hypervisor.assets.domain.Post;
import top.leafage.hypervisor.assets.domain.dto.PostDTO;
import top.leafage.hypervisor.assets.domain.vo.PostVO;
import top.leafage.hypervisor.assets.repository.PostRepository;
import top.leafage.hypervisor.assets.service.PostService;


/**
 * posts service impl.
 *
 * @author wq li
 */
@Service
public class PostServiceImpl implements PostService {

    private static final BeanCopier copier = BeanCopier.create(PostDTO.class, Post.class, false);
    private final PostRepository postRepository;

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
                .orElseThrow(() -> new EntityNotFoundException("post not found: " + id));
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PostVO create(PostDTO dto) {
        if (postRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("title already exists: " + dto.getTitle());
        }
        Post entity = postRepository.saveAndFlush(PostDTO.toEntity(dto));
        return PostVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PostVO modify(Long id, PostDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        Post existing = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("post not found: " + id));
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
    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        if (!postRepository.existsById(id)) {
            throw new EntityNotFoundException("post not found: " + id);
        }
        postRepository.deleteById(id);
    }

}
