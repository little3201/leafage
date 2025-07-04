package io.leafage.hypervisor.service.impl;

import io.leafage.hypervisor.domain.SchedulerLog;
import io.leafage.hypervisor.dto.SchedulerLogDTO;
import io.leafage.hypervisor.repository.SchedulerLogRepository;
import io.leafage.hypervisor.service.SchedulerLogService;
import io.leafage.hypervisor.vo.SchedulerLogVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import top.leafage.common.DomainConverter;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * service for scheduler_logs.
 *
 * @author wq li
 */
@Service
public class SchedulerLogServiceImpl extends DomainConverter implements SchedulerLogService {

    private final SchedulerLogRepository schedulerLogRepository;

    public SchedulerLogServiceImpl(SchedulerLogRepository schedulerLogRepository) {
        this.schedulerLogRepository = schedulerLogRepository;
    }

    @Override
    public Page<SchedulerLogVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<SchedulerLog> spec = (root, query, cb) ->
                buildPredicate(filters, cb, root).orElse(null);

        return schedulerLogRepository.findAll(spec, pageable)
                .map(schedulerLog -> convertToVO(schedulerLog, SchedulerLogVO.class));
    }

    @Override
    public List<SchedulerLogVO> retrieve(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return schedulerLogRepository.findAll().stream()
                    .map(schedulerLog -> convertToVO(schedulerLog, SchedulerLogVO.class)).toList();
        } else {
            return schedulerLogRepository.findAllById(ids).stream()
                    .map(schedulerLog -> convertToVO(schedulerLog, SchedulerLogVO.class)).toList();
        }
    }

    @Override
    public SchedulerLogVO fetch(Long id) {
        return schedulerLogRepository.findById(id)
                .map(schedulerLog -> convertToVO(schedulerLog, SchedulerLogVO.class)).orElse(null);
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
    public SchedulerLogVO create(SchedulerLogDTO dto) {
        SchedulerLog schedulerLog = convertToDomain(dto, SchedulerLog.class);
        schedulerLogRepository.saveAndFlush(schedulerLog);
        return convertToVO(schedulerLog, SchedulerLogVO.class);
    }

    @Override
    public List<SchedulerLogVO> createAll(Iterable<SchedulerLogDTO> iterable) {
        List<SchedulerLog> list = StreamSupport.stream(iterable.spliterator(), false).map(dto -> convertToDomain(dto, SchedulerLog.class)).toList();
        return schedulerLogRepository.saveAll(list).stream()
                .map(schedulerLog -> convertToVO(schedulerLog, SchedulerLogVO.class)).toList();
    }

    @Override
    public SchedulerLogVO modify(Long id, SchedulerLogDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return schedulerLogRepository.findById(id)
                .map(existing -> {
                    SchedulerLog schedulerLog = convert(dto, existing);
                    schedulerLog = schedulerLogRepository.save(schedulerLog);
                    return convertToVO(schedulerLog, SchedulerLogVO.class);
                }).orElseThrow();
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
