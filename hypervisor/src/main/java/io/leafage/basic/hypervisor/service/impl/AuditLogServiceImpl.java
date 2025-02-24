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

import io.leafage.basic.hypervisor.domain.AuditLog;
import io.leafage.basic.hypervisor.dto.AuditLogDTO;
import io.leafage.basic.hypervisor.repository.AuditLogRepository;
import io.leafage.basic.hypervisor.service.AuditLogService;
import io.leafage.basic.hypervisor.vo.AuditLogVO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * audit log service impl.
 *
 * @author wq li
 */
@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * <p>Constructor for AuditLogServiceImpl.</p>
     *
     * @param auditLogRepository a {@link AuditLogRepository} object
     */
    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<AuditLogVO> retrieve(int page, int size, String sortBy, boolean descending, String operation) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<AuditLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(operation)) {
                predicates.add(cb.like(root.get("operation"), "%" + operation + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return auditLogRepository.findAll(spec, pageable)
                .map(auditLog -> convertToVO(auditLog, AuditLogVO.class));
    }

    @Override
    public AuditLogVO fetch(Long id) {
        Assert.notNull(id, "id must not be null.");

        return auditLogRepository.findById(id)
                .map(auditLog -> convertToVO(auditLog, AuditLogVO.class)).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuditLogVO create(AuditLogDTO dto) {
        AuditLog auditLog = new AuditLog();
        BeanCopier copier = BeanCopier.create(AuditLogDTO.class, AuditLog.class, false);
        copier.copy(dto, auditLog, null);

        auditLogRepository.saveAndFlush(auditLog);
        return convertToVO(auditLog, AuditLogVO.class);
    }

    @Override
    public void remove(Long id) {
        Assert.notNull(id, "id must not be null.");
        auditLogRepository.deleteById(id);
    }

}
