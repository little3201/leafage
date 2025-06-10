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

import io.leafage.assets.domain.Tag;
import io.leafage.assets.dto.TagDTO;
import io.leafage.assets.repository.TagRepository;
import io.leafage.assets.service.TagService;
import io.leafage.assets.vo.TagVO;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.leafage.common.DomainConverter;

/**
 * tag service impl.
 *
 * @author wq li
 */
@Service
public class TagServiceImpl extends DomainConverter implements TagService {

    private final TagRepository tagRepository;

    /**
     * <p>Constructor for TagServiceImpl.</p>
     *
     * @param tagRepository a {@link TagRepository} object
     */
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<TagVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        return tagRepository.findAll(pageable).map(tag -> convertToVO(tag, TagVO.class));
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
        Assert.hasText(name, "name must not be empty.");
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
