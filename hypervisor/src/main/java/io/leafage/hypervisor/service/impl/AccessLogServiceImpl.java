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

import io.leafage.hypervisor.domain.AccessLog;
import io.leafage.hypervisor.repository.AccessLogRepository;
import io.leafage.hypervisor.service.AccessLogService;
import io.leafage.hypervisor.vo.AccessLogVO;
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
 * access log service impl
 *
 * @author wq li
 */
@Service
public class AccessLogServiceImpl extends DomainConverter implements AccessLogService {

    private final AccessLogRepository accessLogRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    /**
     * <p>Constructor for AccessLogServiceImpl.</p>
     *
     * @param accessLogRepository a {@link AccessLogRepository} object
     */
    public AccessLogServiceImpl(AccessLogRepository accessLogRepository, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.accessLogRepository = accessLogRepository;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Page<AccessLogVO>> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        Criteria criteria = buildCriteria(filters, AccessLog.class);

        return r2dbcEntityTemplate.select(AccessLog.class)
                .matching(Query.query(criteria).with(pageable))
                .all()
                .map(log -> convertToVO(log, AccessLogVO.class))
                .collectList()
                .zipWith(r2dbcEntityTemplate.count(Query.query(criteria), AccessLog.class))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<AccessLogVO> fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return accessLogRepository.findById(id)
                .map(a -> convertToVO(a, AccessLogVO.class));
    }
}
