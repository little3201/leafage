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

package top.leafage.hypervisor.docs.domain;

import jakarta.persistence.*;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import top.leafage.common.data.jpa.domain.JpaAbstractAuditable;

/**
 * entity class for section fields.
 *
 * @author wq li
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "section_fields")
public class SectionField extends JpaAbstractAuditable<@NonNull String, @NonNull Long> {

    private Long sectionId;

    private String name;

    private String field;

    @Enumerated(EnumType.STRING)
    private Type type;

    private Integer length;

    private boolean required = false;

    public SectionField() {
    }

    public SectionField(Long sectionId, String name, String field, Type type, Integer length, boolean required) {
        this.sectionId = sectionId;
        this.name = name;
        this.field = field;
        this.type = type;
        this.length = length;
        this.required = required;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
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

    public enum Type {
        STRING,
        NUMBER,
        BOOLEAN,
        DATE,
        DATETIME;

        public static Type of(String value) {
            return valueOf(value.toUpperCase());
        }
    }
}
