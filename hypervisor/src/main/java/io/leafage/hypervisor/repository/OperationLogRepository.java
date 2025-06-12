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

package io.leafage.hypervisor.repository;

import io.leafage.hypervisor.domain.AccessLog;
import io.leafage.hypervisor.domain.OperationLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * operation log repository
 *
 * @author wq li
 */
@Repository
public interface OperationLogRepository extends R2dbcRepository<OperationLog, Long> {

    /**
     * 查询
     *
     * @param pageable a {@link Pageable} object
     * @return 有效帖子
     */
    Flux<OperationLog> findAllBy(Pageable pageable);
}
