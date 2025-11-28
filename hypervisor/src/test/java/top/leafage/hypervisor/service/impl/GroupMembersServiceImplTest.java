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

package top.leafage.hypervisor.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.leafage.hypervisor.domain.GroupMembers;
import top.leafage.hypervisor.repository.GroupMembersRepository;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;


/**
 * group members service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class GroupMembersServiceImplTest {

    @Mock
    private GroupMembersRepository groupMembersRepository;

    @InjectMocks
    private GroupMembersServiceImpl groupMembersService;

    @Test
    void members() {
        given(this.groupMembersRepository.findAllByGroupId(anyLong())).willReturn(List.of(mock(GroupMembers.class)));

        List<GroupMembers> members = groupMembersService.members(1L);
        Assertions.assertNotNull(members);
    }

    @Test
    void groups() {
        given(this.groupMembersRepository.findAllByUsername(anyString())).willReturn(List.of(mock(GroupMembers.class)));

        List<GroupMembers> groups = groupMembersService.groups("test");
        Assertions.assertNotNull(groups);
    }

    @Test
    void relation() {

        given(this.groupMembersRepository.saveAllAndFlush(anyCollection())).willReturn(anyList());

        List<GroupMembers> relation = groupMembersService.relation(1L, Set.of("test"));

        verify(this.groupMembersRepository, times(1)).saveAllAndFlush(anyList());
        Assertions.assertNotNull(relation);
    }
}