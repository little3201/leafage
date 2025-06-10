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

import io.leafage.hypervisor.domain.Role;
import io.leafage.hypervisor.dto.RoleDTO;
import io.leafage.hypervisor.repository.RoleRepository;
import io.leafage.hypervisor.vo.RoleVO;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        Page<Role> page = new PageImpl<>(List.of(Mockito.mock(Role.class)));

        given(this.roleRepository.findAll(Mockito.any(Pageable.class))).willReturn(page);

        Page<RoleVO> voPage = roleService.retrieve(0, 2, "id", true, "test");
        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        given(this.roleRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(Mockito.mock(Role.class)));

        RoleVO roleVO = roleService.fetch(Mockito.anyLong());

        Assertions.assertNotNull(roleVO);
    }

    @Test
    void exists() {
        given(this.roleRepository.existsByNameAndIdNot(Mockito.anyString(),
                Mockito.anyLong())).willReturn(true);

        boolean exists = roleService.exists("test", 2L);

        Assertions.assertTrue(exists);
    }

    @Test
    void exists_id_null() {
        given(this.roleRepository.existsByName(Mockito.anyString())).willReturn(true);

        boolean exists = roleService.exists("test", null);

        Assertions.assertTrue(exists);
    }

    @Test
    void create() {
        given(this.roleRepository.saveAndFlush(Mockito.any(Role.class))).willReturn(Mockito.mock(Role.class));

        RoleVO roleVO = roleService.create(Mockito.mock(RoleDTO.class));

        verify(this.roleRepository, times(1)).saveAndFlush(Mockito.any(Role.class));
        Assertions.assertNotNull(roleVO);
    }

    @Test
    void modify() {
        given(this.roleRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(Mockito.mock(Role.class)));

        given(this.roleRepository.save(Mockito.any(Role.class))).willReturn(Mockito.mock(Role.class));

        RoleVO roleVO = roleService.modify(1L, dto);

        verify(this.roleRepository, times(1)).save(Mockito.any(Role.class));
        Assertions.assertNotNull(roleVO);
    }

    @Test
    void remove() {
        roleService.remove(Mockito.anyLong());

        verify(this.roleRepository, times(1)).deleteById(Mockito.anyLong());
    }

}