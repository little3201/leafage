package io.leafage.hypervisor.bo;

import java.time.Instant;

/**
 * bo class for scheduler_logs.
 *
 * @author wq li
 */
public class SchedulerLogBO {


    /**
     * The name of the scheduler_logs.
     */
    private String name;

    /**
     * The record of the scheduler_logs.
     */
    private String record;

    /**
     * The executed_times of the scheduler_logs.
     */
    private Integer executedTimes;

    /**
     * The next_execute_time of the scheduler_logs.
     */
    private Instant nextExecuteTime;

    /**
     * The start_time of the scheduler_logs.
     */
    private Instant startTime;

    /**
     * The status of the scheduler_logs.
     */
    private String status;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public Integer getExecutedTimes() {
        return executedTimes;
    }

    public void setExecutedTimes(Integer executedTimes) {
        this.executedTimes = executedTimes;
    }

    public Instant getNextExecuteTime() {
        return nextExecuteTime;
    }

    public void setNextExecuteTime(Instant nextExecuteTime) {
        this.nextExecuteTime = nextExecuteTime;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
