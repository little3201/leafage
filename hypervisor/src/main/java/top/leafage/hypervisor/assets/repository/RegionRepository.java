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

package top.leafage.hypervisor.assets.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.assets.domain.Region;

/**
 * region repository
 *
 * @author wq li
 */
@Repository
public interface RegionRepository extends R2dbcRepository<Region, Long> {

    /**
     * 是否已存在
     *
     * @param name 名称
     * @return true-是，false-否
     */
    Mono<Boolean> existsByName(String name);

    /**
     * 查询下级
     *
     * @param superiorId superior id
     * @return 结果信息
     */
    Flux<Region> findBySuperiorId(Long superiorId);

    /**
     * Toggles the enabled status of a schema by its ID.
     *
     * @param id The ID of the schema.
     * @return 1 if the update was successful, 0 otherwise.
     */
    @Modifying
    @Query("UPDATE regions SET enabled = NOT enabled WHERE id = :id")
    Mono<Integer> updateEnabledById(Long id);
}
