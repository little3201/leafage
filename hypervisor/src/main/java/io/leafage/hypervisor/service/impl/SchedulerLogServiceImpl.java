package io.leafage.hypervisor.service.impl;

import io.leafage.hypervisor.domain.SchedulerLog;
import io.leafage.hypervisor.domain.vo.SchedulerLogVO;
import io.leafage.hypervisor.repository.SchedulerLogRepository;
import io.leafage.hypervisor.service.SchedulerLogService;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
                .orElse(null);
    }

    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        schedulerLogRepository.deleteById(id);
    }

    @Override
    public void clear() {
        schedulerLogRepository.deleteAll();
    }
}
