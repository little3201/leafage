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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.data.domain.TreeNode;
import top.leafage.hypervisor.domain.GroupMembers;
import top.leafage.hypervisor.domain.GroupPrivileges;
import top.leafage.hypervisor.domain.Privilege;
import top.leafage.hypervisor.domain.dto.PrivilegeDTO;
import top.leafage.hypervisor.domain.vo.PrivilegeVO;
import top.leafage.hypervisor.repository.GroupMembersRepository;
import top.leafage.hypervisor.repository.GroupPrivilegesRepository;
import top.leafage.hypervisor.repository.PrivilegeRepository;
import top.leafage.hypervisor.service.PrivilegeService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static top.leafage.common.data.reactive.ReactiveModelToTreeNodeConverter.toTree;

/**
 * privilege service impl
 *
 * @author wq li
 */
@Service
public class PrivilegeServiceImpl implements PrivilegeService {

    private static final BeanCopier copier = BeanCopier.create(PrivilegeDTO.class, Privilege.class, false);
    private final PrivilegeRepository privilegeRepository;
    private final GroupPrivilegesRepository groupPrivilegesRepository;
    private final GroupMembersRepository groupMembersRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    /**
     * Constructor for PrivilegeServiceImpl.
     *
     * @param privilegeRepository a {@link PrivilegeRepository} object
     */
    public PrivilegeServiceImpl(PrivilegeRepository privilegeRepository, GroupPrivilegesRepository groupPrivilegesRepository, GroupMembersRepository groupMembersRepository, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.privilegeRepository = privilegeRepository;
        this.groupPrivilegesRepository = groupPrivilegesRepository;
        this.groupMembersRepository = groupMembersRepository;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Page<PrivilegeVO>> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        Criteria criteria = buildCriteria(filters, Privilege.class);
        criteria = criteria.and("superiorId").isNull();

        return r2dbcEntityTemplate.select(Privilege.class)
                .matching(Query.query(criteria).with(pageable))
                .all()
                .flatMap(privilege -> privilegeRepository.countBySuperiorId(privilege.getId())
                        .map(count -> PrivilegeVO.from(privilege, count))
                )
                .collectList()
                .zipWith(r2dbcEntityTemplate.count(Query.query(criteria), Privilege.class))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<List<TreeNode<Long>>> tree(String username) {
        Assert.hasText(username, String.format(_MUST_NOT_BE_EMPTY, "username"));

        Set<String> meta = Set.of("path", "redirect", "component", "icon", "actions");
        return groupMembersRepository.findByUsername(username)
                .map(GroupMembers::getGroupId)
                .flatMap(groupPrivilegesRepository::findByGroupId)
                .map(GroupPrivileges::getPrivilegeId)
                .concatMap(this::expandPrivileges)
                .collectMap(Privilege::getId)
                .flatMap(privMap -> toTree(Flux.fromIterable(privMap.values()), meta));
    }

    @Override
    public Flux<PrivilegeVO> subset(Long id) {
        return privilegeRepository.findAllBySuperiorId(id)
                .map(PrivilegeVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<PrivilegeVO> retrieve(List<Long> ids) {
        Flux<Privilege> flux;
        if (CollectionUtils.isEmpty(ids)) {
            flux = privilegeRepository.findAll();
        } else {
            flux = privilegeRepository.findAllById(ids);
        }
        return flux.map(PrivilegeVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<PrivilegeVO> fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return privilegeRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .map(PrivilegeVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Mono<PrivilegeVO> modify(Long id, PrivilegeDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return privilegeRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .flatMap(existing -> {
                    if (!existing.getName().equals(dto.getName())) {
                        return Mono.just(existing);
                    }

                    return privilegeRepository.existsByName(dto.getName())
                            .flatMap(exists -> {
                                if (exists) {
                                    return Mono.error(new IllegalArgumentException("privilege name already exists: " + dto.getName()));
                                }
                                return Mono.just(existing);
                            });
                })
                .flatMap(existing -> {
                    copier.copy(dto, existing, null);
                    return privilegeRepository.save(existing);
                })
                .map(PrivilegeVO::from);
    }

    private Flux<Privilege> expandPrivileges(Long privilegeId) {
        return privilegeRepository.findById(privilegeId)
                .filter(Privilege::isEnabled)
                .flatMapMany(privilege -> {
                    if (privilege.getSuperiorId() == null) {
                        return Flux.just(privilege);
                    }
                    return expandPrivileges(privilege.getSuperiorId())
                            .concatWith(Mono.just(privilege));   // 先上级，再自己
                })
                .switchIfEmpty(Flux.empty());
    }

}
