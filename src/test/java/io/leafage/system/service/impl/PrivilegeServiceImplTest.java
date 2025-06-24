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

import com.server.starter.domain.TreeNode;
import io.leafage.system.domain.Privilege;
import io.leafage.system.domain.RoleMembers;
import io.leafage.system.domain.RolePrivileges;
import io.leafage.system.repository.PrivilegeRepository;
import io.leafage.system.repository.RoleMembersRepository;
import io.leafage.system.repository.RolePrivilegesRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

/**
 * privilege common test
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

    @InjectMocks
    private PrivilegeServiceImpl privilegeService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void tree() {
        given(this.roleMembersRepository.findAllByUsername(Mockito.anyString())).willReturn(Collections.singletonList(Mockito.mock(RoleMembers.class)));

        given(this.rolePrivilegesRepository.findAllByRoleId(Mockito.anyLong())).willReturn(Collections.singletonList(Mockito.mock(RolePrivileges.class)));

        given(this.privilegeRepository.findById(Mockito.anyLong())).willReturn(Optional.of(Mockito.mock(Privilege.class)));

        List<TreeNode> nodes = privilegeService.tree("test");
        Assertions.assertNotNull(nodes);
    }

}