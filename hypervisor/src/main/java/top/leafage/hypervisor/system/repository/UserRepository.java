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
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import top.leafage.hypervisor.system.domain.User;

/**
 * user repository.
 *
 * @author wq li
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * is exists.
     *
     * @param username username.
     * @return if exists return true or false.
     */
    boolean existsByUsername(String username);

    /**
     * is exists.
     *
     * @param email username.
     * @return if exists return true or false.
     */
    boolean existsByEmail(String email);

    /**
     * enable a record by pk.
     *
     * @param id the pk.
     * @return result.
     */
    @Modifying
    @Query("UPDATE User t SET t.enabled = CASE WHEN t.enabled = true THEN false ELSE true END WHERE t.id = :id")
    int updateEnabledById(Long id);

    /**
     * update the accountNonLocked to true by pk.
     *
     * @param id the pk.
     * @return result.
     */
    @Modifying
    @Query("UPDATE User t SET t.accountNonLocked = true WHERE t.id = :id")
    int updateAccountNonLockedById(Long id);
}
