/*
 * Copyright (c) 2025.  little3201.
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

package top.leafage.hypervisor.system.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import top.leafage.hypervisor.system.domain.SchedulerLog;
import top.leafage.hypervisor.system.domain.vo.SchedulerLogVO;
import top.leafage.hypervisor.system.repository.SchedulerLogRepository;
import top.leafage.hypervisor.system.service.SchedulerLogService;

/**
 * service for scheduler_logs.
 *
 * @author wq li
 */
@Service
public class SchedulerLogServiceImpl implements SchedulerLogService {

    private final SchedulerLogRepository schedulerLogRepository;

    public SchedulerLogServiceImpl(SchedulerLogRepository schedulerLogRepository) {
        this.schedulerLogRepository = schedulerLogRepository;
    }

    @Override
    public Page<@NonNull SchedulerLogVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<@NonNull SchedulerLog> spec = (root, query, cb) ->
                buildPredicate(filters, cb, root).orElse(null);

        return schedulerLogRepository.findAll(spec, pageable)
                .map(SchedulerLogVO::from);
    }

    @Override
    public SchedulerLogVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return schedulerLogRepository.findById(id)
                .map(SchedulerLogVO::from)
                .orElseThrow(() -> new EntityNotFoundException("scheduler log not found: " + id));
    }

    @Transactional
    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        if (!schedulerLogRepository.existsById(id)) {
            throw new EntityNotFoundException("scheduler log not found: " + id);
        }
        schedulerLogRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void clear() {
        schedulerLogRepository.deleteAll();
    }
}
