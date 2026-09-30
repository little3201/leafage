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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import top.leafage.common.data.jpa.domain.JpaAbstractAuditable;

import java.util.Map;

/**
 * entity class for sections.
 *
 * @author wq li
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sections")
public class Section extends JpaAbstractAuditable<@NonNull String, @NonNull Long> {

    private Long superiorId;

    // 归属对象主键（template, report）
    private Long ownerId;

    @Enumerated(EnumType.STRING)
    private OwnerType ownerType;

    private String name;

    private Integer sequence;

    private Integer level;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> body;

    private boolean enabled = true;


    public Section() {
    }

    public Section(Long ownerId, OwnerType ownerType, Section section) {
        this.ownerId = ownerId;
        this.ownerType = ownerType;
        this.superiorId = section.getSuperiorId();
        this.name = section.getName();
        this.sequence = section.getSequence();
        this.level = section.getLevel();
        this.body = section.getBody();
    }

    public Section(Long superiorId, Long ownerId, OwnerType ownerType, String name, Integer sequence, Integer level, Map<String, Object> body) {
        this.superiorId = superiorId;
        this.ownerId = ownerId;
        this.ownerType = ownerType;
        this.name = name;
        this.sequence = sequence;
        this.level = level;
        this.body = body;
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

    public OwnerType getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(OwnerType ownerType) {
        this.ownerType = ownerType;
    }

    public String getName() {
        return name;
    }

    public void setName(String title) {
        this.name = title;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

   /* public enum Type {
        HEADING,      // 标题
        PARAGRAPH,    // 段落
        TABLE,        // 表格
        IMAGE;        // 图片

        public static Type of(String value) {
            return valueOf(value.toUpperCase());
        }
    }*/

    public enum OwnerType {
        ARCHIVE,
        TEMPLATE,
        REPORT;

        public static OwnerType of(String value) {
            return valueOf(value.toUpperCase());
        }
    }
}
