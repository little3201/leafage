package io.leafage.hypervisor.vo;


import io.leafage.hypervisor.domain.superclass.SchedulerLogModel;

/**
 * vo class for scheduler_logs.
 *
 * @author wq li
 */
public class SchedulerLogVO extends SchedulerLogModel {

    private Long id;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
