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
package top.leafage.hypervisor.system.domain;

import jakarta.persistence.*;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.Set;

/**
 * entity class for user privileges.
 *
 * @author wq li
 */
@Entity
@Table(name = "user_privileges")
public class UserPrivileges extends AbstractPersistable<@NonNull Long> {

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Long privilegeId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_privilege_actions", joinColumns = @JoinColumn(name = "user_privilege_id"))
    private Set<String> actions;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getPrivilegeId() {
        return privilegeId;
    }

    public void setPrivilegeId(Long privilegeId) {
        this.privilegeId = privilegeId;
    }

    public Set<String> getActions() {
        return actions;
    }

    public void setActions(Set<String> actions) {
        this.actions = actions;
    }
}
