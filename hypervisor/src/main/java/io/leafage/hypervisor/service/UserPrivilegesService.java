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

import io.leafage.hypervisor.domain.UserPrivileges;

import java.util.List;
import java.util.Set;

/**
 * user privileges service.
 *
 * @author wq li
 */
public interface UserPrivilegesService {

    /**
     * 查询关联privilege
     *
     * @param username 账号
     * @return 数据集
     */
    List<UserPrivileges> privileges(String username);

    /**
     * 查询关联user
     *
     * @param privilegeId privilege id
     * @return 数据集
     */
    List<UserPrivileges> users(Long privilegeId);

    /**
     * 保存user-privilege关系
     *
     * @param username    账号
     * @param privilegeId privilege主键
     * @param actions     操作
     * @return 结果集
     */
    UserPrivileges relation(String username, Long privilegeId, Set<String> actions);

    /**
     * 移除user-privilege关系
     *
     * @param username    账号
     * @param privilegeId privilege主键
     * @param actions     操作
     */
    void removeRelation(String username, Long privilegeId, Set<String> actions);
}
