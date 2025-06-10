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
 * entity class for group privileges.
 *
 * @author wq li
 */
@Entity
@Table(name = "group_privileges")
public class GroupPrivileges {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * group主键
     */
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    /**
     * privilege id
     */
    @Column(name = "privilege_id", nullable = false)
    private Long privilegeId;

    /**
     * 操作
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "group_privilege_actions", joinColumns = @JoinColumn(name = "group_privilege_id"))
    @Column(name = "action")
    private Set<String> actions;

    public GroupPrivileges() {

    }

    public GroupPrivileges(Long groupId, Long privilegeId, Set<String> actions) {
        this.groupId = groupId;
        this.privilegeId = privilegeId;
        this.actions = actions;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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
