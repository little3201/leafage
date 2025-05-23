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

package io.leafage.basic.hypervisor.service.impl;

import io.leafage.basic.hypervisor.domain.GroupPrivileges;
import io.leafage.basic.hypervisor.repository.GroupPrivilegesRepository;
import io.leafage.basic.hypervisor.service.GroupPrivilegesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class GroupPrivilegesServiceImpl implements GroupPrivilegesService {

    private final GroupPrivilegesRepository groupPrivilegesRepository;

    public GroupPrivilegesServiceImpl(GroupPrivilegesRepository groupPrivilegesRepository) {
        this.groupPrivilegesRepository = groupPrivilegesRepository;
    }

    @Override
    public List<GroupPrivileges> privileges(Long groupId) {
        return groupPrivilegesRepository.findAllByGroupId(groupId);
    }

    @Override
    public List<GroupPrivileges> groups(Long privilegeId) {
        return groupPrivilegesRepository.findAllByPrivilegeId(privilegeId);
    }

    @Override
    public GroupPrivileges relation(Long groupId, Long privilegeId, Set<String> actions) {
        return null;
    }

    @Override
    public void removeRelation(Long groupId, Long privilegeId, Set<String> actions) {

    }
}
