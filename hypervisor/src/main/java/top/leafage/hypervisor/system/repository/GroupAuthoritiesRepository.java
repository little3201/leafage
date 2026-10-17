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

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import top.leafage.hypervisor.system.domain.GroupAuthorities;

import java.util.Optional;

/**
 * authorities repository
 *
 * @author wq li
 */
@Repository
public interface GroupAuthoritiesRepository extends CrudRepository<GroupAuthorities, Long> {

    /**
     * find by group id and authority.
     *
     * @param groupId   the pk of group.
     * @param authority the authority.
     * @return the result.
     */
    Optional<GroupAuthorities> findByGroupIdAndAuthority(Long groupId, String authority);

    /**
     * delete by group id and authority.
     *
     * @param groupId   the pk of group.
     * @param authority the authority.
     */
    void deleteByGroupIdAndAuthority(Long groupId, String authority);

    /**
     * delete by group id and authority start with.
     *
     * @param groupId   the pk of group.
     * @param authority the authority.
     */
    void deleteByGroupIdAndAuthorityStartingWith(Long groupId, String authority);
}
