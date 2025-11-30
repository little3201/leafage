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

import jakarta.persistence.EntityNotFoundException;
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
import top.leafage.hypervisor.domain.User;
import top.leafage.hypervisor.domain.dto.UserDTO;
import top.leafage.hypervisor.domain.vo.UserVO;
import top.leafage.hypervisor.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.*;

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
    private User entity;

    @BeforeEach
    void setUp() {
        dto = new UserDTO();
        dto.setUsername("test");
        dto.setName("test");
        dto.setEmail("test@example.com");

        entity = UserDTO.toEntity(dto, "123");
    }

    @Test
    void retrieve() {
        Page<User> page = new PageImpl<>(List.of(entity));

        when(userRepository.findAll(ArgumentMatchers.<Specification<User>>any(),
                any(Pageable.class))).thenReturn(page);

        Page<UserVO> voPage = userService.retrieve(0, 2, "id", true, "username:like:test");
        assertEquals(1, voPage.getTotalElements());
        assertEquals(1, voPage.getContent().size());
        verify(userRepository).findAll(ArgumentMatchers.<Specification<User>>any(), any(Pageable.class));
    }

    @Test
    void fetch() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        UserVO vo = userService.fetch(anyLong());
        assertNotNull(vo);
        assertEquals("test", vo.username());
        verify(userRepository).findById(anyLong());
    }

    @Test
    void fetch_not_found() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.fetch(anyLong())
        );
        assertEquals("user not found: 0", exception.getMessage());
        verify(userRepository).findById(anyLong());
    }

    @Test
    void create() {
        when(userRepository.existsByUsername("test")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(entity);

        UserVO vo = userService.create(dto);
        assertNotNull(vo);
        assertEquals("test", vo.username());
        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    void create_username_conflict() {
        when(userRepository.existsByUsername("test")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.create(dto)
        );
        assertEquals("username already exists: test", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void modify() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(userRepository.existsByUsername("demo")).thenReturn(false);
        when(userRepository.existsByEmail("demo@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(entity);

        dto.setUsername("demo");
        dto.setEmail("demo@example.com");
        UserVO vo = userService.modify(1L, dto);
        assertNotNull(vo);
        assertEquals("demo", vo.username());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void modify_username_conflict() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(userRepository.existsByUsername("demo")).thenReturn(true);

        dto.setUsername("demo");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.modify(1L, dto)
        );
        assertEquals("username already exists: demo", exception.getMessage());
    }

    @Test
    void remove() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        userService.remove(anyLong());

        verify(userRepository).deleteById(anyLong());
    }

    @Test
    void remove_not_found() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.remove(anyLong())
        );
        assertEquals("user not found: 0", exception.getMessage());
    }

    @Test
    void enable() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.updateEnabledById(anyLong())).thenReturn(1);

        boolean enabled = userService.enable(1L);
        assertTrue(enabled);
    }

    @Test
    void enable_not_found() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.enable(1L)
        );
        assertEquals("user not found: 1", exception.getMessage());
    }

    @Test
    void unlock() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.updateAccountNonLockedById(anyLong())).thenReturn(1);

        boolean unlock = userService.unlock(1L);
        assertTrue(unlock);
    }

    @Test
    void unlock_not_found() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.unlock(1L)
        );
        assertEquals("user not found: 1", exception.getMessage());
    }
}