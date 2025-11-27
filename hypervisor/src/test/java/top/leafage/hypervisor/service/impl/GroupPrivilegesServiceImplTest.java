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
import top.leafage.hypervisor.domain.GroupPrivileges;
import top.leafage.hypervisor.domain.Privilege;
import top.leafage.hypervisor.repository.GroupAuthoritiesRepository;
import top.leafage.hypervisor.repository.GroupPrivilegesRepository;
import top.leafage.hypervisor.repository.PrivilegeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;


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

    @Test
    void privileges() {
        given(this.groupPrivilegesRepository.findAllByGroupId(anyLong())).willReturn(List.of(mock(GroupPrivileges.class)));

        List<GroupPrivileges> members = groupPrivilegesService.privileges(1L);
        Assertions.assertNotNull(members);
    }

    @Test
    void groups() {
        given(this.groupPrivilegesRepository.findAllByPrivilegeId(anyLong())).willReturn(List.of(mock(GroupPrivileges.class)));

        List<GroupPrivileges> groups = groupPrivilegesService.groups(1L);
        Assertions.assertNotNull(groups);
    }

    @Test
    void relation() {
        given(this.groupPrivilegesRepository.findByGroupIdAndPrivilegeId(anyLong(), anyLong())).willReturn(Optional.of(mock(GroupPrivileges.class)));

        Privilege privilege = new Privilege();
        privilege.setId(2L);
        privilege.setName("test");
        given(this.privilegeRepository.findById(anyLong())).willReturn(Optional.of(privilege));

        GroupAuthorities groupAuthority = new GroupAuthorities();
        groupAuthority.setId(1L);
        groupAuthority.setGroupId(1L);
        groupAuthority.setAuthority("test");
        given(this.groupAuthoritiesRepository.findByGroupIdAndAuthority(anyLong(), anyString())).willReturn(Optional.of(groupAuthority));

        given(this.groupAuthoritiesRepository.saveAll(anyCollection())).willReturn(Collections.singletonList(groupAuthority));

        given(this.groupPrivilegesRepository.saveAndFlush(any(GroupPrivileges.class))).willReturn(mock(GroupPrivileges.class));

        GroupPrivileges relation = groupPrivilegesService.relation(1L, 2L, "test");

        verify(this.groupPrivilegesRepository, times(1)).saveAndFlush(any());
        Assertions.assertNotNull(relation);
    }
}