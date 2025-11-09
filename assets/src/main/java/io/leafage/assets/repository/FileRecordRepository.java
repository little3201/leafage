/*
 * Copyright (c) 2024-2025.  little3201.
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

package io.leafage.assets.repository;

import io.leafage.assets.domain.FileRecord;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * file repository.
 *
 * @author wq li
 */
@Repository
public interface FileRecordRepository extends R2dbcRepository<FileRecord, Long> {

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
     * @return true-是，false-否
     */
    Mono<Boolean> existsByNameAndIdNot(String name, Long id);
}
