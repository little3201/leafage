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
import top.leafage.hypervisor.system.domain.RolePrivileges;

import java.util.List;
import java.util.Optional;

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
    List<RolePrivileges> findAllByRoleId(Long roleId);

    /**
     * 根据role查privilege
     *
     * @param roleId privilege主键
     * @return 关联数据集
     */
    Optional<RolePrivileges> findByRoleIdAndPrivilegeId(Long roleId, Long privilegeId);

    /**
     * 根据privilege查role
     *
     * @param privilegeId privilege主键
     * @return 关联数据集
     */
    List<RolePrivileges> findAllByPrivilegeId(Long privilegeId);

}
