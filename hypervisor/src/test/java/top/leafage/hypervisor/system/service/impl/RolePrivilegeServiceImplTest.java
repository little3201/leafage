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
import top.leafage.hypervisor.system.domain.GroupAuthorities;
import top.leafage.hypervisor.system.domain.GroupRoles;
import top.leafage.hypervisor.system.domain.Privilege;
import top.leafage.hypervisor.system.domain.RolePrivileges;
import top.leafage.hypervisor.system.repository.GroupAuthoritiesRepository;
import top.leafage.hypervisor.system.repository.GroupRolesRepository;
import top.leafage.hypervisor.system.repository.PrivilegeRepository;
import top.leafage.hypervisor.system.repository.RolePrivilegesRepository;
import top.leafage.hypervisor.system.service.impl.RolePrivilegesServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.mock;
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
    private RolePrivileges rolePrivilege;

    @BeforeEach
    void setUp() {
        groupRoles = new GroupRoles();
        groupRoles.setGroupId(1L);
        groupRoles.setRoleId(2L);

        privilege = new Privilege();
        privilege.setName("name");
        privilege.setActions(Set.of("create"));

        rolePrivilege = new RolePrivileges(1L, 1L, 1L, Set.of("test"));
    }

    @Test
    void privileges() {
        when(rolePrivilegesRepository.findAllByRoleId(anyLong())).thenReturn(List.of(mock(RolePrivileges.class)));

        List<RolePrivileges> privileges = rolePrivilegesService.privileges(anyLong());
        assertEquals(1, privileges.size());
        verify(rolePrivilegesRepository).findAllByRoleId(anyLong());
    }

    @Test
    void roles() {
        when(rolePrivilegesRepository.findAllByPrivilegeId(anyLong())).thenReturn(List.of(mock(RolePrivileges.class)));

        List<RolePrivileges> roles = rolePrivilegesService.roles(anyLong());
        assertEquals(1, roles.size());
        verify(rolePrivilegesRepository).findAllByPrivilegeId(anyLong());
    }

    @Test
    void relation() {
        when(privilegeRepository.findById(anyLong())).thenReturn(Optional.of(privilege));
        when(groupRolesRepository.findAllByRoleId(anyLong())).thenReturn(List.of(groupRoles));
        when(groupAuthoritiesRepository.findByGroupIdAndAuthority(anyLong(), anyString())).thenReturn(Optional.of(mock(GroupAuthorities.class)));
        when(rolePrivilegesRepository.saveAndFlush(any(RolePrivileges.class))).thenReturn(rolePrivilege);

        RolePrivileges relation = rolePrivilegesService.relation(1L, 1L, "");
        assertEquals(1, relation.getRoleId());
        verify(rolePrivilegesRepository).saveAndFlush(any(RolePrivileges.class));
        verify(groupAuthoritiesRepository).saveAll(anyList());
    }


    @Test
    void removeRelation() {
        when(rolePrivilegesRepository.findByRoleIdAndPrivilegeId(anyLong(), anyLong())).thenReturn(Optional.of(rolePrivilege));
        when(privilegeRepository.findById(anyLong())).thenReturn(Optional.of(privilege));
        when(groupRolesRepository.findAllByRoleId(anyLong())).thenReturn(List.of(groupRoles));

        rolePrivilegesService.removeRelation(1L, 2L, "test");
        verify(groupAuthoritiesRepository).deleteByGroupIdAndAuthority(anyLong(), anyString());
    }

    @Test
    void removeRelation_empty_action() {
        when(rolePrivilegesRepository.findByRoleIdAndPrivilegeId(anyLong(), anyLong())).thenReturn(Optional.of(rolePrivilege));
        when(privilegeRepository.findById(anyLong())).thenReturn(Optional.of(privilege));
        when(groupRolesRepository.findAllByRoleId(anyLong())).thenReturn(List.of(groupRoles));

        rolePrivilegesService.removeRelation(1L, 2L, "");
        verify(rolePrivilegesRepository).deleteById(anyLong());
        verify(groupAuthoritiesRepository).deleteByGroupIdAndAuthorityStartingWith(anyLong(), anyString());
    }
}