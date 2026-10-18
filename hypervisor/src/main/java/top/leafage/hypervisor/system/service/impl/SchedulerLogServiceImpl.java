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
import top.leafage.hypervisor.system.domain.Message;
import top.leafage.hypervisor.system.domain.SchedulerLog;
import top.leafage.hypervisor.system.domain.vo.SchedulerLogVO;
import top.leafage.hypervisor.system.repository.SchedulerLogRepository;
import top.leafage.hypervisor.system.service.SchedulerLogService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.data.relational.core.query.Query.query;
import static top.leafage.hypervisor.constants.GlobalConstant.ID_MUST_NOT_BE_NULL;

/**
 * service for scheduler_logs.
 *
 * @author wq li
 */
@Service
public class SchedulerLogServiceImpl implements SchedulerLogService {

    private final SchedulerLogRepository schedulerLogRepository;
    private final JdbcAggregateTemplate jdbcAggregateTemplate;

    public SchedulerLogServiceImpl(SchedulerLogRepository schedulerLogRepository, JdbcAggregateTemplate jdbcAggregateTemplate) {
        this.schedulerLogRepository = schedulerLogRepository;
        this.jdbcAggregateTemplate = jdbcAggregateTemplate;
    }

    @Override
    public Page<SchedulerLogVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        Criteria criteria = buildCriteria(filters, SchedulerLog.class);

        List<SchedulerLogVO> voList = jdbcAggregateTemplate.findAll(query(criteria).with(pageable), SchedulerLog.class)
                .stream().map(SchedulerLogVO::from)
                .toList();
        return new PageImpl<>(voList, pageable, jdbcAggregateTemplate.count(query(criteria), SchedulerLog.class));
    }

    @Override
    public SchedulerLogVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return schedulerLogRepository.findById(id)
                .map(SchedulerLogVO::from)
                .orElseThrow(() -> new NoSuchElementException("scheduler log not found: " + id));
    }

    @Transactional
    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        if (!schedulerLogRepository.existsById(id)) {
            throw new NoSuchElementException("scheduler log not found: " + id);
        }
        schedulerLogRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void clear() {
        schedulerLogRepository.deleteAll();
    }
}
