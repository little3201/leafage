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
import top.leafage.hypervisor.system.domain.RoleMembers;
import top.leafage.hypervisor.system.repository.RoleMembersRepository;
import top.leafage.hypervisor.system.service.impl.RoleMembersServiceImpl;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * role members service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class RoleMembersServiceImplTest {

    @Mock
    private RoleMembersRepository roleMembersRepository;

    @InjectMocks
    private RoleMembersServiceImpl roleMembersService;

    private RoleMembers roleMembers;

    @BeforeEach
    void setUp() {
        roleMembers = new RoleMembers(1L, "test");
    }

    @Test
    void members() {
        when(roleMembersRepository.findAllByRoleId(anyLong())).thenReturn(List.of(mock(RoleMembers.class)));

        List<RoleMembers> members = roleMembersService.members(anyLong());
        assertEquals(1, members.size());
        verify(roleMembersRepository).findAllByRoleId(anyLong());
    }

    @Test
    void roles() {
        when(roleMembersRepository.findAllByUsername(anyString())).thenReturn(List.of(mock(RoleMembers.class)));

        List<RoleMembers> roles = roleMembersService.roles("test");
        assertEquals(1, roles.size());
        verify(roleMembersRepository).findAllByUsername(anyString());
    }

    @Test
    void relation() {
        when(roleMembersRepository.saveAllAndFlush(anyIterable())).thenReturn(List.of(mock(RoleMembers.class)));

        List<RoleMembers> relation = roleMembersService.relation(1L, Set.of("test"));
        assertEquals(1, relation.size());
        verify(roleMembersRepository).saveAllAndFlush(anyList());
    }

    @Test
    void removeRelation() {
        when(roleMembersRepository.findAllByRoleId(anyLong())).thenReturn(List.of(roleMembers));

        roleMembersService.removeRelation(1L, Set.of("test"));
        verify(roleMembersRepository).deleteAllByIdInBatch(anyCollection());
    }
}