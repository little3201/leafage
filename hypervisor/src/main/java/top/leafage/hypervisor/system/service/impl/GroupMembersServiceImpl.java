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

package top.leafage.hypervisor.system.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.leafage.hypervisor.system.domain.GroupMembers;
import top.leafage.hypervisor.system.repository.GroupMembersRepository;
import top.leafage.hypervisor.system.service.GroupMembersService;

import java.util.List;
import java.util.Set;

import static top.leafage.common.data.Service._MUST_NOT_BE_EMPTY;
import static top.leafage.common.data.Service._MUST_NOT_BE_NULL;


/**
 * group members service impl.
 *
 * @author wq li
 */
@Service
public class GroupMembersServiceImpl implements GroupMembersService {

    private final GroupMembersRepository groupMembersRepository;

    /**
     * Constructor for GroupMembersServiceImpl.
     *
     * @param groupMembersRepository a {@link GroupMembersRepository} object
     */
    public GroupMembersServiceImpl(GroupMembersRepository groupMembersRepository) {
        this.groupMembersRepository = groupMembersRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GroupMembers> members(Long groupId) {
        Assert.notNull(groupId, String.format(_MUST_NOT_BE_NULL, "groupId"));

        return groupMembersRepository.findAllByGroupId(groupId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GroupMembers> groups(String username) {
        Assert.hasText(username, String.format(_MUST_NOT_BE_EMPTY, "username"));

        return groupMembersRepository.findAllByUsername(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GroupMembers> relation(Long groupId, Set<String> usernames) {
        Assert.notNull(groupId, String.format(_MUST_NOT_BE_NULL, "groupId"));
        Assert.notEmpty(usernames, String.format(_MUST_NOT_BE_EMPTY, "usernames"));

        List<GroupMembers> groupMembers = usernames.stream().map(username -> {
            GroupMembers groupMember = new GroupMembers();
            groupMember.setGroupId(groupId);
            groupMember.setUsername(username);
            return groupMember;
        }).toList();
        return groupMembersRepository.saveAllAndFlush(groupMembers);
    }

    @Override
    public void removeRelation(Long groupId, Set<String> usernames) {
        Assert.notNull(groupId, String.format(_MUST_NOT_BE_NULL, "groupId"));
        Assert.notEmpty(usernames, String.format(_MUST_NOT_BE_EMPTY, "usernames"));

        List<GroupMembers> groupMembers = groupMembersRepository.findAllByGroupId(groupId);
        List<Long> filteredIds = groupMembers.stream()
                .filter(roleMember -> usernames.contains(roleMember.getUsername()))
                .map(GroupMembers::getId).toList();
        groupMembersRepository.deleteAllByIdInBatch(filteredIds);
    }
}
