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

import io.leafage.hypervisor.dto.UserDTO;
import io.leafage.hypervisor.vo.UserVO;
import top.leafage.common.jpa.JpaCrudService;

/**
 * user service.
 *
 * @author wq li
 */
public interface UserService extends JpaCrudService<UserDTO, UserVO> {

    /**
     * Fetch user
     *
     * @param username username
     * @return Record
     */
    UserVO findByUsername(String username);

    /**
     * Update accountNonLocked.
     *
     * @param id 主键
     * @return result.
     */
    boolean unlock(Long id);
}
