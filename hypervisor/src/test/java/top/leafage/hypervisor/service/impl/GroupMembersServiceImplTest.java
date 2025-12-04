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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import top.leafage.hypervisor.domain.GroupMembers;
import top.leafage.hypervisor.repository.GroupMembersRepository;

import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * group members service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class GroupMembersServiceImplTest {

    @Mock
    private GroupMembersRepository groupMembersRepository;

    @InjectMocks
    private GroupMembersServiceImpl groupMembersService;

    @Test
    void members() {
        given(this.groupMembersRepository.findByGroupId(anyLong())).willReturn(Flux.just(mock(GroupMembers.class)));

        StepVerifier.create(groupMembersService.members(anyLong())).expectNextCount(1).verifyComplete();
    }

    @Test
    void groups() {
        given(this.groupMembersRepository.findByUsername(anyString())).willReturn(Flux.just(mock(GroupMembers.class)));

        StepVerifier.create(groupMembersService.groups("test")).expectNextCount(1).verifyComplete();
    }

    @Test
    void relation() {
        given(this.groupMembersRepository.save(any(GroupMembers.class))).willReturn(Mono.just(mock(GroupMembers.class)));

        StepVerifier.create(groupMembersService.relation(anyLong(), Set.of("test")))
                .expectNextCount(1).verifyComplete();
    }
}