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

import io.leafage.hypervisor.domain.OperationLog;
import io.leafage.hypervisor.repository.OperationLogRepository;
import io.leafage.hypervisor.service.OperationLogService;
import io.leafage.hypervisor.vo.OperationLogVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import top.leafage.common.DomainConverter;

/**
 * operation log service impl
 *
 * @author wq li
 */
@Service
public class OperationLogServiceImpl extends DomainConverter implements OperationLogService {

    private final OperationLogRepository operationLogRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    /**
     * <p>Constructor for OperationLogServiceImpl.</p>
     *
     * @param operationLogRepository a {@link OperationLogRepository} object
     */
    public OperationLogServiceImpl(OperationLogRepository operationLogRepository, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.operationLogRepository = operationLogRepository;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Page<OperationLogVO>> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        Criteria criteria = buildCriteria(filters, OperationLog.class);

        return r2dbcEntityTemplate.select(OperationLog.class)
                .matching(Query.query(criteria).with(pageable))
                .all()
                .map(log -> convertToVO(log, OperationLogVO.class))
                .collectList()
                .zipWith(r2dbcEntityTemplate.count(Query.query(criteria), OperationLog.class))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<OperationLogVO> fetch(Long id) {
        Assert.notNull(id, "id must not be null.");

        return operationLogRepository.findById(id)
                .map(o -> convertToVO(o, OperationLogVO.class));
    }
}
