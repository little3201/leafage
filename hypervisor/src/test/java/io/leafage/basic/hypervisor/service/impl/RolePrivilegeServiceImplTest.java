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

package io.leafage.basic.hypervisor.service.impl;

import io.leafage.basic.hypervisor.domain.RolePrivileges;
import io.leafage.basic.hypervisor.repository.RolePrivilegesRepository;
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
 * role privileges service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class RolePrivilegeServiceImplTest {

    @Mock
    private RolePrivilegesRepository rolePrivilegesRepository;

    @InjectMocks
    private RolePrivilegesServiceImpl rolePrivilegesService;

    @Test
    void privileges() {
        given(this.rolePrivilegesRepository.findAllByRoleId(Mockito.anyLong())).willReturn(List.of(Mockito.mock(RolePrivileges.class)));

        List<RolePrivileges> privileges = rolePrivilegesService.privileges(Mockito.anyLong());
        Assertions.assertNotNull(privileges);
    }

    @Test
    void roles() {
        given(this.rolePrivilegesRepository.findAllByPrivilegeId(Mockito.anyLong())).willReturn(List.of(Mockito.mock(RolePrivileges.class)));

        List<RolePrivileges> roles = rolePrivilegesService.roles(Mockito.anyLong());
        Assertions.assertNotNull(roles);
    }

    @Test
    void relation() {
        given(this.rolePrivilegesRepository.saveAllAndFlush(Mockito.anyCollection())).willReturn(Mockito.anyList());

        RolePrivileges relation = rolePrivilegesService.relation(1L, 1L, Set.of("test"));

        verify(this.rolePrivilegesRepository, times(1)).saveAllAndFlush(Mockito.anyList());
        Assertions.assertNotNull(relation);
    }
}