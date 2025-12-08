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

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.domain.GroupAuthorities;
import top.leafage.hypervisor.domain.GroupPrivileges;
import top.leafage.hypervisor.repository.GroupAuthoritiesRepository;
import top.leafage.hypervisor.repository.GroupPrivilegesRepository;
import top.leafage.hypervisor.repository.PrivilegeRepository;
import top.leafage.hypervisor.service.GroupPrivilegesService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * group privileges service impl
 *
 * @author wq li
 */
@Service
public class GroupPrivilegesServiceImpl implements GroupPrivilegesService {

    private final GroupPrivilegesRepository groupPrivilegesRepository;
    private final PrivilegeRepository privilegeRepository;
    private final GroupAuthoritiesRepository groupAuthoritiesRepository;

    /**
     * Constructor for GroupPrivilegesServiceImpl.
     *
     * @param groupPrivilegesRepository a {@link GroupPrivilegesRepository} object
     */
    public GroupPrivilegesServiceImpl(GroupPrivilegesRepository groupPrivilegesRepository, PrivilegeRepository privilegeRepository, GroupAuthoritiesRepository groupAuthoritiesRepository) {
        this.groupPrivilegesRepository = groupPrivilegesRepository;
        this.privilegeRepository = privilegeRepository;
        this.groupAuthoritiesRepository = groupAuthoritiesRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<List<GroupPrivileges>> privileges(Long groupId) {
        Assert.notNull(groupId, "groupId must not be null.");

        return groupPrivilegesRepository.findByGroupId(groupId).collectList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<List<GroupPrivileges>> groups(Long privilegeId) {
        Assert.notNull(privilegeId, "privilegeId must not be empty.");

        return groupPrivilegesRepository.findByPrivilegeId(privilegeId).collectList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<GroupPrivileges> relation(Long groupId, Long privilegeId, Set<String> actions) {
        Assert.notNull(groupId, "groupId must not be null.");
        Assert.notNull(privilegeId, "privilegeId must not be null.");

        return privilegeRepository.findById(privilegeId)
                .flatMap(privilege -> {
                    // 创建 GroupPrivileges 对象
                    GroupPrivileges groupPrivileges = new GroupPrivileges();
                    groupPrivileges.setGroupId(groupId);
                    groupPrivileges.setPrivilegeId(privilegeId);
                    groupPrivileges.setActions(actions);

                    // 生成 GroupAuthorities 列表，添加 "read" 权限
                    List<GroupAuthorities> groupAuthoritiesList = Stream.concat(Stream.of("read"), actions.stream())
                            .map(action -> {
                                GroupAuthorities authority = new GroupAuthorities();
                                authority.setGroupId(groupId);
                                authority.setAuthority(privilege.getName() + ":" + action);
                                return authority;
                            }).collect(Collectors.toList());

                    return groupAuthoritiesRepository.saveAll(groupAuthoritiesList)
                            .then(groupPrivilegesRepository.save(groupPrivileges));
                });
    }

    @Override
    public Mono<Void> removeRelation(Long groupId, Long privilegeId, Set<String> actions) {
        Assert.notNull(groupId, "groupId must not be null.");
        Assert.notNull(privilegeId, "privilegeId must not be null.");

        return groupPrivilegesRepository.findByGroupIdAndPrivilegeId(groupId, privilegeId)
                .flatMap(groupPrivileges -> {
                    // 删除 GroupAuthorities
                    if (CollectionUtils.isEmpty(actions) || groupPrivileges.getActions().containsAll(actions)) {
                        return groupPrivilegesRepository.deleteById(groupPrivileges.getId());
                    }
                    return privilegeRepository.findById(privilegeId).map(privilege -> actions.stream().map(action ->
                                    groupAuthoritiesRepository.deleteByGroupIdAndAuthority(groupId, privilege.getName() + ":" + action)))
                            .then(groupPrivilegesRepository.delete(groupPrivileges));

                });
    }
}
