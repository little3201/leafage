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

import io.leafage.hypervisor.domain.dto.UserDTO;
import io.leafage.hypervisor.repository.UserRepository;
import io.leafage.hypervisor.domain.vo.UserVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * user service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDTO dto;

    @BeforeEach
    void setUp() {
        dto = new UserDTO();
        dto.setUsername("test");
        dto.setName("zhangsan");
        dto.setAvatar("a.jpg");
        dto.setEmail("zhang@test.com");
    }

    @Test
    void retrieve() {
        Page<User> page = new PageImpl<>(List.of(mock(User.class)));

        given(this.userRepository.findAll(ArgumentMatchers.<Specification<User>>any(),
                any(Pageable.class))).willReturn(page);

        Page<UserVO> voPage = userService.retrieve(0, 2, "id", true, "test");
        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        given(this.userRepository.findById(anyLong())).willReturn(Optional.ofNullable(mock(User.class)));

        UserVO vo = userService.fetch(anyLong());

        Assertions.assertNotNull(vo);
    }

    @Test
    void exists() {
        given(this.userRepository.existsByUsernameAndIdNot(anyString(),
                anyLong())).willReturn(true);

        boolean exists = userService.exists("test", 2L);

        Assertions.assertTrue(exists);
    }

    @Test
    void exists_id_null() {
        given(this.userRepository.existsByUsername(anyString())).willReturn(true);

        boolean exists = userService.exists("test", null);

        Assertions.assertTrue(exists);
    }

    @Test
    void create() {
        given(this.userRepository.saveAndFlush(any(User.class))).willReturn(mock(User.class));

        UserVO vo = userService.create(dto);

        verify(userRepository, times(1)).saveAndFlush(any(User.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void modify() {
        // 根据id查询信息
        given(this.userRepository.findById(anyLong())).willReturn(Optional.ofNullable(mock(User.class)));

        // 保存更新信息
        given(this.userRepository.save(any(User.class))).willReturn(mock(User.class));

        UserVO vo = userService.modify(1L, dto);

        verify(userRepository, times(1)).save(any(User.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void remove() {
        userService.remove(anyLong());

        verify(userRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void enable() {
        given(this.userRepository.updateEnabledById(anyLong())).willReturn(1);

        boolean enabled = userService.enable(1L);

        Assertions.assertTrue(enabled);
    }

}