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

package io.leafage.basic.hypervisor.service.impl;

import io.leafage.basic.hypervisor.domain.GroupPrivileges;
import io.leafage.basic.hypervisor.repository.GroupPrivilegesRepository;
import io.leafage.basic.hypervisor.repository.PrivilegeRepository;
import io.leafage.basic.hypervisor.service.GroupPrivilegesService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

/**
 * group privileges service impl
 *
 * @author wq li
 */
@Service
public class GroupPrivilegesServiceImpl implements GroupPrivilegesService {

    private final GroupPrivilegesRepository groupPrivilegesRepository;
    private final PrivilegeRepository privilegeRepository;

    /**
     * <p>Constructor for GroupPrivilegesServiceImpl.</p>
     *
     * @param groupPrivilegesRepository a {@link GroupPrivilegesRepository} object
     */
    public GroupPrivilegesServiceImpl(GroupPrivilegesRepository groupPrivilegesRepository, PrivilegeRepository privilegeRepository) {
        this.groupPrivilegesRepository = groupPrivilegesRepository;
        this.privilegeRepository = privilegeRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<List<GroupPrivileges>> privileges(Long groupId) {
        Assert.notNull(groupId, "groupId must not be null.");

        return groupPrivilegesRepository.findByGroupId(groupId).collectList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<List<GroupPrivileges>> groups(Long privilegeId) {
        Assert.notNull(privilegeId, "privilegeId must not be empty.");

        return groupPrivilegesRepository.findByPrivilegeId(privilegeId).collectList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<GroupPrivileges> relation(Long groupId, Long privilegeId, Set<String> actions) {
        Assert.notNull(groupId, "groupId must not be empty.");
        Assert.notNull(privilegeId, "privilegeId must not be empty.");

        return privilegeRepository.findById(privilegeId)
                .map(privilege -> {
                    GroupPrivileges groupPrivileges = new GroupPrivileges();
                    groupPrivileges.setGroupId(groupId);
                    groupPrivileges.setPrivilegeId(privilegeId);
                    groupPrivileges.setActions(actions);
                    return groupPrivileges;
                }).flatMap(groupPrivilegesRepository::save);
    }
}
