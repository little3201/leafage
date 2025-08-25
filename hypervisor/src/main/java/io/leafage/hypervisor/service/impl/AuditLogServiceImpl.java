/*
 *  Copyright 2018-2025 little3201.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.leafage.hypervisor.service.impl;

import io.leafage.hypervisor.repository.AuditLogRepository;
import io.leafage.hypervisor.service.AuditLogService;
import io.leafage.hypervisor.vo.AuditLogVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import top.leafage.common.DomainConverter;

/**
 * audit log service impl
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
    public Mono<Page<AuditLogVO>> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        return auditLogRepository.findAllBy(pageable)
                .map(a -> convertToVO(a, AuditLogVO.class))
                .collectList()
                .zipWith(auditLogRepository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<AuditLogVO> fetch(Long id) {
        Assert.notNull(id, "id must not be null.");

        return auditLogRepository.findById(id)
                .map(a -> convertToVO(a, AuditLogVO.class));
    }
}
