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

package top.leafage.hypervisor.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.domain.AuditLog;
import top.leafage.hypervisor.domain.vo.AuditLogVO;
import top.leafage.hypervisor.repository.AuditLogRepository;
import top.leafage.hypervisor.service.AuditLogService;

import java.util.NoSuchElementException;


/**
 * audit log service impl
 *
 * @author wq li
 */
@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    /**
     * <p>Constructor for AuditLogServiceImpl.</p>
     *
     * @param auditLogRepository a {@link AuditLogRepository} object
     */
    public AuditLogServiceImpl(AuditLogRepository auditLogRepository, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.auditLogRepository = auditLogRepository;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Page<AuditLogVO>> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        Criteria criteria = buildCriteria(filters, AuditLog.class);

        return r2dbcEntityTemplate.select(AuditLog.class)
                .matching(Query.query(criteria).with(pageable))
                .all()
                .map(AuditLogVO::from)
                .collectList()
                .zipWith(r2dbcEntityTemplate.count(Query.query(criteria), AuditLog.class))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<AuditLogVO> fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return auditLogRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .map(AuditLogVO::from);
    }

    @Override
    public Mono<Void> remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return auditLogRepository.existsById(id).flatMap(exists -> {
            if (exists) {
                return auditLogRepository.deleteById(id);
            }
            return Mono.error(new NoSuchElementException("audit log not found: " + id));
        });
    }
}
