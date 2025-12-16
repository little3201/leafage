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

package top.leafage.hypervisor.system.service;

import top.leafage.hypervisor.system.domain.GroupRoles;

import java.util.List;
import java.util.Set;

/**
 * group roles service.
 *
 * @author wq li
 */
public interface GroupRolesService {

    /**
     * 查询关联 role
     *
     * @param groupId group主键
     * @return 数据集
     */
    List<GroupRoles> roles(Long groupId);

    /**
     * 查询关联 group
     *
     * @param roleId role id
     * @return 数据集
     */
    List<GroupRoles> groups(Long roleId);

    /**
     * 保存group-roles
     *
     * @param groupId group id
     * @param roleIds role ids
     * @return 结果集
     */
    List<GroupRoles> relation(Long groupId, Set<Long> roleIds);

    /**
     * 移除group-roles关系
     *
     * @param groupId group id
     * @param roleIds role ids
     */
    void removeRelation(Long groupId, Set<Long> roleIds);
}
