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

package io.leafage.assets.domain.dto;

import io.leafage.assets.domain.Post;
import io.leafage.assets.domain.Region;
import jakarta.validation.constraints.NotBlank;

/**
 * dto class for region.
 *
 * @author wq li
 */
public class RegionDTO {

    @NotBlank
    private String name;

    private Long superiorId;

    private String areaCode;

    private Integer postalCode;

    private String description;


    public static Region toEntity(RegionDTO dto) {
        return new Region(
                dto.getName(),
                dto.getSuperiorId(),
                dto.getAreaCode(),
                dto.getPostalCode(),
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

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public Integer getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(Integer postalCode) {
        this.postalCode = postalCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
