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

import top.leafage.hypervisor.domain.RoleMembers;
import top.leafage.hypervisor.repository.RoleMembersRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

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
        given(this.roleMembersRepository.findAllByRoleId(anyLong())).willReturn(List.of(mock(RoleMembers.class)));

        List<RoleMembers> members = roleMembersService.members(anyLong());
        Assertions.assertNotNull(members);
    }

    @Test
    void roles() {
        given(this.roleMembersRepository.findAllByUsername(anyString())).willReturn(List.of(mock(RoleMembers.class)));

        List<RoleMembers> roles = roleMembersService.roles("test");
        Assertions.assertNotNull(roles);
    }

    @Test
    void relation() {
        given(this.roleMembersRepository.saveAllAndFlush(anyIterable())).willReturn(anyList());

        List<RoleMembers> relation = roleMembersService.relation(1L, Set.of("test"));

        verify(this.roleMembersRepository, times(1)).saveAllAndFlush(anyList());
        Assertions.assertNotNull(relation);
    }
}