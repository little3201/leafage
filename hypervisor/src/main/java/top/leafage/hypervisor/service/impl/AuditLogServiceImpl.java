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

package top.leafage.hypervisor.service.impl;

import top.leafage.hypervisor.domain.AuditLog;
import top.leafage.hypervisor.domain.vo.AuditLogVO;
import top.leafage.hypervisor.repository.AuditLogRepository;
import top.leafage.hypervisor.service.AuditLogService;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * audit log service impl.
 *
 * @author wq li
 */
@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Constructor for AuditLogServiceImpl.
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
    public Page<@NonNull AuditLogVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<@NonNull AuditLog> spec = (root, query, cb) ->
                buildPredicate(filters, cb, root).orElse(null);

        return auditLogRepository.findAll(spec, pageable)
                .map(AuditLogVO::from);
    }

    @Override
    public AuditLogVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return auditLogRepository.findById(id)
                .map(AuditLogVO::from)
                .orElse(null);
    }

    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        auditLogRepository.deleteById(id);
    }

}
