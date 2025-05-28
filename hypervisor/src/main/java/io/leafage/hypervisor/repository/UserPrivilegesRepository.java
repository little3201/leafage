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
package io.leafage.hypervisor.repository;

import io.leafage.hypervisor.domain.UserPrivileges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * user privileges repository.
 *
 * @author wq li
 */
@Repository
public interface UserPrivilegesRepository extends JpaRepository<UserPrivileges, Long> {

    /**
     * 根据username查privilege
     *
     * @param username 账号
     * @return 关联数据集
     */
    List<UserPrivileges> findAllByUsername(String username);

    /**
     * 根据user查privilege
     *
     * @param username privilege主键
     * @return 关联数据集
     */
    Optional<UserPrivileges> findByUsernameAndPrivilegeId(String username, Long privilegeId);

    /**
     * 根据privilege查user
     *
     * @param privilegeId privilege主键
     * @return 关联数据集
     */
    List<UserPrivileges> findAllByPrivilegeId(Long privilegeId);

}
