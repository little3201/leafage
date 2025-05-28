/*
 *  Copyright 2018-2025 little3201.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.leafage.hypervisor.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

/**
 * model class for group privileges
 *
 * @author wq li
 */
@Table(name = "group_privileges")
public class GroupPrivileges {

    /**
     * 主键
     */
    @Id
    private Long id;
    /**
     * group主键
     */
    @Column(value = "group_id")
    private Long groupId;
    /**
     * privilege主键
     */
    @Column(value = "privilege_id")
    private Long privilegeId;
    /**
     * actions
     */
    private Set<String> actions;

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.Long} object
     */
    public Long getId() {
        return id;
    }

    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a {@link java.lang.Long} object
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * <p>Getter for the field <code>groupId</code>.</p>
     *
     * @return a {@link java.lang.Long} object
     */
    public Long getGroupId() {
        return groupId;
    }

    /**
     * <p>Setter for the field <code>groupId</code>.</p>
     *
     * @param groupId a {@link java.lang.Long} object
     */
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    /**
     * <p>Getter for the field <code>privilegeId</code>.</p>
     *
     * @return a {@link java.lang.Long} object
     */
    public Long getPrivilegeId() {
        return privilegeId;
    }

    /**
     * <p>Setter for the field <code>privilegeId</code>.</p>
     *
     * @param privilegeId a {@link java.lang.Long} object
     */
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
