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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * entity class for group roles.
 *
 * @author wq li
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "group_roles")
public class GroupRoles {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * group id
     */
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    /**
     * role id
     */
    @Column(name = "role_id", nullable = false)
    private Long roleId;


    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link Long} object
     */
    public Long getId() {
        return id;
    }

    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a {@link Long} object
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * <p>Getter for the field <code>groupId</code>.</p>
     *
     * @return a {@link Long} object
     */
    public Long getGroupId() {
        return groupId;
    }

    /**
     * <p>Setter for the field <code>groupId</code>.</p>
     *
     * @param groupId a {@link Long} object
     */
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    /**
     * <p>Getter for the field <code>roleId</code>.</p>
     *
     * @return a {@link String} object
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * <p>Setter for the field <code>roleId</code>.</p>
     *
     * @param roleId a {@link String} object
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
