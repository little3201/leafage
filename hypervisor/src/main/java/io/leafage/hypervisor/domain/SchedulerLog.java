/*
 *  Copyright 2018-2025 little3201.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.leafage.hypervisor.domain;

import io.leafage.hypervisor.domain.superclass.SchedulerLogModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;
import top.leafage.common.r2dbc.audit.R2dbcAuditMetadata;

/**
 * entity class for scheduler log.
 *
 * @author wq li
 */
@Table(name = "scheduler_logs")
public class SchedulerLog extends SchedulerLogModel {

    /**
     * Primary key.
     */
    @Id
    private Long id;

    private ScheduleStatus status;

    @Embedded.Nullable
    private R2dbcAuditMetadata auditMetadata = new R2dbcAuditMetadata();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public R2dbcAuditMetadata getAuditMetadata() {
        return auditMetadata;
    }

    public void setAuditMetadata(R2dbcAuditMetadata auditMetadata) {
        this.auditMetadata = auditMetadata;
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
