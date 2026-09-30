/*
 * Copyright(c) 2019-present the original author or authors.
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

package top.leafage.hypervisor.docs.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import top.leafage.hypervisor.docs.domain.Section;

import java.util.Map;

/**
 * dto class for section.
 *
 * @author wq li
 */
public class SectionDTO {

    private Long superiorId;

    @NotNull
    private Long ownerId;

    @NotNull
    private Section.OwnerType ownerType;

    @NotBlank
    private String name;

    private Integer sequence;

    @Min(1)
    @Max(6)
    private Integer level;

    private Map<String, Object> body;


    public static Section toEntity(SectionDTO dto) {
        return new Section(
                dto.getSuperiorId(),
                dto.getOwnerId(),
                dto.getOwnerType(),
                dto.getName(),
                dto.getSequence(),
                dto.getLevel(),
                dto.getBody()
        );
    }


    public Long getSuperiorId() {
        return superiorId;
    }

    public void setSuperiorId(Long superiorId) {
        this.superiorId = superiorId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Section.OwnerType getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(Section.OwnerType ownerType) {
        this.ownerType = ownerType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

}
