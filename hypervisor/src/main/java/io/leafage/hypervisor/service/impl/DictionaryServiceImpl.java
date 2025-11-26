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

package io.leafage.hypervisor.service.impl;

import io.leafage.hypervisor.domain.Dictionary;
import io.leafage.hypervisor.domain.dto.DictionaryDTO;
import io.leafage.hypervisor.domain.vo.DictionaryVO;
import io.leafage.hypervisor.repository.DictionaryRepository;
import io.leafage.hypervisor.service.DictionaryService;
import org.jspecify.annotations.NonNull;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * dictionary service impl.
 *
 * @author wq li
 */
@Service
public class DictionaryServiceImpl implements DictionaryService {

    private final DictionaryRepository dictionaryRepository;
    private static final BeanCopier copier = BeanCopier.create(DictionaryDTO.class, Dictionary.class, false);

    /**
     * <p>Constructor for DictionaryServiceImpl.</p>
     *
     * @param dictionaryRepository a {@link DictionaryRepository} object
     */
    public DictionaryServiceImpl(DictionaryRepository dictionaryRepository) {
        this.dictionaryRepository = dictionaryRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<@NonNull DictionaryVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<@NonNull Dictionary> spec = (root, query, cb) ->
                buildPredicate(filters, cb, root).orElse(null);
        spec = spec.and((root, query, cb) -> cb.isNull(root.get("superiorId")));

        return dictionaryRepository.findAll(spec, pageable)
                .map(DictionaryVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DictionaryVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return dictionaryRepository.findById(id)
                .map(DictionaryVO::from)
                .orElse(null);
    }

    @Override
    public boolean enable(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return dictionaryRepository.updateEnabledById(id) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DictionaryVO> subset(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return dictionaryRepository.findAllBySuperiorId(id)
                .stream().map(DictionaryVO::from)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(Long superiorId, String name, Long id) {
        Assert.hasText(name, String.format(_MUST_NOT_BE_EMPTY, "name"));

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
        Dictionary entity = dictionaryRepository.saveAndFlush(DictionaryDTO.toEntity(dto));
        return DictionaryVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DictionaryVO modify(Long id, DictionaryDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        Dictionary entity = dictionaryRepository.findById(id).map(existing -> {
                    copier.copy(dto, existing, null);
                    return dictionaryRepository.save(existing);
                })
                .orElseThrow();
        return DictionaryVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        dictionaryRepository.deleteById(id);
    }

}
