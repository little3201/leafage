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

import io.leafage.system.domain.Group;
import io.leafage.system.dto.GroupDTO;
import io.leafage.system.repository.GroupRepository;
import io.leafage.system.vo.GroupVO;
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
import top.leafage.common.TreeNode;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * group common test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupServiceImpl groupService;

    private GroupDTO groupDTO;

    @BeforeEach
    void setUp() {
        groupDTO = new GroupDTO();
        groupDTO.setName("group");
    }

    @Test
    void retrieve() {
        Page<Group> page = new PageImpl<>(List.of(Mockito.mock(Group.class)));

        given(this.groupRepository.findAllBySuperiorId(Mockito.anyLong(), Mockito.any(Pageable.class))).willReturn(page);

        Page<GroupVO> voPage = groupService.retrieve(0, 2, "id", true, "test");
        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void retrieve_superiorIdIsNull() {
        Page<Group> page = new PageImpl<>(List.of(Mockito.mock(Group.class)));

        given(this.groupRepository.findAllBySuperiorIdIsNull(Mockito.any(Pageable.class))).willReturn(page);

        Page<GroupVO> voPage = groupService.retrieve(0, 2, "id", true, "test");
        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void tree() {
        given(this.groupRepository.findAll()).willReturn(Arrays.asList(Mockito.mock(Group.class), Mockito.mock(Group.class)));

        List<TreeNode<Long>> nodes = groupService.tree();
        Assertions.assertNotNull(nodes);
    }

    @Test
    void fetch() {
        given(this.groupRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(Mockito.mock(Group.class)));

        GroupVO groupVO = groupService.fetch(Mockito.anyLong());

        Assertions.assertNotNull(groupVO);
    }

    @Test
    void create() {
        given(this.groupRepository.save(Mockito.any(Group.class))).willReturn(Mockito.mock(Group.class));

        GroupVO groupVO = groupService.create(Mockito.mock(GroupDTO.class));

        verify(this.groupRepository, times(1)).save(Mockito.any(Group.class));
        Assertions.assertNotNull(groupVO);
    }

    @Test
    void create_error() {
        given(this.groupRepository.save(Mockito.any(Group.class))).willThrow(new RuntimeException());

        Assertions.assertThrows(RuntimeException.class, () -> groupService.create(Mockito.mock(GroupDTO.class)));
    }

    @Test
    void modify() {
        given(this.groupRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(Mockito.mock(Group.class)));

        given(this.groupRepository.save(Mockito.any(Group.class))).willReturn(Mockito.mock(Group.class));

        GroupVO groupVO = groupService.modify(1L, groupDTO);

        verify(this.groupRepository, times(1)).save(Mockito.any(Group.class));
        Assertions.assertNotNull(groupVO);
    }

    @Test
    void remove() {
        groupService.remove(1L);

        verify(this.groupRepository, times(1)).deleteById(Mockito.anyLong());
    }

}