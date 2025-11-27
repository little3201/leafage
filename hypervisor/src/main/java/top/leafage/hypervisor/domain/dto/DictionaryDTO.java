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

package top.leafage.hypervisor.domain.dto;

import top.leafage.hypervisor.domain.Dictionary;
import jakarta.validation.constraints.NotBlank;

/**
 * dto class for dictionary.
 *
 * @author wq li
 */
public class DictionaryDTO {

    @NotBlank
    private String name;

    private Long superiorId;

    private String description;


    public static Dictionary toEntity(DictionaryDTO dto) {
        return new Dictionary(
                dto.getName(),
                dto.getSuperiorId(),
                dto.getDescription()
        );
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
}
