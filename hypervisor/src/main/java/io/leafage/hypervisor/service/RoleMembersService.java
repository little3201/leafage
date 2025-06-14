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

package io.leafage.hypervisor.service;

import io.leafage.hypervisor.domain.RoleMembers;

import java.util.List;
import java.util.Set;

/**
 * role members service.
 *
 * @author wq li
 */
public interface RoleMembersService {

    /**
     * 查询关联 members
     *
     * @param roleId role主键
     * @return 数据集
     */
    List<RoleMembers> members(Long roleId);

    /**
     * 查询关联role
     *
     * @param username 用户名
     * @return 数据集
     */
    List<RoleMembers> roles(String username);

    /**
     * 保存role-members
     *
     * @param roleId role主键
     * @param users  user集合
     * @return 结果集
     */
    List<RoleMembers> relation(Long roleId, Set<String> users);

    /**
     * 移除role-members关系
     *
     * @param roleId    role主键
     * @param usernames username集合
     */
    void removeRelation(Long roleId, Set<String> usernames);
}
