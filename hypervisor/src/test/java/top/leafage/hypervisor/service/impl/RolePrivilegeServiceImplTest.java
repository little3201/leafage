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

import top.leafage.hypervisor.domain.GroupAuthorities;
import top.leafage.hypervisor.domain.GroupRoles;
import top.leafage.hypervisor.domain.Privilege;
import top.leafage.hypervisor.domain.RolePrivileges;
import top.leafage.hypervisor.repository.GroupAuthoritiesRepository;
import top.leafage.hypervisor.repository.GroupRolesRepository;
import top.leafage.hypervisor.repository.PrivilegeRepository;
import top.leafage.hypervisor.repository.RolePrivilegesRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * role privileges service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class RolePrivilegeServiceImplTest {

    @Mock
    private PrivilegeRepository privilegeRepository;

    @Mock
    private RolePrivilegesRepository rolePrivilegesRepository;

    @Mock
    private GroupRolesRepository groupRolesRepository;

    @Mock
    private GroupAuthoritiesRepository groupAuthoritiesRepository;

    @InjectMocks
    private RolePrivilegesServiceImpl rolePrivilegesService;

    private GroupRoles groupRoles;
    private Privilege privilege;

    @BeforeEach
    void setUp() {
        groupRoles = new GroupRoles();
        groupRoles.setGroupId(1L);
        groupRoles.setRoleId(2L);

        privilege = new Privilege();
        privilege.setId(1L);
        privilege.setName("name");
    }

    @Test
    void privileges() {
        given(this.rolePrivilegesRepository.findAllByRoleId(anyLong())).willReturn(List.of(mock(RolePrivileges.class)));

        List<RolePrivileges> privileges = rolePrivilegesService.privileges(anyLong());
        Assertions.assertNotNull(privileges);
    }

    @Test
    void roles() {
        given(this.rolePrivilegesRepository.findAllByPrivilegeId(anyLong())).willReturn(List.of(mock(RolePrivileges.class)));

        List<RolePrivileges> roles = rolePrivilegesService.roles(anyLong());
        Assertions.assertNotNull(roles);
    }

    @Test
    void relation() {
        RolePrivileges rolePrivilege = new RolePrivileges();
        rolePrivilege.setId(1L);
        rolePrivilege.setPrivilegeId(1L);
        rolePrivilege.setRoleId(1L);
        given(this.rolePrivilegesRepository.findByRoleIdAndPrivilegeId(anyLong(), anyLong())).willReturn(Optional.of(rolePrivilege));

        given(this.privilegeRepository.findById(anyLong())).willReturn(Optional.of(privilege));

        given(this.groupRolesRepository.findAllByRoleId(anyLong())).willReturn(List.of(groupRoles));

        given(this.groupAuthoritiesRepository.findByGroupIdAndAuthority(anyLong(), anyString())).willReturn(Optional.ofNullable(mock(GroupAuthorities.class)));

        given(this.rolePrivilegesRepository.saveAndFlush(any(RolePrivileges.class))).willReturn(rolePrivilege);
        RolePrivileges relation = rolePrivilegesService.relation(1L, 1L, "");

        verify(this.rolePrivilegesRepository, times(1)).saveAndFlush(any(RolePrivileges.class));
        verify(this.groupAuthoritiesRepository, times(1)).saveAll(anyList());

        Assertions.assertNotNull(relation);
    }
}