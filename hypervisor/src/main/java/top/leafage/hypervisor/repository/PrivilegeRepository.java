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

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.domain.Privilege;

/**
 * privilege repository
 *
 * @author wq li
 */
@Repository
public interface PrivilegeRepository extends R2dbcRepository<Privilege, Long> {

    /**
     * 查询
     *
     * @param superiorId 上级
     * @return 有效帖子
     */
    Flux<Privilege> findAllBySuperiorId(Long superiorId);

    /**
     * 是否已存在
     *
     * @param name 名称
     * @return true-是，false-否
     */
    Mono<Boolean> existsByName(String name);

    /**
     * 统计
     *
     * @param superiorId 上级
     * @return 数量
     */
    Mono<Long> countBySuperiorId(Long superiorId);
}
