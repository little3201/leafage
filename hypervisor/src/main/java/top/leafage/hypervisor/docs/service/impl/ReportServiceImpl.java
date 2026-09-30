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
import top.leafage.hypervisor.docs.domain.Report;
import top.leafage.hypervisor.docs.domain.Section;
import top.leafage.hypervisor.docs.domain.dto.ReportDTO;
import top.leafage.hypervisor.docs.domain.vo.ReportVO;
import top.leafage.hypervisor.docs.repository.ReportRepository;
import top.leafage.hypervisor.docs.repository.SectionRepository;
import top.leafage.hypervisor.docs.service.ReportService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static top.leafage.hypervisor.constants.GlobalConstant.ID_MUST_NOT_BE_NULL;

/**
 * report service impl
 *
 * @author wq li
 */
@OperationLog("reports")
@Service
public class ReportServiceImpl implements ReportService {

    private static final BeanCopier copier = BeanCopier.create(ReportDTO.class, Report.class, false);

    private final ReportRepository reportRepository;
    private final SectionRepository sectionRepository;

    /**
     * Constructor for ReportServiceImpl.
     *
     * @param reportRepository  a {@link ReportRepository} object
     * @param sectionRepository a {@link SectionRepository} object
     */
    public ReportServiceImpl(ReportRepository reportRepository, SectionRepository sectionRepository) {
        this.reportRepository = reportRepository;
        this.sectionRepository = sectionRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<ReportVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<Report> spec = (root, _, cb) ->
                buildPredicate(filters, cb, root).orElse(null);

        return reportRepository.findAll(spec, pageable)
                .map(ReportVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReportVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return reportRepository.findById(id)
                .map(ReportVO::from)
                .orElseThrow(() -> new EntityNotFoundException("report not found: " + id));
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public ReportVO create(ReportDTO dto) {
        if (reportRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("title already exists: " + dto.getTitle());
        }
        Report entity = reportRepository.save(ReportDTO.toEntity(dto));
        // 执行模板内容复制
        Long schemaId = dto.getSchemaId();
        if (schemaId != null) {
            copySections(schemaId, entity.getId());
        }
        return ReportVO.from(entity);
    }

    private void copySections(Long schemaId, Long reportId) {
        List<Section> templateSections = sectionRepository.findAllByOwnerIdAndOwnerType(schemaId, Section.OwnerType.REPORT);
        List<Section> copiedSections = templateSections.stream()
                .map(section -> new Section(reportId, Section.OwnerType.REPORT, section))
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
    public ReportVO modify(Long id, ReportDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        Report existing = reportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("report not found: " + id));
        if (!existing.getTitle().equals(dto.getTitle()) &&
                reportRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("title already exists: " + dto.getTitle());
        }
        copier.copy(dto, existing, null);
        Report entity = reportRepository.save(existing);
        return ReportVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        if (!reportRepository.existsById(id)) {
            throw new EntityNotFoundException("report not found: " + id);
        }
        reportRepository.deleteById(id);
        // 删除关联的章节
        List<Long> ids = sectionRepository.findAllByOwnerIdAndOwnerType(id, Section.OwnerType.REPORT)
                .stream().map(Section::getId)
                .toList();
        sectionRepository.deleteAllById(ids);
    }

    @Override
    public byte[] generate(Long id) {
        return new byte[0];
    }

    @Override
    public String preview(Long id) {
        return "";
    }
}
