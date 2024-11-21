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

import io.leafage.basic.assets.domain.Tag;
import io.leafage.basic.assets.dto.TagDTO;
import io.leafage.basic.assets.repository.TagPostsRepository;
import io.leafage.basic.assets.repository.TagRepository;
import io.leafage.basic.assets.service.TagService;
import io.leafage.basic.assets.vo.TagVO;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * tag service impl.
 *
 * @author wq li
 */
@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagPostsRepository tagPostsRepository;

    /**
     * <p>Constructor for TagServiceImpl.</p>
     *
     * @param tagRepository      a {@link io.leafage.basic.assets.repository.TagRepository} object
     * @param tagPostsRepository a {@link io.leafage.basic.assets.repository.TagPostsRepository} object
     */
    public TagServiceImpl(TagRepository tagRepository, TagPostsRepository tagPostsRepository) {
        this.tagRepository = tagRepository;
        this.tagPostsRepository = tagPostsRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<TagVO> retrieve(int page, int size, String sortBy, boolean descending) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        return tagRepository.findAll(pageable).map(tag -> {
            TagVO vo = convertToVO(tag, TagVO.class);
            long count = tagPostsRepository.countByTagId(tag.getId());
            vo.setCount(count);
            return vo;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TagVO fetch(Long id) {
        Assert.notNull(id, "id must not be null.");
        Tag tag = tagRepository.findById(id).orElse(null);
        if (tag == null) {
            return null;
        }
        return convertToVO(tag, TagVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String name, Long id) {
        Assert.hasText(name, "name must not be blank.");
        if (id == null) {
            return tagRepository.existsByName(name);
        }
        return tagRepository.existsByNameAndIdNot(name, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TagVO create(TagDTO dto) {
        Tag tag = new Tag();
        BeanCopier copier = BeanCopier.create(TagDTO.class, Tag.class, false);
        copier.copy(dto, tag, null);

        tag = tagRepository.saveAndFlush(tag);
        return convertToVO(tag, TagVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TagVO modify(Long id, TagDTO dto) {
        Assert.notNull(id, "id must not be null.");
        Tag tag = tagRepository.findById(id).orElse(null);
        if (tag == null) {
            return null;
        }
        BeanCopier copier = BeanCopier.create(TagDTO.class, Tag.class, false);
        copier.copy(dto, tag, null);

        tag = tagRepository.save(tag);
        return convertToVO(tag, TagVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Long id) {
        Assert.notNull(id, "id must not be null.");

        tagRepository.deleteById(id);
    }

}
