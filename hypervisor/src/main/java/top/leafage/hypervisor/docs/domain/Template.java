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
 * entity class for templates.
 *
 * @author wq li
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "templates")
public class Template extends JpaAbstractAuditable<@NonNull String, @NonNull Long> {

    private String name;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Version
    private int version;

    @Enumerated(EnumType.STRING)
    private Status status = Status.DRAFT;

    private boolean enabled;


    public Template() {
    }

    public Template(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public enum Status {
        DRAFT,
        PUBLISHED,
        ARCHIVED;

        public static Status of(String value) {
            return valueOf(value.toUpperCase());
        }
    }

    public enum Type {
        WORD,
        EXCEL;

        public static Type of(String value) {
            return valueOf(value.toUpperCase());
        }
    }
}
