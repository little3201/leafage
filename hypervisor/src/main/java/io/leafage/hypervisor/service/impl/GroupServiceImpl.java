/*
 *  Copyright 2018-2025 little3201.
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
package io.leafage.hypervisor.service.impl;

import io.leafage.hypervisor.domain.Group;
import io.leafage.hypervisor.dto.GroupDTO;
import io.leafage.hypervisor.repository.GroupRepository;
import io.leafage.hypervisor.service.GroupService;
import io.leafage.hypervisor.vo.GroupVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.DomainConverter;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * group service impl
 *
 * @author wq li 2018/12/17 19:25
 */
@Service
public class GroupServiceImpl extends DomainConverter implements GroupService {

    private final GroupRepository groupRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    /**
     * <p>Constructor for GroupServiceImpl.</p>
     *
     * @param groupRepository a {@link GroupRepository} object
     */
    public GroupServiceImpl(GroupRepository groupRepository, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.groupRepository = groupRepository;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Page<GroupVO>> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        Criteria criteria = buildCriteria(filters, Group.class);

        return r2dbcEntityTemplate.select(Group.class)
                .matching(Query.query(criteria).with(pageable))
                .all()
                .map(group -> convertToVO(group, GroupVO.class))
                .collectList()
                .zipWith(r2dbcEntityTemplate.count(Query.query(criteria), Group.class))
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
