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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.BDDMockito.given;

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

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setUsername("test");
        userDTO.setFamilyName("john");
        userDTO.setGivenName("steven");
        userDTO.setCredentialsExpiresAt(Instant.now());
    }

    @Test
    void retrieve() {
        given(this.userRepository.findAllBy(Mockito.any(PageRequest.class))).willReturn(Flux.just(Mockito.mock(User.class)));

        given(this.userRepository.count()).willReturn(Mono.just(2L));

        StepVerifier.create(userService.retrieve(0, 2, "id", true,"username:like:a")).expectNextCount(1).verifyComplete();
    }

    @Test
    void fetch() {
        given(this.userRepository.findById(Mockito.anyLong())).willReturn(Mono.just(Mockito.mock(User.class)));
        StepVerifier.create(userService.fetch(Mockito.anyLong())).expectNextCount(1).verifyComplete();
    }

    /**
     * 测试新增user
     */
    @Test
    void create() {
        given(this.userRepository.save(Mockito.any(User.class))).willReturn(Mono.just(Mockito.mock(User.class)));
        StepVerifier.create(userService.create(Mockito.mock(UserDTO.class))).expectNextCount(1).verifyComplete();
    }

    @Test
    void exists() {
        given(this.userRepository.existsByUsername(Mockito.anyString())).willReturn(Mono.just(Boolean.TRUE));

        StepVerifier.create(userService.exists("test", 1L)).expectNext(Boolean.TRUE).verifyComplete();
    }

    @Test
    void modify() {
        given(this.userRepository.findById(Mockito.anyLong())).willReturn(Mono.just(Mockito.mock(User.class)));

        given(this.userRepository.save(Mockito.any(User.class))).willReturn(Mono.just(Mockito.mock(User.class)));


        StepVerifier.create(userService.modify(Mockito.anyLong(), userDTO)).expectNextCount(1).verifyComplete();
    }

    @Test
    void remove() {
        given(this.userRepository.deleteById(Mockito.anyLong())).willReturn(Mono.empty());

        StepVerifier.create(userService.remove(Mockito.anyLong())).verifyComplete();
    }
}