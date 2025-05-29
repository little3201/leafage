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

import io.leafage.hypervisor.domain.Privilege;
import io.leafage.hypervisor.dto.PrivilegeDTO;
import io.leafage.hypervisor.repository.GroupMembersRepository;
import io.leafage.hypervisor.repository.GroupPrivilegesRepository;
import io.leafage.hypervisor.repository.PrivilegeRepository;
import io.leafage.hypervisor.service.PrivilegeService;
import io.leafage.hypervisor.vo.PrivilegeVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.TreeNode;
import top.leafage.common.reactive.ReactiveAbstractTreeNodeService;
import top.leafage.common.reactive.audit.ReactiveAuditMetadata;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * privilege service impl
 *
 * @author wq li
 */
@Service
public class PrivilegeServiceImpl extends ReactiveAbstractTreeNodeService<Privilege, Long> implements PrivilegeService {

    private final PrivilegeRepository privilegeRepository;
    private final GroupPrivilegesRepository groupPrivilegesRepository;
    private final GroupMembersRepository groupMembersRepository;

    /**
     * <p>Constructor for PrivilegeServiceImpl.</p>
     *
     * @param privilegeRepository a {@link PrivilegeRepository} object
     */
    public PrivilegeServiceImpl(PrivilegeRepository privilegeRepository, GroupPrivilegesRepository groupPrivilegesRepository, GroupMembersRepository groupMembersRepository) {
        this.privilegeRepository = privilegeRepository;
        this.groupPrivilegesRepository = groupPrivilegesRepository;
        this.groupMembersRepository = groupMembersRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Page<PrivilegeVO>> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        return privilegeRepository.findAllBySuperiorIdIsNull(pageable)
                .flatMap(p -> privilegeRepository.countBySuperiorId(p.getId())
                        .map(count -> {
                            PrivilegeVO vo = convertToVO(p, PrivilegeVO.class);
                            vo.setCount(count);
                            return vo;
                        }))
                .collectList()
                .map(voList -> new PageImpl<>(voList, pageable, voList.size()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<List<TreeNode<Long>>> tree(String username) {
        Assert.hasText(username, "username must not be empty.");

        Flux<Privilege> privilegeFlux = groupMembersRepository.findByUsername(username)
                .flatMap(groupMember -> groupPrivilegesRepository.findByGroupId(groupMember.getGroupId())
                        .flatMap(groupPrivilege -> addSuperior(groupPrivilege.getPrivilegeId(), new HashSet<>())))
                .distinct(Privilege::getId); // 统一去重

        return this.buildTree(privilegeFlux);
    }

    @Override
    public Flux<PrivilegeVO> subset(Long id) {
        return privilegeRepository.findAllBySuperiorId(id)
                .map(p -> convertToVO(p, PrivilegeVO.class));
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
        return flux.map(p -> convertToVO(p, PrivilegeVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<PrivilegeVO> fetch(Long id) {
        Assert.notNull(id, "id must not be null.");
        return privilegeRepository.findById(id)
                .map(p -> convertToVO(p, PrivilegeVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Boolean> exists(String name, Long id) {
        Assert.hasText(name, "name must not be empty.");
        return privilegeRepository.existsByName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<PrivilegeVO> create(PrivilegeDTO dto) {
        return privilegeRepository.save(convertToDomain(dto, Privilege.class))
                .map(p -> convertToVO(p, PrivilegeVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<PrivilegeVO> modify(Long id, PrivilegeDTO dto) {
        Assert.notNull(id, "id must not be null.");

        return privilegeRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .map(privilege -> convert(dto, privilege))
                .flatMap(privilegeRepository::save)
                .map(p -> convertToVO(p, PrivilegeVO.class));
    }

    private Flux<Privilege> addSuperior(Long privilegeId, Set<Long> visited) {
        if (!visited.add(privilegeId)) {
            return Flux.empty(); // 已访问，防止死循环
        }

        return privilegeRepository.findById(privilegeId)
                .filter(ReactiveAuditMetadata::isEnabled)
                .flatMapMany(privilege -> {
                    if (privilege.getSuperiorId() == null) {
                        return Flux.just(privilege);
                    }
                    return addSuperior(privilege.getSuperiorId(), visited)
                            .concatWithValues(privilege); // 先上级，再自己
                });
    }

    /**
     * convert to TreeNode
     *
     * @param privileges privilege集合
     * @return TreeNode of Flux
     */
    private Mono<List<TreeNode<Long>>> buildTree(Flux<Privilege> privileges) {
        Set<String> meta = new HashSet<>();
        meta.add("path");
        meta.add("redirect");
        meta.add("component");
        meta.add("icon");
        meta.add("actions");
        return convertToTree(privileges, meta);
    }

}
