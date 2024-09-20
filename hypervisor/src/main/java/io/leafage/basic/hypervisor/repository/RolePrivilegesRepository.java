/*
 *  Copyright 2018-2024 little3201.
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
package io.leafage.basic.hypervisor.repository;

import io.leafage.basic.hypervisor.domain.RolePrivileges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * role privileges repository.
 *
 * @author wq li
 */
@Repository
public interface RolePrivilegesRepository extends JpaRepository<RolePrivileges, Long> {

    /**
     * 根据role查privilege
     *
     * @param roleId privilege主键
     * @return 关联数据集
     */
    List<RolePrivileges> findByRoleId(Long roleId);

    /**
     * 根据privilege查role
     *
     * @param privilegeId privilege主键
     * @return 关联数据集
     */
    List<RolePrivileges> findByPrivilegeId(Long privilegeId);

}
