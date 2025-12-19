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
package top.leafage.hypervisor.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.leafage.hypervisor.system.domain.GroupRoles;

import java.util.List;

/**
 * group members repository.
 *
 * @author wq li
 */
@Repository
public interface GroupRolesRepository extends JpaRepository<GroupRoles, Long> {

    /**
     * find by role id.
     *
     * @param roleId role id
     * @return the result.
     */
    List<GroupRoles> findAllByRoleId(Long roleId);

    /**
     * find by group id.
     *
     * @param groupId group id
     * @return the result.
     */
    List<GroupRoles> findAllByGroupId(Long groupId);
}
