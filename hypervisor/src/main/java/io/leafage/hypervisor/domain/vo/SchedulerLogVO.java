package io.leafage.hypervisor.domain.vo;


import io.leafage.hypervisor.domain.SchedulerLog;

import java.time.Instant;

/**
 * vo class for scheduler_logs.
 *
 * @author wq li
 */
public record SchedulerLogVO(
        Long id,
        String name,
        Instant startTime,
        Integer executedTimes,
        Instant nextExecuteTime,
        String description
) {
    public static SchedulerLogVO from(SchedulerLog entity) {
        return new SchedulerLogVO(
                entity.getId(),
                entity.getName(),
                entity.getStartTime(),
                entity.getExecutedTimes(),
                entity.getNextExecuteTime(),
                entity.getDescription()
        );
    }
}
