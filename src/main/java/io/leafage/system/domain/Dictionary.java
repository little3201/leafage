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

package io.leafage.system.domain;

import top.leafage.common.AuditMetadata;
import io.leafage.system.domain.superclass.DictionaryModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

/**
 * entity class for dictionary.
 *
 * @author wq li
 */
@Table(name = "dictionaries")
public class Dictionary extends DictionaryModel {

    /**
     * Primary key.
     */
    @Id
    private Long id;

    @Embedded.Nullable
    private AuditMetadata auditMetadata = new AuditMetadata();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuditMetadata getAuditMetadata() {
        return auditMetadata;
    }

    public void setAuditMetadata(AuditMetadata auditMetadata) {
        this.auditMetadata = auditMetadata;
    }
}
