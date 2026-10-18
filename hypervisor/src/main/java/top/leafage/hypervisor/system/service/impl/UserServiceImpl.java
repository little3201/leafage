/*
 *
 *  * Copyright(c) 2019-present the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *       https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package top.leafage.hypervisor.system.service.impl;

import org.jspecify.annotations.NonNull;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import top.leafage.hypervisor.system.domain.User;
import top.leafage.hypervisor.system.domain.dto.UserDTO;
import top.leafage.hypervisor.system.domain.vo.UserVO;
import top.leafage.hypervisor.system.repository.UserRepository;
import top.leafage.hypervisor.system.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.data.relational.core.query.Query.query;
import static top.leafage.hypervisor.constants.GlobalConstant.ID_MUST_NOT_BE_NULL;

/**
 * user service impl.
 *
 * @author wq li
 */
@Service
public class UserServiceImpl implements UserService {

    private static final BeanCopier copier = BeanCopier.create(UserDTO.class, User.class, false);
    private final UserRepository userRepository;
    private final JdbcAggregateTemplate jdbcAggregateTemplate;

    /**
     * Constructor for UserServiceImpl.
     *
     * @param userRepository a {@link UserRepository} object
     */
    public UserServiceImpl(UserRepository userRepository, JdbcAggregateTemplate jdbcAggregateTemplate) {
        this.userRepository = userRepository;
        this.jdbcAggregateTemplate = jdbcAggregateTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<@NonNull UserVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        Criteria criteria = buildCriteria(filters, User.class);

        List<UserVO> voList = jdbcAggregateTemplate.findAll(query(criteria).with(pageable), User.class)
                .stream().map(UserVO::from)
                .toList();
        return new PageImpl<>(voList, pageable, jdbcAggregateTemplate.count(query(criteria), User.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return userRepository.findById(id)
                .map(entity -> UserVO.from(entity, false))
                .orElseThrow(() -> new NoSuchElementException("user not found: " + id));
    }

    @Transactional
    @Override
    public boolean enable(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("user not found: " + id);
        }
        return userRepository.updateEnabledById(id) > 0;
    }

    @Transactional
    @Override
    public boolean unlock(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("user not found: " + id);
        }
        return userRepository.updateAccountNonLockedById(id) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public UserVO create(UserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("username already exists: " + dto.getUsername());
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("username already exists: " + dto.getUsername());
        }
        User entity = userRepository.save(UserDTO.toEntity(dto, "{noop}123456"));
        return UserVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public UserVO modify(Long id, UserDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("user not found: " + id));

        if (!existing.getUsername().equals(dto.getUsername()) &&
                userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("username already exists: " + dto.getUsername());
        }
        if (!existing.getEmail().equals(dto.getEmail()) &&
                userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("email already exists: " + dto.getEmail());
        }

        copier.copy(dto, existing, null);
        User entity = userRepository.save(existing);
        return UserVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("user not found: " + id);
        }
        userRepository.deleteById(id);
    }

}
