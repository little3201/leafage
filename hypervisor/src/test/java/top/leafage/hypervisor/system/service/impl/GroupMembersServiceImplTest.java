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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.leafage.hypervisor.system.domain.GroupMembers;
import top.leafage.hypervisor.system.repository.GroupMembersRepository;
import top.leafage.hypervisor.system.service.impl.GroupMembersServiceImpl;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


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

    private GroupMembers groupMembers;

    @BeforeEach
    void setUp() {
        groupMembers = new GroupMembers(1L, "test");
    }

    @Test
    void members() {
        when(groupMembersRepository.findAllByGroupId(anyLong())).thenReturn(List.of(mock(GroupMembers.class)));

        List<GroupMembers> members = groupMembersService.members(1L);
        assertEquals(1, members.size());
        verify(groupMembersRepository).findAllByGroupId(anyLong());
    }

    @Test
    void groups() {
        when(groupMembersRepository.findAllByUsername(anyString())).thenReturn(List.of(mock(GroupMembers.class)));

        List<GroupMembers> groups = groupMembersService.groups("test");
        assertEquals(1, groups.size());
        verify(groupMembersRepository).findAllByUsername(anyString());
    }

    @Test
    void relation() {
        when(groupMembersRepository.saveAllAndFlush(anyCollection())).thenReturn(List.of(mock(GroupMembers.class)));

        List<GroupMembers> relation = groupMembersService.relation(1L, Set.of("test"));
        assertEquals(1, relation.size());
        verify(groupMembersRepository).saveAllAndFlush(anyCollection());
    }

    @Test
    void removeRelation() {
        when(groupMembersRepository.findAllByGroupId(anyLong())).thenReturn(List.of(groupMembers));

        groupMembersService.removeRelation(1L, Set.of("test"));
        verify(groupMembersRepository).deleteAllByIdInBatch(anyCollection());
    }
}