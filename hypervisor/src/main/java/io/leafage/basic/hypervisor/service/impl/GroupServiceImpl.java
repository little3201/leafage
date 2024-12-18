/*
 *  Copyright 2018-2024 little3201.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package io.leafage.basic.hypervisor.service.impl;

import io.leafage.basic.hypervisor.domain.Group;
import io.leafage.basic.hypervisor.dto.GroupDTO;
import io.leafage.basic.hypervisor.repository.GroupRepository;
import io.leafage.basic.hypervisor.service.GroupService;
import io.leafage.basic.hypervisor.vo.GroupVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.TreeNode;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * group service impl
 *
 * @author wq li 2018/12/17 19:25
 */
@Service
public class GroupServiceImpl implements GroupService {

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
    public Mono<Page<GroupVO>> retrieve(int page, int size, String sortBy, boolean descending) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        return groupRepository.findAllBy(pageable)
                .map(g -> convertToVO(g, GroupVO.class))
                .collectList()
                .zipWith(groupRepository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Flux<GroupVO> retrieve(List<Long> ids) {
        Flux<Group> flux;
        if (CollectionUtils.isEmpty(ids)) {
            flux = groupRepository.findAll();
        } else {
            flux = groupRepository.findAllById(ids);
        }
        return flux.map(g -> convertToVO(g, GroupVO.class));
    }

    @Override
    public Mono<List<TreeNode>> tree() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<GroupVO> fetch(Long id) {
        Assert.notNull(id, "id must not be null.");

        return groupRepository.findById(id)
                .map(g -> convertToVO(g, GroupVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Boolean> exists(String name, Long id) {
        Assert.hasText(name, "name must not be empty.");

        return groupRepository.existsByName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<GroupVO> create(GroupDTO dto) {
        return groupRepository.save(convertToDomain(dto, Group.class))
                .map(g -> convertToVO(g, GroupVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<GroupVO> modify(Long id, GroupDTO dto) {
        Assert.notNull(id, "id must not be null.");

        return groupRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .map(group -> convert(dto, group))
                .flatMap(groupRepository::save)
                .map(g -> convertToVO(g, GroupVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> remove(Long id) {
        Assert.notNull(id, "id must not be null.");

        return groupRepository.deleteById(id);
    }

}
