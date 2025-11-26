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

import io.leafage.hypervisor.domain.Group;
import io.leafage.hypervisor.domain.dto.GroupDTO;
import io.leafage.hypervisor.repository.GroupRepository;
import io.leafage.hypervisor.domain.vo.GroupVO;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;


/**
 * group service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupServiceImpl groupService;

    private GroupDTO dto;

    @BeforeEach
    void setUp() {
        dto = new GroupDTO();
        dto.setName("group");
    }

    @Test
    void retrieve() {
        Page<Group> page = new PageImpl<>(List.of(mock(Group.class)));

        given(this.groupRepository.findAll(ArgumentMatchers.<Specification<Group>>any(),
                any(Pageable.class))).willReturn(page);

        Page<GroupVO> voPage = groupService.retrieve(0, 2, "id", true, "filter_superiorId:=:2L,filter_name:like:test");
        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void tree() {
        given(this.groupRepository.findAll()).willReturn(Arrays.asList(mock(Group.class), mock(Group.class)));

        List<TreeNode<Long>> nodes = groupService.tree();
        Assertions.assertNotNull(nodes);
    }

    @Test
    void fetch() {
        given(this.groupRepository.findById(anyLong())).willReturn(Optional.ofNullable(mock(Group.class)));

        GroupVO vo = groupService.fetch(anyLong());

        Assertions.assertNotNull(vo);
    }

    @Test
    void exists() {
        given(this.groupRepository.existsByNameAndIdNot(anyString(),
                anyLong())).willReturn(true);

        boolean exists = groupService.exists("test", 2L);

        Assertions.assertTrue(exists);
    }

    @Test
    void exists_id_null() {
        given(this.groupRepository.existsByName(anyString())).willReturn(true);

        boolean exists = groupService.exists("test", null);

        Assertions.assertTrue(exists);
    }

    @Test
    void create() {
        given(this.groupRepository.saveAndFlush(any(Group.class))).willReturn(mock(Group.class));

        GroupVO vo = groupService.create(mock(GroupDTO.class));

        verify(this.groupRepository, times(1)).saveAndFlush(any(Group.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void create_error() {
        given(this.groupRepository.saveAndFlush(any(Group.class))).willThrow(new RuntimeException());

        Assertions.assertThrows(RuntimeException.class, () -> groupService.create(mock(GroupDTO.class)));
    }

    @Test
    void modify() {
        given(this.groupRepository.findById(anyLong())).willReturn(Optional.ofNullable(mock(Group.class)));

        given(this.groupRepository.save(any(Group.class))).willReturn(mock(Group.class));

        GroupVO vo = groupService.modify(1L, dto);

        verify(this.groupRepository, times(1)).save(any(Group.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void remove() {
        groupService.remove(1L);

        verify(this.groupRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void enable() {
        given(this.groupRepository.updateEnabledById(anyLong())).willReturn(1);

        boolean enabled = groupService.enable(1L);

        Assertions.assertTrue(enabled);
    }

}