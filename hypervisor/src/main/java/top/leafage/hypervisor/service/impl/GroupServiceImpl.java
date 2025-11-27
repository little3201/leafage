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

import top.leafage.hypervisor.domain.Group;
import top.leafage.hypervisor.domain.dto.GroupDTO;
import top.leafage.hypervisor.domain.vo.GroupVO;
import top.leafage.hypervisor.repository.GroupRepository;
import top.leafage.hypervisor.service.GroupService;
import org.jspecify.annotations.NonNull;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.leafage.common.data.domain.TreeNode;

import java.util.List;

import static top.leafage.common.data.converter.ModelToTreeNodeConverter.convertToTree;


/**
 * group service impl.
 *
 * @author wq li
 */
@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private static final BeanCopier copier = BeanCopier.create(GroupDTO.class, Group.class, false);

    /**
     * Constructor for GroupServiceImpl.
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
    public Page<@NonNull GroupVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<@NonNull Group> spec = (root, query, cb) ->
                buildPredicate(filters, cb, root).orElse(null);

        return groupRepository.findAll(spec, pageable)
                .map(GroupVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TreeNode<@NonNull Long>> tree() {
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
                .map(GroupVO::from)
                .orElse(null);
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
        Group entity = groupRepository.saveAndFlush(GroupDTO.toEntity(dto));
        return GroupVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupVO modify(Long id, GroupDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        Group entity = groupRepository.findById(id).map(existing -> {
                    copier.copy(dto, existing, null);
                    return groupRepository.save(existing);
                })
                .orElseThrow();
        return GroupVO.from(entity);
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
