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

import top.leafage.hypervisor.domain.GroupPrivileges;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * group privileges repository
 *
 * @author wq li
 */
@Repository
public interface GroupPrivilegesRepository extends R2dbcRepository<GroupPrivileges, Long> {

    /**
     * 根据group查
     *
     * @param groupId group主键
     * @return 关联数据集
     */
    Flux<GroupPrivileges> findByGroupId(Long groupId);

    /**
     * 根据group和privilege查
     *
     * @param groupId     group主键
     * @param privilegeId privilege主键
     * @return 关联数据集
     */
    Mono<GroupPrivileges> findByGroupIdAndPrivilegeId(Long groupId, Long privilegeId);

    /**
     * 根据privilege查
     *
     * @param privilegeId privilege主键
     * @return 关联数据集
     */
    Flux<GroupPrivileges> findByPrivilegeId(Long privilegeId);
}
