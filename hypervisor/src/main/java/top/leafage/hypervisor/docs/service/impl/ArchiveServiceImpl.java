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
import top.leafage.hypervisor.docs.domain.Archive;
import top.leafage.hypervisor.docs.domain.Section;
import top.leafage.hypervisor.docs.domain.dto.ArchiveDTO;
import top.leafage.hypervisor.docs.domain.vo.ArchiveVO;
import top.leafage.hypervisor.docs.repository.ArchiveRepository;
import top.leafage.hypervisor.docs.repository.SectionRepository;
import top.leafage.hypervisor.docs.service.ArchiveService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static top.leafage.hypervisor.constants.GlobalConstant.ID_MUST_NOT_BE_NULL;

/**
 * archive service impl
 *
 * @author wq li
 */
@OperationLog("archives")
@Service
public class ArchiveServiceImpl implements ArchiveService {

    private static final BeanCopier copier = BeanCopier.create(ArchiveDTO.class, Archive.class, false);

    private final ArchiveRepository archiveRepository;
    private final SectionRepository sectionRepository;

    /**
     * Constructor for ArchiveServiceImpl.
     *
     * @param archiveRepository a {@link ArchiveRepository} object
     * @param sectionRepository a {@link SectionRepository} object
     */
    public ArchiveServiceImpl(ArchiveRepository archiveRepository, SectionRepository sectionRepository) {
        this.archiveRepository = archiveRepository;
        this.sectionRepository = sectionRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<ArchiveVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<Archive> spec = (root, _, cb) ->
                buildPredicate(filters, cb, root).orElse(null);

        return archiveRepository.findAll(spec, pageable)
                .map(ArchiveVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArchiveVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return archiveRepository.findById(id)
                .map(ArchiveVO::from)
                .orElseThrow(() -> new EntityNotFoundException("archive not found: " + id));
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public ArchiveVO create(ArchiveDTO dto) {
        if (archiveRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("title already exists: " + dto.getTitle());
        }
        Archive entity = archiveRepository.save(ArchiveDTO.toEntity(dto));
        // 执行模板内容复制
        Long schemaId = dto.getSchemaId();
        if (schemaId != null) {
            copySections(schemaId, entity.getId());
        }
        return ArchiveVO.from(entity);
    }

    private void copySections(Long schemaId, Long archiveId) {
        List<Section> templateSections = sectionRepository.findAllByOwnerIdAndOwnerType(schemaId, Section.OwnerType.ARCHIVE);
        List<Section> copiedSections = templateSections.stream()
                .map(section -> new Section(archiveId, Section.OwnerType.ARCHIVE, section))
                .toList();
        List<Section> savedSections = sectionRepository.saveAll(copiedSections);

        Map<Long, Long> idMapping = new HashMap<>();
        for (int i = 0; i < templateSections.size(); i++) {
            idMapping.put(templateSections.get(i).getId(), savedSections.get(i).getId());
        }
        for (int i = 0; i < templateSections.size(); i++) {
            Long superiorId = templateSections.get(i).getSuperiorId();
            savedSections.get(i).setSuperiorId(idMapping.get(superiorId));
        }
        sectionRepository.saveAll(savedSections);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public ArchiveVO modify(Long id, ArchiveDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        Archive existing = archiveRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("archive not found: " + id));
        if (!existing.getTitle().equals(dto.getTitle()) &&
                archiveRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("title already exists: " + dto.getTitle());
        }
        copier.copy(dto, existing, null);
        Archive entity = archiveRepository.save(existing);
        return ArchiveVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        if (!archiveRepository.existsById(id)) {
            throw new EntityNotFoundException("archive not found: " + id);
        }
        archiveRepository.deleteById(id);
        // 删除关联的章节
        List<Long> ids = sectionRepository.findAllByOwnerIdAndOwnerType(id, Section.OwnerType.ARCHIVE)
                .stream().map(Section::getId)
                .toList();
        sectionRepository.deleteAllById(ids);
    }

}
