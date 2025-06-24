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

package io.leafage.system.domain.superclass;



import org.springframework.data.relational.core.mapping.Column;

import java.time.Instant;

/**
 * superclass class for scheduler_logs.
 *
 * @author wq li
 */
public class SchedulerLogModel {

    private String name;

    @Column(value = "start_time")
    private Instant startTime;

    @Column(value = "executed_times")
    private Integer executedTimes;

    @Column(value = "next_execute_time")
    private Instant nextExecuteTime;

    private String description;


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
