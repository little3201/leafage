package top.leafage.hypervisor.domain.vo;


import top.leafage.hypervisor.domain.SchedulerLog;

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
        String status,
        Instant nextExecuteTime,
        String record
) {
    public static SchedulerLogVO from(SchedulerLog entity) {
        return new SchedulerLogVO(
                entity.getId(),
                entity.getName(),
                entity.getStartTime(),
                entity.getExecutedTimes(),
                entity.getStatus().name(),
                entity.getNextExecuteTime(),
                entity.getRecord()
        );
    }
}