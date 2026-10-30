/*
 *
 *  * Copyright(c) 2019-present the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *       https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package top.leafage.hypervisor.system.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import top.leafage.hypervisor.system.domain.User;

import java.util.Optional;

/**
 * user repository.
 *
 * @author wq li
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Query("SELECT * from users WHERE username = ?#{principal?.name}")
    Optional<User> findCurrentUser();

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
    @Query("UPDATE users SET enabled = CASE WHEN enabled = true THEN false ELSE true END WHERE id = :id")
    int updateEnabledById(Long id);

    /**
     * update the accountNonLocked to true by pk.
     *
     * @param id the pk.
     * @return result.
     */
    @Modifying
    @Query("UPDATE users SET account_non_locked = true WHERE id = :id")
    int updateAccountNonLockedById(Long id);
}
