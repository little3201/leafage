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

package io.leafage.assets.repository;

import io.leafage.assets.domain.Region;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * region repository
 *
 * @author wq li
 */
@Repository
public interface RegionRepository extends R2dbcRepository<Region, Long> {

    /**
     * 分页查询
     *
     * @param pageable 分页参数
     * @return 有效数据集
     */
    Flux<Region> findAllBySuperiorIdIsNull(Pageable pageable);

    /**
     * 是否已存在
     *
     * @param name 名称
     * @return true-是，false-否
     */
    Mono<Boolean> existsByName(String name);

    /**
     * 是否已存在
     *
     * @param name 名称
     * @param id   主键
     * @return true-是，false-否
     */
    Mono<Boolean> existsByNameAndIdNot(String name, Long id);

    /**
     * 查询下级
     *
     * @param superiorId superior id
     * @return 结果信息
     */
    Flux<Region> findBySuperiorId(Long superiorId);

    /**
     * 统计
     *
     * @return 记录数
     */
    Mono<Long> countBySuperiorIdIsNullAndEnabledTrue();
}
