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
import top.leafage.hypervisor.docs.domain.SectionField;

/**
 * dto class for section field.
 *
 * @author wq li
 */
public class SectionFieldDTO {

    @NotNull
    private Long sectionId;

    @NotBlank
    private String name;

    @NotBlank
    private String field;

    @NotBlank
    private SectionField.Type type;

    @Min(1)
    @Max(500)
    private Integer length;

    private boolean required;


    public static SectionField toEntity(SectionFieldDTO dto) {
        return new SectionField(
                dto.getSectionId(),
                dto.getName(),
                dto.getField(),
                dto.getType(),
                dto.getLength(),
                dto.isRequired()
        );
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public SectionField.Type getType() {
        return type;
    }

    public void setType(SectionField.Type type) {
        this.type = type;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
