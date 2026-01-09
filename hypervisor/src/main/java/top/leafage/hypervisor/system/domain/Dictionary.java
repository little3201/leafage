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

/**
 * entity class for dictionary.
 *
 * @author wq li
 */
@Table(name = "dictionaries")
public class Dictionary extends AbstractAuditable<@NonNull String, @NonNull Long> {

    private String name;

    private Long superiorId;

    private String description;

    private boolean enabled = true;

    public Dictionary() {
    }

    public Dictionary(String name, Long superiorId, String description) {
        this.name = name;
        this.superiorId = superiorId;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSuperiorId() {
        return superiorId;
    }

    public void setSuperiorId(Long superiorId) {
        this.superiorId = superiorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
