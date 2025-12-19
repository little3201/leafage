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

import top.leafage.hypervisor.system.domain.RolePrivileges;

import java.util.List;

/**
 * role privileges service.
 *
 * @author wq li
 */
public interface RolePrivilegesService {

    /**
     * 查询关联privilege
     *
     * @param roleId the pk of role.
     * @return 数据集
     */
    List<RolePrivileges> privileges(Long roleId);

    /**
     * 查询关联role
     *
     * @param privilegeId privilege id
     * @return 数据集
     */
    List<RolePrivileges> roles(Long privilegeId);

    /**
     * 保存role-privilege关系
     *
     * @param roleId      the pk of role.
     * @param privilegeId privilege id
     * @param action      操作
     * @return 结果集
     */
    RolePrivileges relation(Long roleId, Long privilegeId, String action);

    /**
     * 移除role-privilege关系
     *
     * @param roleId      the pk of role.
     * @param privilegeId the pk of privilege.
     * @param action      操作
     */
    void removeRelation(Long roleId, Long privilegeId, String action);
}
