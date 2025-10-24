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

package io.leafage.hypervisor.service;

import io.leafage.hypervisor.domain.GroupMembers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

/**
 * group members service
 *
 * @author wq li
 */
public interface GroupMembersService {

    /**
     * 查询关联user
     *
     * @param groupId group主键
     * @return 数据集
     */
    Flux<GroupMembers> members(Long groupId);

    /**
     * 查询关联group
     *
     * @param username 用户名
     * @return 数据集
     */
    Mono<List<GroupMembers>> groups(String username);

    /**
     * group-user关系
     *
     * @param groupId   group 主键
     * @param usernames username集合
     * @return 是否成功： true - 是， false - 否
     */
    Mono<Boolean> relation(Long groupId, Set<String> usernames);
}
