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

import io.leafage.hypervisor.domain.GroupMembers;
import io.leafage.hypervisor.domain.GroupPrivileges;
import io.leafage.hypervisor.domain.Privilege;
import io.leafage.hypervisor.dto.PrivilegeDTO;
import io.leafage.hypervisor.repository.GroupMembersRepository;
import io.leafage.hypervisor.repository.GroupPrivilegesRepository;
import io.leafage.hypervisor.repository.PrivilegeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.BDDMockito.given;

/**
 * privilege service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class PrivilegeServiceImplTest {

    @Mock
    private PrivilegeRepository privilegeRepository;

    @Mock
    private GroupMembersRepository groupMembersRepository;

    @Mock
    private GroupPrivilegesRepository groupPrivilegesRepository;

    @InjectMocks
    private PrivilegeServiceImpl privilegeService;

    private PrivilegeDTO privilegeDTO;

    @BeforeEach
    void setUp() {
        privilegeDTO = new PrivilegeDTO();
        privilegeDTO.setName("test");
    }

    @Test
    void retrieve_page() {
        given(this.privilegeRepository.findAllBySuperiorIdIsNull(Mockito.any(PageRequest.class))).willReturn(Flux.just(Mockito.mock(Privilege.class)));

        given(this.privilegeRepository.countBySuperiorId(Mockito.anyLong())).willReturn(Mono.just(Mockito.anyLong()));

        StepVerifier.create(privilegeService.retrieve(0, 2, "id", true,"name:like:a"))
                .expectNextCount(1).verifyComplete();
    }

    @Test
    void retrieve() {
        given(this.privilegeRepository.findAllById(Mockito.anyList())).willReturn(Flux.just(Mockito.mock(Privilege.class)));

        StepVerifier.create(privilegeService.retrieve(List.of(1L)))
                .expectNextCount(1).verifyComplete();
    }

    @Test
    void retrieve_ids_null() {
        given(this.privilegeRepository.findAll()).willReturn(Flux.just(Mockito.mock(Privilege.class)));

        StepVerifier.create(privilegeService.retrieve(null)).expectNextCount(1).verifyComplete();
    }

    @Test
    void fetch() {
        given(this.privilegeRepository.findById(Mockito.anyLong())).willReturn(Mono.just(Mockito.mock(Privilege.class)));

        StepVerifier.create(privilegeService.fetch(1L)).expectNextCount(1).verifyComplete();
    }

    @Test
    void fetch_no_superior() {
        given(this.privilegeRepository.findById(Mockito.anyLong())).willReturn(Mono.just(Mockito.mock(Privilege.class)));

        StepVerifier.create(privilegeService.fetch(1L)).expectNextCount(1).verifyComplete();
    }

    @Test
    void create() {
        given(this.privilegeRepository.save(Mockito.any(Privilege.class))).willReturn(Mono.just(Mockito.mock(Privilege.class)));

        StepVerifier.create(privilegeService.create(privilegeDTO)).expectNextCount(1).verifyComplete();
    }


    @Test
    void create_no_superior() {
        given(this.privilegeRepository.save(Mockito.any(Privilege.class))).willReturn(Mono.just(Mockito.mock(Privilege.class)));

        StepVerifier.create(privilegeService.create(privilegeDTO)).expectNextCount(1).verifyComplete();
    }

    @Test
    void modify() {
        given(this.privilegeRepository.findById(Mockito.anyLong())).willReturn(Mono.just(Mockito.mock(Privilege.class)));

        given(this.privilegeRepository.save(Mockito.any(Privilege.class))).willReturn(Mono.just(Mockito.mock(Privilege.class)));

        StepVerifier.create(privilegeService.modify(1L, privilegeDTO)).expectNextCount(1).verifyComplete();
    }

    @Test
    void tree() {
        given(this.groupMembersRepository.findByUsername(Mockito.anyString())).willReturn(Flux.just(Mockito.mock(GroupMembers.class)));

        given(this.groupPrivilegesRepository.findByGroupId(Mockito.anyLong())).willReturn(Flux.just(Mockito.mock(GroupPrivileges.class)));

        given(this.privilegeRepository.findById(Mockito.anyLong())).willReturn(Mono.just(Mockito.mock(Privilege.class)));

        StepVerifier.create(privilegeService.tree("test")).expectNextCount(1).verifyComplete();
    }

    @Test
    void exists() {
        given(this.privilegeRepository.existsByName(Mockito.anyString())).willReturn(Mono.just(Boolean.TRUE));

        StepVerifier.create(privilegeService.exists("test", 1L)).expectNext(Boolean.TRUE).verifyComplete();
    }
}