/*
 * Copyright (c) 2026.  little3201.
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

package top.leafage.hypervisor.system.domain;

import org.jspecify.annotations.NonNull;
import org.springframework.data.relational.core.mapping.Table;
import top.leafage.common.data.domain.AbstractAuditable;

import java.time.Instant;

/**
 * entity class for scheduler log.
 *
 * @author wq li
 */
@Table(name = "scheduler_logs")
public class SchedulerLog extends AbstractAuditable<@NonNull String, @NonNull Long> {

    private String name;

    private Instant startTime;

    private Integer executedTimes;

    private Instant nextExecuteTime;

    private String record;

    private ScheduleStatus status;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
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

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public ScheduleStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduleStatus status) {
        this.status = status;
    }


    public enum ScheduleStatus {
        PENDING, RUNNING, SUCCESS, FAILED, CANCELED
    }
}
