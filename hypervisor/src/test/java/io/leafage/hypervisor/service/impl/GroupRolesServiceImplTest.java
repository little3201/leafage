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

import io.leafage.hypervisor.domain.GroupRoles;
import io.leafage.hypervisor.repository.GroupRolesRepository;
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

    @Test
    void roles() {
        given(this.groupRolesRepository.findAllByGroupId(Mockito.anyLong())).willReturn(List.of(Mockito.mock(GroupRoles.class)));

        List<GroupRoles> members = groupRolesService.roles(1L);
        Assertions.assertNotNull(members);
    }

    @Test
    void groups() {
        given(this.groupRolesRepository.findAllByRoleId(Mockito.anyLong())).willReturn(List.of(Mockito.mock(GroupRoles.class)));

        List<GroupRoles> groups = groupRolesService.groups(1L);
        Assertions.assertNotNull(groups);
    }

    @Test
    void relation() {
        given(this.groupRolesRepository.saveAllAndFlush(Mockito.anyCollection())).willReturn(Mockito.anyList());

        List<GroupRoles> relation = groupRolesService.relation(1L, Set.of(1L));

        verify(this.groupRolesRepository, times(1)).saveAllAndFlush(Mockito.anyList());
        Assertions.assertNotNull(relation);
    }
}