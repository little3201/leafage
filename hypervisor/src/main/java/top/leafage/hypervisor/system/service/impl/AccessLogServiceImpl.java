/*
 *
 *  * Copyright(c) 2019-present the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *       https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package top.leafage.hypervisor.system.service.impl;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import top.leafage.hypervisor.system.domain.AccessLog;
import top.leafage.hypervisor.system.domain.vo.AccessLogVO;
import top.leafage.hypervisor.system.repository.AccessLogRepository;
import top.leafage.hypervisor.system.service.AccessLogService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.data.relational.core.query.Query.query;
import static top.leafage.hypervisor.constants.GlobalConstant.ID_MUST_NOT_BE_NULL;

/**
 * access log service impl.
 *
 * @author wq li
 */
@Service
public class AccessLogServiceImpl implements AccessLogService {

    private final AccessLogRepository accessLogRepository;
    private final JdbcAggregateTemplate jdbcAggregateTemplate;

    /**
     * Constructor for AccessLogServiceImpl.
     *
     * @param accessLogRepository a {@link AccessLogRepository} object
     */
    public AccessLogServiceImpl(AccessLogRepository accessLogRepository, JdbcAggregateTemplate jdbcAggregateTemplate) {
        this.accessLogRepository = accessLogRepository;
        this.jdbcAggregateTemplate = jdbcAggregateTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<@NonNull AccessLogVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        Criteria criteria = buildCriteria(filters, AccessLog.class);

        List<AccessLogVO> voList = jdbcAggregateTemplate.findAll(query(criteria).with(pageable), AccessLog.class)
                .stream().map(AccessLogVO::from)
                .toList();
        return new PageImpl<>(voList, pageable, jdbcAggregateTemplate.count(query(criteria), AccessLog.class));
    }

    @Override
    public AccessLogVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return accessLogRepository.findById(id)
                .map(AccessLogVO::from)
                .orElseThrow(() -> new NoSuchElementException("access log not found: " + id));
    }

    @Transactional
    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        if (!accessLogRepository.existsById(id)) {
            throw new NoSuchElementException("access log not found: " + id);
        }
        accessLogRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void clear() {
        accessLogRepository.deleteAll();
    }
}
