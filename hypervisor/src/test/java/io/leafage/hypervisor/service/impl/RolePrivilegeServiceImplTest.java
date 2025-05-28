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

import io.leafage.hypervisor.domain.*;
import io.leafage.hypervisor.dto.AuthorizePrivilegesDTO;
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
    private PrivilegeRepository privilegeRepository;

    @Mock
    private RolePrivilegesRepository rolePrivilegesRepository;

    @Mock
    private GroupRolesRepository groupRolesRepository;

    @Mock
    private GroupAuthoritiesRepository groupAuthoritiesRepository;

    @InjectMocks
    private RolePrivilegesServiceImpl rolePrivilegesService;

    private AuthorizePrivilegesDTO authorizePrivilegesDTO;
    private Group group;
    private GroupRoles groupRoles;
    private Privilege privilege;

    @BeforeEach
    void setUp() {
        authorizePrivilegesDTO = new AuthorizePrivilegesDTO();
        authorizePrivilegesDTO.setPrivilegeId(1L);
        authorizePrivilegesDTO.setActions(Set.of("create"));

        group = new Group();
        group.setId(1L);
        group.setName("group1");
        group.setDescription("description");

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
        given(this.privilegeRepository.findById(Mockito.anyLong())).willReturn(Optional.of(privilege));

        given(this.groupRolesRepository.findAllByRoleId(Mockito.anyLong())).willReturn(List.of(groupRoles));

        given(this.groupAuthoritiesRepository.save(Mockito.any())).willReturn(Mockito.mock(GroupAuthorities.class));

        List<RolePrivileges> relations = rolePrivilegesService.relation(1L, List.of(authorizePrivilegesDTO));

        verify(this.rolePrivilegesRepository, times(1)).saveAndFlush(Mockito.any(RolePrivileges.class));
        Assertions.assertNotNull(relations);
        Assertions.assertFalse(relations.isEmpty());
    }
}