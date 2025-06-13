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

import io.leafage.hypervisor.domain.GroupAuthorities;
import io.leafage.hypervisor.domain.GroupRoles;
import io.leafage.hypervisor.domain.Privilege;
import io.leafage.hypervisor.domain.RolePrivileges;
import io.leafage.hypervisor.repository.GroupAuthoritiesRepository;
import io.leafage.hypervisor.repository.GroupRolesRepository;
import io.leafage.hypervisor.repository.PrivilegeRepository;
import io.leafage.hypervisor.repository.RolePrivilegesRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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
        RolePrivileges rolePrivilege = new RolePrivileges();
        rolePrivilege.setId(1L);
        rolePrivilege.setPrivilegeId(1L);
        rolePrivilege.setRoleId(1L);
        given(this.rolePrivilegesRepository.findByRoleIdAndPrivilegeId(Mockito.anyLong(), Mockito.anyLong())).willReturn(Optional.of(rolePrivilege));

        given(this.privilegeRepository.findById(Mockito.anyLong())).willReturn(Optional.of(privilege));

        given(this.groupRolesRepository.findAllByRoleId(Mockito.anyLong())).willReturn(List.of(groupRoles));

        given(this.groupAuthoritiesRepository.findByGroupIdAndAuthority(Mockito.anyLong(), Mockito.anyString())).willReturn(Optional.ofNullable(Mockito.mock(GroupAuthorities.class)));

        given(this.rolePrivilegesRepository.saveAndFlush(Mockito.any(RolePrivileges.class))).willReturn(rolePrivilege);
        RolePrivileges relation = rolePrivilegesService.relation(1L, 1L, "");

        verify(this.rolePrivilegesRepository, times(1)).saveAndFlush(Mockito.any(RolePrivileges.class));
        verify(this.groupAuthoritiesRepository, times(1)).saveAll(Mockito.anyList());

        Assertions.assertNotNull(relation);
    }
}