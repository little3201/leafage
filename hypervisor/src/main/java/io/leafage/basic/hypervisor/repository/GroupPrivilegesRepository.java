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
package io.leafage.basic.hypervisor.repository;

import io.leafage.basic.hypervisor.domain.GroupPrivileges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * group privileges repository.
 *
 * @author wq li
 */
@Repository
public interface GroupPrivilegesRepository extends JpaRepository<GroupPrivileges, Long> {

    /**
     * 根据group查privilege
     *
     * @param groupId privilege主键
     * @return 关联数据集
     */
    List<GroupPrivileges> findAllByGroupId(Long groupId);

    /**
     * 根据group查privilege
     *
     * @param groupId privilege主键
     * @return 关联数据集
     */
    Optional<GroupPrivileges> findByGroupIdAndPrivilegeId(Long groupId, Long privilegeId);

    /**
     * 根据privilege查group
     *
     * @param privilegeId privilege主键
     * @return 关联数据集
     */
    List<GroupPrivileges> findAllByPrivilegeId(Long privilegeId);

}
