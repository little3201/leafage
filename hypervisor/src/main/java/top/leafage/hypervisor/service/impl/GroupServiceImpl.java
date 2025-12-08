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
package top.leafage.hypervisor.service.impl;

import org.springframework.cglib.beans.BeanCopier;
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
import top.leafage.hypervisor.domain.Group;
import top.leafage.hypervisor.domain.dto.GroupDTO;
import top.leafage.hypervisor.domain.vo.GroupVO;
import top.leafage.hypervisor.repository.GroupMembersRepository;
import top.leafage.hypervisor.repository.GroupRepository;
import top.leafage.hypervisor.service.GroupService;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * group service impl
 *
 * @author wq li 2018/12/17 19:25
 */
@Service
public class GroupServiceImpl implements GroupService {

    private static final BeanCopier copier = BeanCopier.create(GroupDTO.class, Group.class, false);
    private final GroupRepository groupRepository;
    private final GroupMembersRepository groupMembersRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    /**
     * Constructor for GroupServiceImpl.
     *
     * @param groupRepository a {@link GroupRepository} object
     */
    public GroupServiceImpl(GroupRepository groupRepository, GroupMembersRepository groupMembersRepository, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.groupRepository = groupRepository;
        this.groupMembersRepository = groupMembersRepository;
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
                .map(GroupVO::from)
                .collectList()
                .zipWith(r2dbcEntityTemplate.count(Query.query(criteria), Group.class))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Flux<GroupVO> retrieve(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return groupRepository.findAll().map(GroupVO::from);
        }
        return groupRepository.findAllById(ids).map(GroupVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<GroupVO> fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return groupRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .map(GroupVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<GroupVO> create(GroupDTO dto) {
        return groupRepository.existsByName(dto.getName())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("name already exists: " + dto.getName()));
                    }
                    return groupRepository.save(GroupDTO.toEntity(dto))
                            .map(GroupVO::from);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<GroupVO> modify(Long id, GroupDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return groupRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .flatMap(existing -> {
                    if (!existing.getName().equals(dto.getName())) {
                        return Mono.just(existing);
                    }

                    return groupRepository.existsByName(dto.getName())
                            .flatMap(exists -> {
                                if (exists) {
                                    return Mono.error(new IllegalArgumentException("group name already exists: " + dto.getName()));
                                }
                                return Mono.just(existing);
                            });
                })
                .flatMap(existing -> {
                    copier.copy(dto, existing, null);
                    return groupRepository.save(existing);
                })
                .map(GroupVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return groupRepository.deleteById(id);
    }

}
