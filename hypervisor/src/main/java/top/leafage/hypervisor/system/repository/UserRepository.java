/*
 * Copyright(c) 2019-present the original author or authors.
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

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import top.leafage.hypervisor.system.domain.Role;
import top.leafage.hypervisor.system.domain.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * user repository.
 *
 * @author wq li
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * Fetch current user.
     *
     * @return user.
     */
    @Query("SELECT t from User t WHERE t.username = ?#{ principal?.name }")
    Optional<User> findCurrentUser();

    @EntityGraph(attributePaths = "roles")
    @Query("SELECT t from User t WHERE t.username = ?#{ principal?.name }")
    Optional<User> findCurrentUserWithRoles();

    /**
     * 查询
     *
     * @param role the role.
     * @return result.
     */
    List<User> findDisctinctByRolesContaining(Role role);

    /**
     * 查询
     *
     * @param ids the pk of records.
     * @return result.
     */
    List<User> findByRolesIdIn(Collection<Long> ids);

    /**
     * 查询
     *
     * @param id The pk.
     * @return result.
     */
    @EntityGraph(attributePaths = "roles")
    Optional<User> findWithRolesById(Long id);

    /**
     * 查询.
     *
     * @param usernames the username of users.
     * @return result.
     */
    List<User> findAllByUsernameIn(Collection<String> usernames);

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
     * @param id The pk.
     * @return result.
     */
    @Modifying
    @Query("UPDATE User t SET t.enabled = true WHERE t.id = :id AND t.enabled = false")
    int enableById(Long id);

    /**
     * disable a record by pk.
     *
     * @param id The pk.
     * @return result.
     */
    @Modifying
    @Query("UPDATE User t SET t.enabled = false WHERE t.id = :id AND t.enabled = true")
    int disableById(Long id);

}
