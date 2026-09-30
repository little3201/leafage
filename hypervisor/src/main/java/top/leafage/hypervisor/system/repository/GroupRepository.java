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
import top.leafage.hypervisor.system.domain.Group;
import top.leafage.hypervisor.system.domain.Role;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * group repository.
 *
 * @author wq li
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Long>, JpaSpecificationExecutor<Group> {

    /**
     * 查询关联
     *
     * @param role the role.
     * @return result.
     */
    List<Group> findDisctinctByRolesContaining(Role role);

    @EntityGraph(attributePaths = "roles")
    List<Group> findDistinctByMembersUsernameAndEnabledTrue(String username);

    List<Group> findAllBySuperiorIdInAndEnabledTrue(Collection<Long> superiorIds);

    /**
     * 查询 members
     *
     * @param id the pk of group.
     * @return result.
     */
    @EntityGraph(attributePaths = "members")
    Optional<Group> findWithMembersById(Long id);

    /**
     * 查询 members
     *
     * @param ids the pk of group.
     * @return result.
     */
    @EntityGraph(attributePaths = "members")
    List<Group> findWithMembersByIdIn(Collection<Long> ids);

    /**
     * 查询 roles
     *
     * @param id the pk of group.
     * @return result.
     */
    @EntityGraph(attributePaths = "roles")
    Optional<Group> findWithRolesById(Long id);

    /**
     * is exists.
     *
     * @param name name.
     * @return if exists return true or false.
     */
    boolean existsByName(String name);

    /**
     * enable a record by pk.
     *
     * @param id The pk.
     * @return result.
     */
    @Modifying
    @Query("UPDATE Group t SET t.enabled = true WHERE t.id = :id AND t.enabled = false")
    int enableById(Long id);

    /**
     * disable a record by pk.
     *
     * @param id The pk.
     * @return result.
     */
    @Modifying
    @Query("UPDATE Group t SET t.enabled = false WHERE t.id = :id AND t.enabled = true")
    int disableById(Long id);
}
