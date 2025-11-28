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
import top.leafage.common.data.domain.TreeNode;
import top.leafage.hypervisor.domain.*;
import top.leafage.hypervisor.domain.dto.PrivilegeDTO;
import top.leafage.hypervisor.domain.vo.PrivilegeVO;
import top.leafage.hypervisor.repository.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


/**
 * privilege service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class PrivilegeServiceImplTest {

    @Mock
    private RoleMembersRepository roleMembersRepository;

    @Mock
    private RolePrivilegesRepository rolePrivilegesRepository;

    @Mock
    private PrivilegeRepository privilegeRepository;

    @Mock
    private GroupMembersRepository groupMembersRepository;

    @Mock
    private GroupRolesRepository groupRolesRepository;

    @Mock
    private GroupPrivilegesRepository groupPrivilegesRepository;

    @InjectMocks
    private PrivilegeServiceImpl privilegeService;

    private PrivilegeDTO dto;
    private Privilege entity;

    @BeforeEach
    void init() {
        dto = new PrivilegeDTO();
        dto.setName("test");
        dto.setIcon("test");
        dto.setPath("/test");
        dto.setSuperiorId(1L);

        entity = PrivilegeDTO.toEntity(dto);
    }

    @Test
    void retrieve() {
        Page<Privilege> page = new PageImpl<>(List.of(new Privilege()));

        when(privilegeRepository.findAll(ArgumentMatchers.<Specification<Privilege>>any(),
                any(Pageable.class))).thenReturn(page);

        Page<PrivilegeVO> voPage = privilegeService.retrieve(0, 2, "id", true, null);
        assertEquals(1, voPage.getTotalElements());
        assertEquals(1, voPage.getContent().size());
        verify(privilegeRepository).findAll(ArgumentMatchers.<Specification<Privilege>>any(), any(Pageable.class));
    }

    @Test
    void fetch() {
        when(privilegeRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        PrivilegeVO vo = privilegeService.fetch(1L);
        assertNotNull(vo);
        assertEquals("test", vo.name());
        verify(privilegeRepository).findById(anyLong());
    }

    @Test
    void subset() {
        when(privilegeRepository.findAllBySuperiorId(anyLong())).thenReturn(List.of(mock(Privilege.class)));

        List<PrivilegeVO> voList = privilegeService.subset(1L);
        assertEquals(1, voList.size());
    }

    @Test
    void subset_empty() {
        when(privilegeRepository.findAllBySuperiorId(anyLong())).thenReturn(Collections.emptyList());

        List<PrivilegeVO> voList = privilegeService.subset(1L);
        assertEquals(Collections.emptyList(), voList);
    }

    @Test
    void modify() {
        when(privilegeRepository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(privilegeRepository.existsByName("demo")).thenReturn(false);
        when(privilegeRepository.save(any(Privilege.class))).thenReturn(entity);

        dto.setName("demo");
        PrivilegeVO vo = privilegeService.modify(1L, dto);
        assertNotNull(vo);
        assertEquals("demo", vo.name());
        verify(privilegeRepository).save(any(Privilege.class));
    }

    @Test
    void modify_username_conflict() {
        when(privilegeRepository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(privilegeRepository.existsByName("demo")).thenReturn(true);

        dto.setName("demo");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> privilegeService.modify(1L, dto)
        );
        assertEquals("name already exists: demo", exception.getMessage());
    }

    @Test
    void tree() {
        when(groupMembersRepository.findAllByUsername(anyString())).thenReturn(Collections.singletonList(mock(GroupMembers.class)));
        when(groupRolesRepository.findAllByGroupId(anyLong())).thenReturn(Collections.singletonList(mock(GroupRoles.class)));
        when(groupPrivilegesRepository.findAllByGroupId(anyLong())).thenReturn(Collections.singletonList(mock(GroupPrivileges.class)));
        when(roleMembersRepository.findAllByUsername(anyString())).thenReturn(Collections.singletonList(mock(RoleMembers.class)));
        when(rolePrivilegesRepository.findAllByRoleId(anyLong())).thenReturn(Collections.singletonList(mock(RolePrivileges.class)));

        List<TreeNode<Long>> nodes = privilegeService.tree("test");
        assertNotNull(nodes);
    }

    @Test
    void enable() {
        when(privilegeRepository.existsById(anyLong())).thenReturn(true);
        when(privilegeRepository.updateEnabledById(anyLong())).thenReturn(1);

        boolean enabled = privilegeService.enable(1L);
        assertTrue(enabled);
    }

    @Test
    void enable_not_found() {
        when(privilegeRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> privilegeService.enable(1L)
        );
        assertEquals("privilege not found: 1", exception.getMessage());
    }
}