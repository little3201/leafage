/*
 * Copyright (c) 2025.  little3201.
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

package io.leafage.hypervisor.service.impl;

import io.leafage.hypervisor.domain.UserPrivileges;
import io.leafage.hypervisor.repository.UserPrivilegesRepository;
import io.leafage.hypervisor.service.UserPrivilegesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserPrivilegesServiceImpl implements UserPrivilegesService {

    private final UserPrivilegesRepository userPrivilegesRepository;

    public UserPrivilegesServiceImpl(UserPrivilegesRepository userPrivilegesRepository) {
        this.userPrivilegesRepository = userPrivilegesRepository;
    }

    @Override
    public List<UserPrivileges> privileges(String username) {
        return userPrivilegesRepository.findAllByUsername(username);
    }

    @Override
    public List<UserPrivileges> users(Long privilegeId) {
        return userPrivilegesRepository.findAllByPrivilegeId(privilegeId);
    }

    @Override
    public UserPrivileges relation(String username, Long privilegeId, Set<String> actions) {
        return null;
    }

    @Override
    public void removeRelation(String username, Long privilegeId, Set<String> actions) {

    }
}
