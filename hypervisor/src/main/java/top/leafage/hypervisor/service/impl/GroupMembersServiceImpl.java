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

package top.leafage.hypervisor.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.domain.GroupMembers;
import top.leafage.hypervisor.repository.GroupMembersRepository;
import top.leafage.hypervisor.service.GroupMembersService;

import java.util.List;
import java.util.Set;

import static top.leafage.common.data.AbstractService._MUST_NOT_BE_EMPTY;
import static top.leafage.common.data.AbstractService._MUST_NOT_BE_NULL;

/**
 * group members service impl
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
    public Flux<GroupMembers> members(Long groupId) {
        Assert.notNull(groupId, String.format(_MUST_NOT_BE_NULL, "groupId"));

        return groupMembersRepository.findByGroupId(groupId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<List<GroupMembers>> groups(String username) {
        Assert.hasText(username, String.format(_MUST_NOT_BE_EMPTY, "username"));

        return groupMembersRepository.findByUsername(username).collectList();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Mono<Boolean> relation(Long groupId, Set<String> usernames) {
        Assert.notNull(groupId, String.format(_MUST_NOT_BE_NULL, "groupId"));
        Assert.notEmpty(usernames, String.format(_MUST_NOT_BE_EMPTY, "usernames"));

        return Flux.fromIterable(usernames).map(username -> {
                    GroupMembers groupMembers = new GroupMembers();
                    groupMembers.setUsername(username);
                    groupMembers.setGroupId(groupId);
                    return groupMembers;
                }).flatMap(groupMembersRepository::save)
                .all(groupMembers -> Boolean.TRUE);
    }
}
