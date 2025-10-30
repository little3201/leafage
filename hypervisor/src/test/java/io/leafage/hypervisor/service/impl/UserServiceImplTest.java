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

import io.leafage.hypervisor.domain.User;
import io.leafage.hypervisor.dto.UserDTO;
import io.leafage.hypervisor.repository.UserRepository;
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

import java.time.Instant;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * user接口测试
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    private UserDTO dto;
    private User entity;

    @BeforeEach
    void setUp() {
        dto = new UserDTO();
        dto.setUsername("test");
        dto.setFullname("john steven");
        dto.setCredentialsExpiresAt(Instant.now());

        entity = new User();
        entity.setUsername("test");
        entity.setFullname("john steven");
        entity.setCredentialsExpiresAt(Instant.now());
    }

    @Test
    void retrieve() {
        ReactiveSelectOperation.ReactiveSelect<User> select = mock(ReactiveSelectOperation.ReactiveSelect.class);
        ReactiveSelectOperation.TerminatingSelect<User> terminating = mock(ReactiveSelectOperation.TerminatingSelect.class);

        given(r2dbcEntityTemplate.select(User.class)).willReturn(select);
        given(select.matching(any(Query.class))).willReturn(terminating);
        given(terminating.all()).willReturn(Flux.just(entity));
        given(r2dbcEntityTemplate.count(any(Query.class), eq(User.class))).willReturn(Mono.just(1L));

        StepVerifier.create(userService.retrieve(0, 2, "id", true, "username:like:a"))
                .assertNext(page -> {
                    assertThat(page.getContent()).hasSize(1);
                    assertThat(page.getTotalElements()).isEqualTo(1);
                    assertThat(page.getNumber()).isEqualTo(0);
                    assertThat(page.getSize()).isEqualTo(2);
                }).verifyComplete();
    }

    @Test
    void fetch() {
        given(this.userRepository.findById(anyLong())).willReturn(Mono.just(mock(User.class)));
        StepVerifier.create(userService.fetch(anyLong())).expectNextCount(1).verifyComplete();
    }

    /**
     * 测试新增user
     */
    @Test
    void create() {
        given(this.userRepository.save(any(User.class))).willReturn(Mono.just(mock(User.class)));
        StepVerifier.create(userService.create(mock(UserDTO.class))).expectNextCount(1).verifyComplete();
    }

    @Test
    void exists() {
        given(this.userRepository.existsByUsernameAndIdNot(anyString(), anyLong())).willReturn(Mono.just(Boolean.TRUE));

        StepVerifier.create(userService.exists("test", 1L)).expectNext(Boolean.TRUE).verifyComplete();
    }

    @Test
    void exists_id_null() {
        given(this.userRepository.existsByUsername(anyString())).willReturn(Mono.just(Boolean.TRUE));

        StepVerifier.create(userService.exists("test", null)).expectNext(Boolean.TRUE).verifyComplete();
    }

    @Test
    void modify() {
        given(this.userRepository.findById(anyLong())).willReturn(Mono.just(mock(User.class)));

        given(this.userRepository.save(any(User.class))).willReturn(Mono.just(mock(User.class)));


        StepVerifier.create(userService.modify(anyLong(), dto)).expectNextCount(1).verifyComplete();
    }

    @Test
    void remove() {
        given(this.userRepository.deleteById(anyLong())).willReturn(Mono.empty());

        StepVerifier.create(userService.remove(anyLong())).verifyComplete();
    }
}