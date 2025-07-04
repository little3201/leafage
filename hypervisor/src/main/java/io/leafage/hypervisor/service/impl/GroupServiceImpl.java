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
import io.leafage.hypervisor.dto.GroupDTO;
import io.leafage.hypervisor.repository.GroupRepository;
import io.leafage.hypervisor.service.GroupService;
import io.leafage.hypervisor.vo.GroupVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.leafage.common.TreeNode;
import top.leafage.common.jdbc.JdbcTreeAndDomainConverter;

import java.util.List;

/**
 * group service impl.
 *
 * @author wq li
 */
@Service
public class GroupServiceImpl extends JdbcTreeAndDomainConverter<Group, Long> implements GroupService {

    private final GroupRepository groupRepository;

    /**
     * <p>Constructor for GroupServiceImpl.</p>
     *
     * @param groupRepository a {@link GroupRepository} object
     */
    public GroupServiceImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<GroupVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<Group> spec = (root, query, cb) ->
                buildPredicate(filters, cb, root).orElse(null);

        return groupRepository.findAll(spec, pageable)
                .map(group -> convertToVO(group, GroupVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TreeNode<Long>> tree() {
        List<Group> groups = groupRepository.findAll();
        return convertToTree(groups);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return groupRepository.findById(id)
                .map(group -> convertToVO(group, GroupVO.class)).orElse(null);
    }

    @Override
    public boolean enable(Long id) {
        return groupRepository.updateEnabledById(id) > 0;
    }

    @Override
    public boolean exists(String name, Long id) {
        Assert.hasText(name, String.format(_MUST_NOT_BE_EMPTY, "name"));
        if (id == null) {
            return groupRepository.existsByName(name);
        }
        return groupRepository.existsByNameAndIdNot(name, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupVO create(GroupDTO dto) {
        Group group = convertToDomain(dto, Group.class);

        groupRepository.saveAndFlush(group);
        return convertToVO(group, GroupVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupVO modify(Long id, GroupDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        return groupRepository.findById(id).map(existing -> {
                    Group group = convert(dto, existing);
                    group = groupRepository.save(group);
                    return convertToVO(group, GroupVO.class);
                })
                .orElseThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        groupRepository.deleteById(id);
    }

}
