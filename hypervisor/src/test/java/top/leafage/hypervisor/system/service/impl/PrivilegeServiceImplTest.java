/*
 * Copyright (c) 2026.  little3201.
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

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveSelectOperation;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import top.leafage.hypervisor.system.domain.GroupMembers;
import top.leafage.hypervisor.system.domain.GroupPrivileges;
import top.leafage.hypervisor.system.domain.Privilege;
import top.leafage.hypervisor.system.domain.dto.PrivilegeDTO;
import top.leafage.hypervisor.system.repository.GroupMembersRepository;
import top.leafage.hypervisor.system.repository.GroupPrivilegesRepository;
import top.leafage.hypervisor.system.repository.PrivilegeRepository;
import top.leafage.hypervisor.system.service.impl.PrivilegeServiceImpl;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

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

    @Mock
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    private PrivilegeDTO dto;
    private Privilege entity;

    @BeforeEach
    void setUp() {
        dto = new PrivilegeDTO();
        dto.setName("test");
        dto.setIcon("test");
        dto.setPath("/test");
        dto.setSuperiorId(1L);

        entity = PrivilegeDTO.toEntity(dto);
    }

    @Test
    void retrieve_page() {
        ReactiveSelectOperation.ReactiveSelect select = mock(ReactiveSelectOperation.ReactiveSelect.class);
        ReactiveSelectOperation.TerminatingSelect terminating = mock(ReactiveSelectOperation.TerminatingSelect.class);

        given(r2dbcEntityTemplate.select(Privilege.class)).willReturn(select);
        given(select.matching(any(Query.class))).willReturn(terminating);
        given(terminating.all()).willReturn(Flux.just(entity));
        given(this.privilegeRepository.countBySuperiorId(anyLong())).willReturn(Mono.just(0L));
        given(r2dbcEntityTemplate.count(any(Query.class), eq(Privilege.class))).willReturn(Mono.just(1L));

        StepVerifier.create(privilegeService.retrieve(0, 2, "id", true, "name:like:test"))
                .assertNext(page -> {
                    assertThat(page.getContent()).hasSize(1);
                    AssertionsForClassTypes.assertThat(page.getTotalElements()).isEqualTo(1);
                    AssertionsForClassTypes.assertThat(page.getNumber()).isEqualTo(0);
                    AssertionsForClassTypes.assertThat(page.getSize()).isEqualTo(2);
                }).verifyComplete();
    }

    @Test
    void retrieve() {
        given(this.privilegeRepository.findAllById(anyList())).willReturn(Flux.just(mock(Privilege.class)));

        StepVerifier.create(privilegeService.retrieve(List.of(1L)))
                .expectNextCount(1).verifyComplete();
    }

    @Test
    void retrieve_ids_null() {
        given(this.privilegeRepository.findAll()).willReturn(Flux.just(mock(Privilege.class)));

        StepVerifier.create(privilegeService.retrieve(null)).expectNextCount(1).verifyComplete();
    }

    @Test
    void fetch() {
        given(this.privilegeRepository.findById(anyLong())).willReturn(Mono.just(mock(Privilege.class)));

        StepVerifier.create(privilegeService.fetch(1L)).expectNextCount(1).verifyComplete();
    }

    @Test
    void fetch_no_superior() {
        given(this.privilegeRepository.findById(anyLong())).willReturn(Mono.just(mock(Privilege.class)));

        StepVerifier.create(privilegeService.fetch(1L)).expectNextCount(1).verifyComplete();
    }

    @Test
    void subset() {
        given(this.privilegeRepository.findAllBySuperiorId(anyLong())).willReturn(Flux.just(mock(Privilege.class)));

        StepVerifier.create(privilegeService.subset(1L)).expectNextCount(1).verifyComplete();
    }

    @Test
    void create() {
        given(this.privilegeRepository.save(any(Privilege.class))).willReturn(Mono.just(mock(Privilege.class)));

        StepVerifier.create(privilegeService.create(dto)).expectNextCount(1).verifyComplete();
    }


    @Test
    void create_no_superior() {
        given(this.privilegeRepository.save(any(Privilege.class))).willReturn(Mono.just(mock(Privilege.class)));

        StepVerifier.create(privilegeService.create(dto)).expectNextCount(1).verifyComplete();
    }

    @Test
    void modify() {
        given(this.privilegeRepository.findById(anyLong())).willReturn(Mono.just(mock(Privilege.class)));

        given(this.privilegeRepository.save(any(Privilege.class))).willReturn(Mono.just(mock(Privilege.class)));

        StepVerifier.create(privilegeService.modify(1L, dto)).expectNextCount(1).verifyComplete();
    }

    @Test
    void tree() {
        given(this.groupMembersRepository.findByUsername(anyString())).willReturn(Flux.just(mock(GroupMembers.class)));

        given(this.groupPrivilegesRepository.findByGroupId(anyLong())).willReturn(Flux.just(mock(GroupPrivileges.class)));

        given(this.privilegeRepository.findById(anyLong())).willReturn(Mono.just(mock(Privilege.class)));

        StepVerifier.create(privilegeService.tree("test")).expectNextCount(1).verifyComplete();
    }

}