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

package top.leafage.hypervisor.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.domain.User;

/**
 * user repository
 *
 * @author wq li
 */
@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {

    /**
     * 是否已存在
     *
     * @param username user
     * @return true-是，false-否
     */
    Mono<Boolean> existsByUsername(String username);

    /**
     * Toggles the enabled status of a record by its ID.
     *
     * @param id the pk.
     * @return result.
     */
    @Modifying
    @Query("UPDATE users SET enabled = CASE WHEN enabled = true THEN false ELSE true END WHERE id = :id")
    Mono<Integer> updateEnabledById(Long id);

    /**
     * Toggles the accountNonLocked status of a record by its ID.
     *
     * @param id the pk.
     * @return result.
     */
    @Modifying
    @Query("UPDATE users SET account_non_locked = CASE WHEN account_non_locked = true THEN false ELSE true END WHERE id = :id")
    Mono<Integer> updateAccountNonLockedById(Long id);
}
