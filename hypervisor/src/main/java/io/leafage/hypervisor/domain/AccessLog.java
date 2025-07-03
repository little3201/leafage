/*
 * Copyright (c) 2024-2025.  little3201.
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

package io.leafage.hypervisor.domain;


import io.leafage.hypervisor.domain.superclass.AccessLogModel;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import top.leafage.common.jpa.JpaAuditMetadata;

/**
 * entity class for access log.
 *
 * @author wq li
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "access_logs")
public class AccessLog extends AccessLogModel {

    /**
     * Primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private JpaAuditMetadata auditMetadata = new JpaAuditMetadata();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JpaAuditMetadata getAuditMetadata() {
        return auditMetadata;
    }

    public void setAuditMetadata(JpaAuditMetadata auditMetadata) {
        this.auditMetadata = auditMetadata;
    }
}
