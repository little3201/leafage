/*
 * Copyright (c) 2026.  little3201.
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

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.system.domain.GroupAuthorities;

/**
 * group authorities repository
 *
 * @author wq li
 */
@Repository
public interface GroupAuthoritiesRepository extends R2dbcRepository<GroupAuthorities, Long> {

    /**
     * 根据group删除
     *
     * @param groupId   group the pk.
     * @param authority 权限
     */
    Mono<Void> deleteByGroupIdAndAuthority(Long groupId, String authority);

}
