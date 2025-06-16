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

import io.leafage.hypervisor.domain.*;
import io.leafage.hypervisor.dto.PrivilegeDTO;
import io.leafage.hypervisor.repository.*;
import io.leafage.hypervisor.vo.PrivilegeVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import top.leafage.common.TreeNode;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
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

    private PrivilegeDTO privilegeDTO;

    @BeforeEach
    void init() {
        privilegeDTO = new PrivilegeDTO();
        privilegeDTO.setName("西安市");
        privilegeDTO.setIcon("user");
        privilegeDTO.setPath("/user");
        privilegeDTO.setSuperiorId(1L);
    }

    @Test
    void retrieve() {
        Page<Privilege> page = new PageImpl<>(List.of(new Privilege()));

        given(this.privilegeRepository.findAll(ArgumentMatchers.<Specification<Privilege>>any(),
                Mockito.any(Pageable.class))).willReturn(page);

        Page<PrivilegeVO> voPage = privilegeService.retrieve(0, 2, "id", true, null);
        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        given(this.privilegeRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(Mockito.mock(Privilege.class)));

        PrivilegeVO vo = privilegeService.fetch(1L);

        Assertions.assertNotNull(vo);
    }

    @Test
    void modify() {
        given(this.privilegeRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(Mockito.mock(Privilege.class)));

        given(this.privilegeRepository.save(Mockito.any(Privilege.class))).willReturn(Mockito.mock(Privilege.class));

        PrivilegeVO vo = privilegeService.modify(Mockito.anyLong(), privilegeDTO);

        verify(this.privilegeRepository, times(1)).save(Mockito.any(Privilege.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void tree() {
        given(this.groupMembersRepository.findAllByUsername(Mockito.anyString())).willReturn(Collections.singletonList(Mockito.mock(GroupMembers.class)));

        given(this.groupRolesRepository.findAllByGroupId(Mockito.anyLong())).willReturn(Collections.singletonList(Mockito.mock(GroupRoles.class)));

        given(this.groupPrivilegesRepository.findAllByGroupId(Mockito.anyLong())).willReturn(Collections.singletonList(Mockito.mock(GroupPrivileges.class)));

        given(this.roleMembersRepository.findAllByUsername(Mockito.anyString())).willReturn(Collections.singletonList(Mockito.mock(RoleMembers.class)));

        given(this.rolePrivilegesRepository.findAllByRoleId(Mockito.anyLong())).willReturn(Collections.singletonList(Mockito.mock(RolePrivileges.class)));

        given(this.privilegeRepository.findById(Mockito.anyLong())).willReturn(Optional.of(Mockito.mock(Privilege.class)));

        List<TreeNode<Long>> nodes = privilegeService.tree("test");
        Assertions.assertNotNull(nodes);
    }

    @Test
    void enable() {
        given(this.privilegeRepository.updateEnabledById(Mockito.anyLong())).willReturn(1);

        boolean enabled = privilegeService.enable(1L);

        Assertions.assertTrue(enabled);
    }

}