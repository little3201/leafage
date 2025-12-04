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
import top.leafage.hypervisor.domain.OperationLog;
import top.leafage.hypervisor.domain.vo.OperationLogVO;
import top.leafage.hypervisor.repository.OperationLogRepository;
import top.leafage.hypervisor.service.OperationLogService;

import java.util.NoSuchElementException;


/**
 * operation log service impl
 *
 * @author wq li
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {

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
                .map(OperationLogVO::from)
                .collectList()
                .zipWith(r2dbcEntityTemplate.count(Query.query(criteria), OperationLog.class))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<OperationLogVO> fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return operationLogRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .map(OperationLogVO::from);
    }

    @Override
    public Mono<Void> remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return operationLogRepository.existsById(id).flatMap(exists -> {
            if (exists) {
                return operationLogRepository.deleteById(id);
            }
            throw new NoSuchElementException("operation log not found: " + id);
        });
    }
}
