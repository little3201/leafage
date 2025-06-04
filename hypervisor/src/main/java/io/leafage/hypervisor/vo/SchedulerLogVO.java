package io.leafage.hypervisor.vo;


import io.leafage.hypervisor.bo.SchedulerLogBO;
import top.leafage.common.ReadonlyMetadata;

import java.time.Instant;

/**
 * vo class for scheduler_logs.
 *
 * @author wq li
 */
public class SchedulerLogVO extends SchedulerLogBO implements ReadonlyMetadata {

    private final Long id;

    private final boolean enabled;

    private final Instant lastModifiedDate;

    public SchedulerLogVO(Long id, boolean enabled, Instant lastModifiedDate) {
        this.id = id;
        this.enabled = enabled;
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }
}
