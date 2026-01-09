/*
 * Copyright (c) 2026.  little3201.
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
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.system.domain.User;
import top.leafage.hypervisor.system.domain.dto.UserDTO;
import top.leafage.hypervisor.system.domain.vo.UserVO;
import top.leafage.hypervisor.system.repository.UserRepository;
import top.leafage.hypervisor.system.service.UserService;

import java.util.NoSuchElementException;


/**
 * user service impl
 *
 * @author wq li 2018-07-28 0:30
 */
@Service
public class UserServiceImpl implements UserService {

    private static final BeanCopier copier = BeanCopier.create(UserDTO.class, User.class, false);
    private final UserRepository userRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    /**
     * Constructor for UserServiceImpl.
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
                .map(UserVO::from)
                .collectList()
                .zipWith(r2dbcEntityTemplate.count(Query.query(criteria), User.class))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserVO> fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .map(UserVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Mono<UserVO> create(UserDTO dto) {
        return userRepository.existsByUsername(dto.getUsername())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("username already exists: " + dto.getUsername()));
                    }
                    return userRepository.save(UserDTO.toEntity(dto, "{noop}12345678"))
                            .map(UserVO::from);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Mono<UserVO> modify(Long id, UserDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .flatMap(existing -> {
                    if (existing.getUsername().equals(dto.getUsername())) {
                        return Mono.just(existing);
                    }

                    return userRepository.existsByUsername(dto.getUsername())
                            .flatMap(exists -> {
                                if (exists) {
                                    return Mono.error(new IllegalArgumentException("username already exists: " + dto.getUsername()));
                                }
                                return Mono.just(existing);
                            });
                })
                .flatMap(existing -> {
                    copier.copy(dto, existing, null);
                    return userRepository.save(existing);
                })
                .map(UserVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Mono<Void> remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return userRepository.existsById(id).flatMap(exists -> {
            if (exists) {
                return userRepository.deleteById(id);
            }
            return Mono.error(new NoSuchElementException("user not found: " + id));
        });
    }

    @Transactional
    @Override
    public Mono<Boolean> enable(Long id) {
        return userRepository.updateEnabledById(id).map(count -> count > 0);
    }

    @Transactional
    @Override
    public Mono<Boolean> unlock(Long id) {
        return userRepository.updateAccountNonLockedById(id).map(count -> count > 0);
    }

}
