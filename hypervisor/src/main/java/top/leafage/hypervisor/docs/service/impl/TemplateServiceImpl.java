/*
 * Copyright(c) 2019-present the original author or authors.
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

package top.leafage.hypervisor.docs.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import top.leafage.common.logging.annotation.OperationLog;
import top.leafage.hypervisor.docs.domain.Section;
import top.leafage.hypervisor.docs.domain.Template;
import top.leafage.hypervisor.docs.domain.dto.TemplateDTO;
import top.leafage.hypervisor.docs.domain.vo.TemplateVO;
import top.leafage.hypervisor.docs.repository.SectionRepository;
import top.leafage.hypervisor.docs.repository.TemplateRepository;
import top.leafage.hypervisor.docs.service.TemplateService;

import java.util.List;

import static top.leafage.hypervisor.constants.GlobalConstant.ID_MUST_NOT_BE_NULL;

/**
 * template service impl
 *
 * @author wq li
 */
@OperationLog("templates")
@Service
public class TemplateServiceImpl implements TemplateService {

    private static final BeanCopier copier = BeanCopier.create(TemplateDTO.class, Template.class, false);

    private final TemplateRepository templateRepository;
    private final SectionRepository sectionRepository;

    /**
     * Constructor for SchemaServiceImpl.
     *
     * @param templateRepository a {@link TemplateRepository} object
     * @param sectionRepository  a {@link SectionRepository} object
     */
    public TemplateServiceImpl(TemplateRepository templateRepository, SectionRepository sectionRepository) {
        this.templateRepository = templateRepository;
        this.sectionRepository = sectionRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<TemplateVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<Template> spec = (root, _, cb) ->
                buildPredicate(filters, cb, root).orElse(null);

        return templateRepository.findAll(spec, pageable)
                .map(TemplateVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TemplateVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return templateRepository.findById(id)
                .map(TemplateVO::from)
                .orElseThrow(() -> new EntityNotFoundException("template not found: " + id));
    }

    @Transactional
    @Override
    public boolean enable(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        if (!templateRepository.existsById(id)) {
            throw new EntityNotFoundException("template not found: " + id);
        }
        return templateRepository.enableById(id) > 0;
    }

    @Transactional
    @Override
    public boolean disable(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        if (!templateRepository.existsById(id)) {
            throw new EntityNotFoundException("template not found: " + id);
        }
        return templateRepository.disableById(id) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public TemplateVO create(TemplateDTO dto) {
        if (templateRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("name already exists: " + dto.getName());
        }
        Template entity = templateRepository.save(TemplateDTO.toEntity(dto));
        return TemplateVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public TemplateVO modify(Long id, TemplateDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        Template existing = templateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("template not found: " + id));
        if (!existing.getName().equals(dto.getName()) &&
                templateRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("name already exists: " + dto.getName());
        }
        copier.copy(dto, existing, null);
        Template entity = templateRepository.save(existing);
        return TemplateVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        if (!templateRepository.existsById(id)) {
            throw new EntityNotFoundException("template not found: " + id);
        }
        templateRepository.deleteById(id);
        // 删除关联的章节
        List<Long> ids = sectionRepository.findAllByOwnerIdAndOwnerType(id, Section.OwnerType.TEMPLATE)
                .stream().map(Section::getId)
                .toList();
        sectionRepository.deleteAllById(ids);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public boolean publish(Long id) {
        return templateRepository.updateStatusById(id, Template.Status.PUBLISHED) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public boolean archive(Long id) {
        return templateRepository.updateStatusById(id, Template.Status.ARCHIVED) > 0;
    }
}
