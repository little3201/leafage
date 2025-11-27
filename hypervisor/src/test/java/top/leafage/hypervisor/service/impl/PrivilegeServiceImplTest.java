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

import io.leafage.hypervisor.domain.*;
import top.leafage.hypervisor.domain.*;
import top.leafage.hypervisor.domain.dto.PrivilegeDTO;
import top.leafage.hypervisor.domain.vo.PrivilegeVO;
import io.leafage.hypervisor.repository.*;
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
import top.leafage.common.TreeNode;
import top.leafage.hypervisor.repository.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;


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
                any(Pageable.class))).willReturn(page);

        Page<PrivilegeVO> voPage = privilegeService.retrieve(0, 2, "id", true, null);
        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        given(this.privilegeRepository.findById(anyLong())).willReturn(Optional.ofNullable(mock(Privilege.class)));

        PrivilegeVO vo = privilegeService.fetch(1L);

        Assertions.assertNotNull(vo);
    }

    @Test
    void subset() {
        given(this.privilegeRepository.findAllBySuperiorId(anyLong())).willReturn(List.of(mock(Privilege.class)));

        List<Privilege> list = privilegeService.subset(1L);

        Assertions.assertNotNull(list);
    }

    @Test
    void subset_empty() {
        given(this.privilegeRepository.findAllBySuperiorId(anyLong())).willReturn(Collections.emptyList());

        List<Privilege> list = privilegeService.subset(1L);

        Assertions.assertEquals(Collections.emptyList(), list);
    }

    @Test
    void modify() {
        given(this.privilegeRepository.findById(anyLong())).willReturn(Optional.ofNullable(mock(Privilege.class)));

        given(this.privilegeRepository.save(any(Privilege.class))).willReturn(mock(Privilege.class));

        PrivilegeVO vo = privilegeService.modify(anyLong(), privilegeDTO);

        verify(this.privilegeRepository, times(1)).save(any(Privilege.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void tree() {
        given(this.groupMembersRepository.findAllByUsername(anyString())).willReturn(Collections.singletonList(mock(GroupMembers.class)));

        given(this.groupRolesRepository.findAllByGroupId(anyLong())).willReturn(Collections.singletonList(mock(GroupRoles.class)));

        given(this.groupPrivilegesRepository.findAllByGroupId(anyLong())).willReturn(Collections.singletonList(mock(GroupPrivileges.class)));

        given(this.roleMembersRepository.findAllByUsername(anyString())).willReturn(Collections.singletonList(mock(RoleMembers.class)));

        given(this.rolePrivilegesRepository.findAllByRoleId(anyLong())).willReturn(Collections.singletonList(mock(RolePrivileges.class)));

        given(this.privilegeRepository.findById(anyLong())).willReturn(Optional.of(mock(Privilege.class)));

        List<TreeNode<Long>> nodes = privilegeService.tree("test");
        Assertions.assertNotNull(nodes);
    }

    @Test
    void enable() {
        given(this.privilegeRepository.updateEnabledById(anyLong())).willReturn(1);

        boolean enabled = privilegeService.enable(1L);

        Assertions.assertTrue(enabled);
    }

}