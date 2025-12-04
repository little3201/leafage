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

package top.leafage.hypervisor.service;

import reactor.core.publisher.Mono;
import top.leafage.hypervisor.domain.GroupPrivileges;

import java.util.List;
import java.util.Set;

/**
 * group privileges service
 *
 * @author wq li
 */
public interface GroupPrivilegesService {

    /**
     * 查询关联user
     *
     * @param groupId group主键
     * @return 数据集
     */
    Mono<List<GroupPrivileges>> privileges(Long groupId);

    /**
     * 查询关联group
     *
     * @param privilegeId privilege主键
     * @return 数据集
     */
    Mono<List<GroupPrivileges>> groups(Long privilegeId);

    /**
     * group-privileges关联
     *
     * @param groupId     group 主键
     * @param privilegeId privilege 主键
     * @param actions     按钮
     * @return 数据集
     */
    Mono<GroupPrivileges> relation(Long groupId, Long privilegeId, Set<String> actions);

    /**
     * 移除group-privileges关系
     *
     * @param groupId     group 主键
     * @param privilegeId privilege 主键
     * @param actions     按钮
     * @return 数据集
     */
    Mono<Void> removeRelation(Long groupId, Long privilegeId, Set<String> actions);
}
