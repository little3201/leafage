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

package io.leafage.basic.hypervisor.service.impl;

import io.leafage.basic.hypervisor.domain.Dictionary;
import io.leafage.basic.hypervisor.dto.DictionaryDTO;
import io.leafage.basic.hypervisor.repository.DictionaryRepository;
import io.leafage.basic.hypervisor.service.DictionaryService;
import io.leafage.basic.hypervisor.vo.DictionaryVO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import top.leafage.common.servlet.ServletAbstractTreeNodeService;

import java.util.ArrayList;
import java.util.List;

/**
 * dictionary service impl.
 *
 * @author wq li
 */
@Service
public class DictionaryServiceImpl extends ServletAbstractTreeNodeService<Dictionary> implements DictionaryService {

    private final DictionaryRepository dictionaryRepository;

    /**
     * <p>Constructor for DictionaryServiceImpl.</p>
     *
     * @param dictionaryRepository a {@link io.leafage.basic.hypervisor.repository.DictionaryRepository} object
     */
    public DictionaryServiceImpl(DictionaryRepository dictionaryRepository) {
        this.dictionaryRepository = dictionaryRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<DictionaryVO> retrieve(int page, int size, String sortBy, boolean descending, String name) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<Dictionary> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isNull(root.get("superiorId")));
            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return dictionaryRepository.findAll(spec, pageable)
                .map(dictionary -> convertToVO(dictionary, DictionaryVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DictionaryVO fetch(Long id) {
        Assert.notNull(id, "id must not be null.");

        return dictionaryRepository.findById(id)
                .map(dictionary -> convertToVO(dictionary, DictionaryVO.class)).orElse(null);
    }

    @Override
    public boolean enable(Long id) {
        return dictionaryRepository.updateEnabledById(id) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DictionaryVO> subset(Long id) {
        Assert.notNull(id, "id must not be null.");

        return dictionaryRepository.findAllBySuperiorId(id).stream()
                .map(dictionary -> convertToVO(dictionary, DictionaryVO.class)).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(Long superiorId, String name, Long id) {
        Assert.hasText(name, "name must not be empty.");

        if (id == null) {
            return dictionaryRepository.existsBySuperiorIdAndName(superiorId, name);
        }
        return dictionaryRepository.existsBySuperiorIdAndNameAndIdNot(superiorId, name, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DictionaryVO create(DictionaryDTO dto) {
        Dictionary dictionary = convertToDomain(dto, Dictionary.class);

        dictionaryRepository.saveAndFlush(dictionary);
        return convertToVO(dictionary, DictionaryVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DictionaryVO modify(Long id, DictionaryDTO dto) {
        Assert.notNull(id, "id must not be null.");
        return dictionaryRepository.findById(id).map(existing -> {
            Dictionary dictionary = convert(dto, existing);
            dictionary = dictionaryRepository.save(dictionary);
            return convertToVO(dictionary, DictionaryVO.class);
        }).orElseThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Long id) {
        Assert.notNull(id, "id must not be null.");
        dictionaryRepository.deleteById(id);
    }

}
