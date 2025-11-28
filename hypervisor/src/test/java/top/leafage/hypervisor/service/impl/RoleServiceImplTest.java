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
import top.leafage.hypervisor.domain.Role;
import top.leafage.hypervisor.domain.dto.RoleDTO;
import top.leafage.hypervisor.domain.vo.RoleVO;
import top.leafage.hypervisor.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * role service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private RoleDTO dto;

    @BeforeEach
    void setUp() {
        dto = new RoleDTO();
        dto.setName("role");
        dto.setDescription("role");
    }

    @Test
    void retrieve() {
        Page<Role> page = new PageImpl<>(List.of(mock(Role.class)));

        given(this.roleRepository.findAll(ArgumentMatchers.<Specification<Role>>any(),
                any(Pageable.class))).willReturn(page);

        Page<RoleVO> voPage = roleService.retrieve(0, 2, "id", true, "test");
        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        given(this.roleRepository.findById(anyLong())).willReturn(Optional.of(mock(Role.class)));

        RoleVO vo = roleService.fetch(anyLong());

        Assertions.assertNotNull(vo);
    }

    @Test
    void exists() {
        given(this.roleRepository.existsByNameAndIdNot(anyString(),
                anyLong())).willReturn(true);

        boolean exists = roleService.exists("test", 2L);

        Assertions.assertTrue(exists);
    }

    @Test
    void exists_id_null() {
        given(this.roleRepository.existsByName(anyString())).willReturn(true);

        boolean exists = roleService.exists("test", null);

        Assertions.assertTrue(exists);
    }

    @Test
    void create() {
        given(this.roleRepository.saveAndFlush(any(Role.class))).willReturn(mock(Role.class));

        RoleVO vo = roleService.create(mock(RoleDTO.class));

        verify(this.roleRepository, times(1)).saveAndFlush(any(Role.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void modify() {
        given(this.roleRepository.findById(anyLong())).willReturn(Optional.of(mock(Role.class)));

        given(this.roleRepository.save(any(Role.class))).willReturn(mock(Role.class));

        RoleVO vo = roleService.modify(1L, dto);

        verify(this.roleRepository, times(1)).save(any(Role.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void remove() {
        roleService.remove(anyLong());

        verify(this.roleRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void enable() {
        given(this.roleRepository.updateEnabledById(anyLong())).willReturn(1);

        boolean enabled = roleService.enable(1L);

        Assertions.assertTrue(enabled);
    }

}