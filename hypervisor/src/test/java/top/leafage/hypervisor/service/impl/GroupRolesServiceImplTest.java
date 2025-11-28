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
import top.leafage.hypervisor.domain.GroupRoles;
import top.leafage.hypervisor.repository.GroupRolesRepository;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


/**
 * group roles service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class GroupRolesServiceImplTest {

    @Mock
    private GroupRolesRepository groupRolesRepository;

    @InjectMocks
    private GroupRolesServiceImpl groupRolesService;

    private GroupRoles groupRoles;

    @BeforeEach
    void setUp() {
        groupRoles = new GroupRoles(1L, 1L);
    }

    @Test
    void roles() {
        when(groupRolesRepository.findAllByGroupId(anyLong())).thenReturn(List.of(mock(GroupRoles.class)));

        List<GroupRoles> members = groupRolesService.roles(1L);
        assertEquals(1, members.size());
        verify(groupRolesRepository).findAllByGroupId(anyLong());
    }

    @Test
    void groups() {
        when(groupRolesRepository.findAllByRoleId(anyLong())).thenReturn(List.of(mock(GroupRoles.class)));

        List<GroupRoles> groups = groupRolesService.groups(1L);
        assertEquals(1, groups.size());
        verify(groupRolesRepository).findAllByRoleId(anyLong());
    }

    @Test
    void relation() {
        when(groupRolesRepository.saveAllAndFlush(anyCollection())).thenReturn(List.of(mock(GroupRoles.class)));

        List<GroupRoles> relation = groupRolesService.relation(1L, Set.of(1L));
        assertEquals(1, relation.size());
        verify(groupRolesRepository).saveAllAndFlush(anyList());
    }

    @Test
    void removeRelation() {
        when(groupRolesRepository.findAllByGroupId(anyLong())).thenReturn(List.of(groupRoles));

        groupRolesService.removeRelation(1L, Set.of(1L));
        verify(groupRolesRepository).deleteAllByIdInBatch(anyCollection());
    }
}