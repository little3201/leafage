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
package io.leafage.system.service.impl;

import io.leafage.system.domain.User;
import io.leafage.system.dto.UserDTO;
import io.leafage.system.repository.UserRepository;
import io.leafage.system.vo.UserVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * user common test
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
    private User user;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setUsername("test");
        userDTO.setFullName("zhang");
        userDTO.setAvatar("a.jpg");
        userDTO.setEmail("zhang@test.com");

        user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setLastModifiedDate(Instant.now());
    }

    @Test
    void retrieve() {
        Page<User> page = new PageImpl<>(List.of(user));

        given(this.userRepository.findAllByUsernameContaining(Mockito.any(String.class), Mockito.any(Pageable.class))).willReturn(page);

        Page<UserVO> voPage = userService.retrieve(0, 2, "id", true, "test");
        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void retrieve_username_null() {
        Page<User> page = new PageImpl<>(List.of(user));

        given(this.userRepository.findAll(Mockito.any(Pageable.class))).willReturn(page);

        Page<UserVO> voPage = userService.retrieve(0, 2, "id", true, null);
        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        given(this.userRepository.findById(Mockito.anyLong())).willReturn(Optional.of(user));

        UserVO vo = userService.fetch(Mockito.anyLong());

        Assertions.assertNotNull(vo);
    }

    @Test
    void fetch_error() {
        given(this.userRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(Mockito.mock(User.class)));

        UserVO vo = userService.fetch(Mockito.anyLong());

        Assertions.assertNotNull(vo);
    }

    @Test
    void findByUsername() {
        given(this.userRepository.findByUsername(Mockito.anyString())).willReturn(Optional.ofNullable(Mockito.mock(User.class)));

        UserVO vo = userService.findByUsername(Mockito.anyString());

        Assertions.assertNotNull(vo);
    }

    @Test
    void exists() {
        given(this.userRepository.existsByUsernameAndIdNot(Mockito.anyString(), Mockito.anyLong())).willReturn(Boolean.TRUE);

        boolean exists = userService.exists("test", 1L);

        Assertions.assertTrue(exists);
    }

    @Test
    void exist_false() {
        given(this.userRepository.existsByUsernameAndIdNot(Mockito.anyString(), Mockito.anyLong())).willReturn(false);

        boolean exists = userService.exists("test", 1L);

        Assertions.assertFalse(exists);
    }

    @Test
    void create() {
        given(this.userRepository.save(Mockito.any(User.class))).willReturn(Mockito.mock(User.class));

        UserVO vo = userService.create(userDTO);

        verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void modify() {
        // 根据id查询信息
        given(this.userRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(Mockito.mock(User.class)));

        // 保存更新信息
        given(this.userRepository.save(Mockito.any(User.class))).willReturn(Mockito.mock(User.class));

        UserVO vo = userService.modify(1L, userDTO);

        verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void remove() {
        userService.remove(Mockito.anyLong());

        verify(userRepository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }
}