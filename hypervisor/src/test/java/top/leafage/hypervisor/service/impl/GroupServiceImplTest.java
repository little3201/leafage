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

package top.leafage.hypervisor.service.impl;

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
import top.leafage.hypervisor.domain.Group;
import top.leafage.hypervisor.domain.dto.GroupDTO;
import top.leafage.hypervisor.repository.GroupRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * group service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupServiceImpl groupService;

    @Mock
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    private GroupDTO dto;
    private Group entity;

    @BeforeEach
    void setUp() {
        dto = new GroupDTO();
        dto.setName("test");
        dto.setDescription("description");

        entity = GroupDTO.toEntity(dto);
    }

    @Test
    void retrieve_page() {
        ReactiveSelectOperation.ReactiveSelect<Group> select = mock(ReactiveSelectOperation.ReactiveSelect.class);
        ReactiveSelectOperation.TerminatingSelect<Group> terminating = mock(ReactiveSelectOperation.TerminatingSelect.class);

        given(r2dbcEntityTemplate.select(Group.class)).willReturn(select);
        given(select.matching(any(Query.class))).willReturn(terminating);
        given(terminating.all()).willReturn(Flux.just(entity));
        given(r2dbcEntityTemplate.count(any(Query.class), eq(Group.class))).willReturn(Mono.just(1L));

        StepVerifier.create(groupService.retrieve(0, 2, "id", true, "name:like:test")).assertNext(page -> {
            assertThat(page.getContent()).hasSize(1);
            AssertionsForClassTypes.assertThat(page.getTotalElements()).isEqualTo(1);
            AssertionsForClassTypes.assertThat(page.getNumber()).isEqualTo(0);
            AssertionsForClassTypes.assertThat(page.getSize()).isEqualTo(2);
        }).verifyComplete();
    }

    @Test
    void retrieve() {
        given(this.groupRepository.findAllById(anyList())).willReturn(Flux.just(mock(Group.class)));

        StepVerifier.create(groupService.retrieve(List.of(1L))).expectNextCount(1).verifyComplete();
    }

    @Test
    void retrieve_ids_null() {
        given(this.groupRepository.findAll()).willReturn(Flux.just(mock(Group.class)));

        StepVerifier.create(groupService.retrieve(null)).expectNextCount(1).verifyComplete();
    }

    @Test
    void fetch() {
        given(this.groupRepository.findById(anyLong())).willReturn(Mono.just(mock(Group.class)));

        StepVerifier.create(groupService.fetch(anyLong())).expectNextCount(1).verifyComplete();
    }

    @Test
    void create() {
        given(this.groupRepository.save(any(Group.class))).willReturn(Mono.just(mock(Group.class)));

        StepVerifier.create(groupService.create(mock(GroupDTO.class))).expectNextCount(1).verifyComplete();
    }

    @Test
    void modify() {
        given(this.groupRepository.findById(anyLong())).willReturn(Mono.just(mock(Group.class)));

        given(this.groupRepository.save(any(Group.class))).willReturn(Mono.just(mock(Group.class)));

        StepVerifier.create(groupService.modify(anyLong(), dto)).expectNextCount(1).verifyComplete();
    }

    @Test
    void remove() {
        given(this.groupRepository.deleteById(anyLong())).willReturn(Mono.empty());

        StepVerifier.create(groupService.remove(anyLong())).verifyComplete();
    }

}