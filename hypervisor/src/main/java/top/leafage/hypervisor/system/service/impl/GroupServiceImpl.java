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
package top.leafage.hypervisor.system.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import top.leafage.common.data.domain.TreeNode;
import top.leafage.hypervisor.system.domain.Group;
import top.leafage.hypervisor.system.domain.dto.GroupDTO;
import top.leafage.hypervisor.system.domain.vo.GroupVO;
import top.leafage.hypervisor.system.repository.GroupRepository;
import top.leafage.hypervisor.system.service.GroupService;

import java.util.Collections;
import java.util.List;

import static top.leafage.common.data.converter.ModelToTreeNodeConverter.toTree;


/**
 * group service impl.
 *
 * @author wq li
 */
@Service
public class GroupServiceImpl implements GroupService {

    private static final BeanCopier copier = BeanCopier.create(GroupDTO.class, Group.class, false);
    private final GroupRepository groupRepository;

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
        if (CollectionUtils.isEmpty(groups)) {
            return Collections.emptyList();
        }
        return toTree(groups);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return groupRepository.findById(id)
                .map(GroupVO::from)
                .orElseThrow(() -> new EntityNotFoundException("group not found: " + id));
    }

    @Override
    public boolean enable(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        if (!groupRepository.existsById(id)) {
            throw new EntityNotFoundException("group not found: " + id);
        }
        return groupRepository.updateEnabledById(id) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupVO create(GroupDTO dto) {
        if (groupRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("name already exists: " + dto.getName());
        }
        Group entity = groupRepository.saveAndFlush(GroupDTO.toEntity(dto));
        return GroupVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupVO modify(Long id, GroupDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        Group existing = groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("group not found: " + id));
        if (!existing.getName().equals(dto.getName()) &&
                groupRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("name already exists: " + dto.getName());
        }

        copier.copy(dto, existing, null);
        Group entity = groupRepository.save(existing);
        return GroupVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        if (!groupRepository.existsById(id)) {
            throw new EntityNotFoundException("group not found: " + id);
        }
        groupRepository.deleteById(id);
    }

}
