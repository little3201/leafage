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

import io.leafage.hypervisor.domain.AuditLog;
import io.leafage.hypervisor.domain.Group;
import io.leafage.hypervisor.dto.AuditLogDTO;
import io.leafage.hypervisor.repository.AuditLogRepository;
import io.leafage.hypervisor.service.AuditLogService;
import io.leafage.hypervisor.vo.AuditLogVO;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.leafage.common.DomainConverter;

/**
 * audit log service impl.
 *
 * @author wq li
 */
@Service
public class AuditLogServiceImpl extends DomainConverter implements AuditLogService {

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
    public Page<AuditLogVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<AuditLog> spec = (root, query, cb) ->
                parseFilters(filters, cb, root).orElse(null);

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
