package io.leafage.hypervisor.service.impl;

import io.leafage.hypervisor.domain.SchedulerLog;
import io.leafage.hypervisor.repository.SchedulerLogRepository;
import io.leafage.hypervisor.service.SchedulerLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

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
    public Page<SchedulerLog> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<SchedulerLog> spec = (root, query, cb) ->
                buildPredicate(filters, cb, root).orElse(null);

        return schedulerLogRepository.findAll(spec, pageable);
    }

    @Override
    public List<SchedulerLog> retrieve(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return schedulerLogRepository.findAll();
        } else {
            return schedulerLogRepository.findAllById(ids);
        }
    }

    @Override
    public Optional<SchedulerLog> fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return schedulerLogRepository.findById(id);
    }

    @Override
    public boolean exists(String name, Long id) {
        Assert.hasText(name, String.format(_MUST_NOT_BE_EMPTY, "name"));

        if (id == null) {
            return schedulerLogRepository.existsByName(name);
        }
        return schedulerLogRepository.existsByNameAndIdNot(name, id);
    }

    @Override
    public SchedulerLog create(SchedulerLog entity) {
        return schedulerLogRepository.saveAndFlush(entity);
    }

    @Override
    public List<SchedulerLog> createAll(Iterable<SchedulerLog> iterable) {
        return schedulerLogRepository.saveAll(iterable);
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
