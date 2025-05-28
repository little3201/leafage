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

import io.leafage.hypervisor.domain.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * message repository
 *
 * @author wq li
 */
@Repository
public interface MessageRepository extends R2dbcRepository<Message, Long> {

    /**
     * 分页查询
     *
     * @param receiver 接收者
     * @param pageable 分页参数
     * @return 有效数据集
     */
    Flux<Message> findByReceiver(String receiver, Pageable pageable);

    /**
     * 查询未读记录数
     *
     * @param receiver a {@link java.lang.String} object
     * @return 记录数
     */
    Mono<Long> countByReceiver(String receiver);
}
