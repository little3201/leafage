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

package io.leafage.hypervisor.service.impl;

import io.leafage.hypervisor.domain.RoleMembers;
import io.leafage.hypervisor.repository.RoleMembersRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
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

    @Test
    void members() {
        given(this.roleMembersRepository.findAllByRoleId(Mockito.anyLong())).willReturn(List.of(Mockito.mock(RoleMembers.class)));

        List<RoleMembers> members = roleMembersService.members(Mockito.anyLong());
        Assertions.assertNotNull(members);
    }

    @Test
    void roles() {
        given(this.roleMembersRepository.findAllByUsername(Mockito.anyString())).willReturn(List.of(Mockito.mock(RoleMembers.class)));

        List<RoleMembers> roles = roleMembersService.roles("test");
        Assertions.assertNotNull(roles);
    }

    @Test
    void relation() {
        given(this.roleMembersRepository.saveAllAndFlush(Mockito.anyIterable())).willReturn(Mockito.anyList());

        List<RoleMembers> relation = roleMembersService.relation(1L, Set.of("test"));

        verify(this.roleMembersRepository, times(1)).saveAllAndFlush(Mockito.anyList());
        Assertions.assertNotNull(relation);
    }
}