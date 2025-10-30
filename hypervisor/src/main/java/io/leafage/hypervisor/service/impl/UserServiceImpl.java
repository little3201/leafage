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

import io.leafage.hypervisor.domain.User;
import io.leafage.hypervisor.dto.UserDTO;
import io.leafage.hypervisor.repository.UserRepository;
import io.leafage.hypervisor.service.UserService;
import io.leafage.hypervisor.vo.UserVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import top.leafage.common.DomainConverter;

import java.util.NoSuchElementException;


/**
 * user service impl
 *
 * @author wq li 2018-07-28 0:30
 */
@Service
public class UserServiceImpl extends DomainConverter implements UserService {

    private final UserRepository userRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    /**
     * <p>Constructor for UserServiceImpl.</p>
     *
     * @param userRepository a {@link UserRepository} object
     */
    public UserServiceImpl(UserRepository userRepository, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.userRepository = userRepository;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Page<UserVO>> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        Criteria criteria = buildCriteria(filters, User.class);

        return r2dbcEntityTemplate.select(User.class)
                .matching(Query.query(criteria).with(pageable))
                .all()
                .map(user -> convertToVO(user, UserVO.class))
                .collectList()
                .zipWith(r2dbcEntityTemplate.count(Query.query(criteria), User.class))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public Mono<UserVO> findByUsername(String username) {
        Assert.hasText(username, "username must not be empty.");

        return userRepository.findByUsername(username)
                .map(user -> convertToVO(user, UserVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserVO> fetch(Long id) {
        Assert.notNull(id, "id must not be null.");

        return userRepository.findById(id)
                .map(u -> convertToVO(u, UserVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Boolean> exists(String username, Long id) {
        Assert.hasText(username, "username must not be empty.");
        if (id == null) {
            return userRepository.existsByUsername(username);
        }
        return userRepository.existsByUsernameAndIdNot(username, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserVO> create(UserDTO dto) {
        return userRepository.save(convertToDomain(dto, User.class))
                .map(u -> convertToVO(u, UserVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserVO> modify(Long id, UserDTO dto) {
        Assert.notNull(id, "id must not be null.");

        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .map(user -> convert(dto, user))
                .flatMap(userRepository::save)
                .map(u -> convertToVO(u, UserVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> remove(Long id) {
        Assert.notNull(id, "id must not be null.");

        return userRepository.deleteById(id);
    }

    @Override
    public Mono<Boolean> enable(Long id) {
        return userRepository.updateEnabledById(id).map(count -> count > 0);
    }

    @Override
    public Mono<Boolean> unlock(Long id) {
        return userRepository.updateAccountNonLockedById(id).map(count -> count > 0);
    }

}
