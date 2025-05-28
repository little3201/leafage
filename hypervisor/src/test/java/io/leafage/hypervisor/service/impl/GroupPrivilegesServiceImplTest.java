/*
 *  Copyright 2018-2025 little3201.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.leafage.hypervisor.service.impl;

import io.leafage.hypervisor.domain.GroupAuthorities;
import io.leafage.hypervisor.domain.GroupPrivileges;
import io.leafage.hypervisor.domain.Privilege;
import io.leafage.hypervisor.repository.GroupAuthoritiesRepository;
import io.leafage.hypervisor.repository.GroupPrivilegesRepository;
import io.leafage.hypervisor.repository.PrivilegeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.BDDMockito.given;

/**
 * group privileges service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class GroupPrivilegesServiceImplTest {

    @Mock
    private PrivilegeRepository privilegeRepository;

    @Mock
    private GroupPrivilegesRepository groupPrivilegesRepository;

    @Mock
    private GroupAuthoritiesRepository groupAuthoritiesRepository;

    @InjectMocks
    private GroupPrivilegesServiceImpl groupRolesService;

    @Test
    void privileges() {
        given(this.groupPrivilegesRepository.findByGroupId(Mockito.anyLong())).willReturn(Flux.just(Mockito.mock(GroupPrivileges.class)));

        StepVerifier.create(groupRolesService.privileges(Mockito.anyLong())).expectNextCount(1).verifyComplete();
    }

    @Test
    void groups() {
        given(this.groupPrivilegesRepository.findByPrivilegeId(Mockito.anyLong())).willReturn(Flux.just(Mockito.mock(GroupPrivileges.class)));

        StepVerifier.create(groupRolesService.groups(Mockito.anyLong())).expectNextCount(1).verifyComplete();
    }

    @Test
    void relation() {
        given(this.privilegeRepository.findById(Mockito.anyLong())).willReturn(Mono.just(Mockito.mock(Privilege.class)));

        given(this.groupAuthoritiesRepository.saveAll(Mockito.anyList())).willReturn(Flux.just(Mockito.mock(GroupAuthorities.class)));

        given(this.groupPrivilegesRepository.save(Mockito.any(GroupPrivileges.class))).willReturn(Mono.just(Mockito.mock(GroupPrivileges.class)));

        StepVerifier.create(groupRolesService.relation(1L, 2L, Mockito.anySet()))
                .expectNextCount(1).verifyComplete();
    }
}