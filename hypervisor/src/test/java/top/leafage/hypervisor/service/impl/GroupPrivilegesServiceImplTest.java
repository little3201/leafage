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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.leafage.hypervisor.domain.GroupAuthorities;
import top.leafage.hypervisor.domain.GroupPrivileges;
import top.leafage.hypervisor.domain.Privilege;
import top.leafage.hypervisor.repository.GroupAuthoritiesRepository;
import top.leafage.hypervisor.repository.GroupPrivilegesRepository;
import top.leafage.hypervisor.repository.PrivilegeRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


/**
 * group privileges service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class GroupPrivilegesServiceImplTest {

    @Mock
    private GroupPrivilegesRepository groupPrivilegesRepository;

    @Mock
    private PrivilegeRepository privilegeRepository;

    @Mock
    private GroupAuthoritiesRepository groupAuthoritiesRepository;

    @InjectMocks
    private GroupPrivilegesServiceImpl groupPrivilegesService;

    private Privilege privilege;
    private GroupAuthorities groupAuthority;
    private GroupPrivileges groupPrivileges;

    @BeforeEach
    void setUp() {
        privilege = new Privilege();
        privilege.setName("test");
        privilege.setActions(Set.of("create"));

        groupAuthority = new GroupAuthorities(1L, "test");

        groupPrivileges = new GroupPrivileges(1L, 1L, 1L, Set.of("test"));
    }

    @Test
    void privileges() {
        when(groupPrivilegesRepository.findAllByGroupId(anyLong())).thenReturn(List.of(mock(GroupPrivileges.class)));

        List<GroupPrivileges> members = groupPrivilegesService.privileges(1L);
        assertEquals(1, members.size());
        verify(groupPrivilegesRepository).findAllByGroupId(anyLong());
    }

    @Test
    void groups() {
        when(groupPrivilegesRepository.findAllByPrivilegeId(anyLong())).thenReturn(List.of(mock(GroupPrivileges.class)));

        List<GroupPrivileges> groups = groupPrivilegesService.groups(1L);
        assertEquals(1, groups.size());
        verify(groupPrivilegesRepository).findAllByPrivilegeId(anyLong());
    }

    @Test
    void relation() {
        when(privilegeRepository.findById(anyLong())).thenReturn(Optional.of(privilege));
        when(groupAuthoritiesRepository.findByGroupIdAndAuthority(anyLong(), anyString())).thenReturn(Optional.of(groupAuthority));
        when(groupAuthoritiesRepository.saveAll(anyCollection())).thenReturn(Collections.singletonList(groupAuthority));
        when(groupPrivilegesRepository.saveAndFlush(any(GroupPrivileges.class))).thenReturn(groupPrivileges);

        GroupPrivileges relation = groupPrivilegesService.relation(1L, 2L, "test");
        assertEquals(1, relation.getGroupId());
        verify(groupPrivilegesRepository).saveAndFlush(any());
    }

    @Test
    void removeRelation() {
        when(groupPrivilegesRepository.findByGroupIdAndPrivilegeId(anyLong(), anyLong())).thenReturn(Optional.of(groupPrivileges));
        when(privilegeRepository.findById(anyLong())).thenReturn(Optional.of(privilege));

        groupPrivilegesService.removeRelation(1L, 2L, "test");
        verify(groupAuthoritiesRepository).deleteByGroupIdAndAuthority(anyLong(), anyString());
    }

    @Test
    void removeRelation_empty_action() {
        when(groupPrivilegesRepository.findByGroupIdAndPrivilegeId(anyLong(), anyLong())).thenReturn(Optional.of(groupPrivileges));
        when(privilegeRepository.findById(anyLong())).thenReturn(Optional.of(privilege));

        groupPrivilegesService.removeRelation(1L, 2L, "");
        verify(groupPrivilegesRepository).deleteById(anyLong());
        verify(groupAuthoritiesRepository).deleteByGroupIdAndAuthorityStartingWith(anyLong(), anyString());
    }
}