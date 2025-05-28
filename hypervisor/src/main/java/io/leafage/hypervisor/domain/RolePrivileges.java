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
package io.leafage.hypervisor.domain;

import jakarta.persistence.*;

import java.util.Set;

/**
 * model class for role privileges.
 *
 * @author wq li
 */
@Entity
@Table(name = "role_privileges")
public class RolePrivileges {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * role主键
     */
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    /**
     * privilege id
     */
    @Column(name = "privilege_id", nullable = false)
    private Long privilegeId;

    /**
     * 操作
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "role_privilege_actions", joinColumns = @JoinColumn(name = "role_privilege_id"))
    @Column(name = "action")
    private Set<String> actions;

    public RolePrivileges() {
    }

    public RolePrivileges(Long roleId, Long privilegeId, Set<String> actions) {
        this.roleId = roleId;
        this.privilegeId = privilegeId;
        this.actions = actions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
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
