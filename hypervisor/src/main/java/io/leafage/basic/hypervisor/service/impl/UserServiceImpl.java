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
package io.leafage.basic.hypervisor.service.impl;

import io.leafage.basic.hypervisor.domain.User;
import io.leafage.basic.hypervisor.dto.UserDTO;
import io.leafage.basic.hypervisor.repository.UserRepository;
import io.leafage.basic.hypervisor.service.UserService;
import io.leafage.basic.hypervisor.vo.UserVO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * user service impl.
 *
 * @author wq li
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * <p>Constructor for UserServiceImpl.</p>
     *
     * @param userRepository a {@link io.leafage.basic.hypervisor.repository.UserRepository} object
     */
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<UserVO> retrieve(int page, int size, String sortBy, boolean descending, String username) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(username)) {
                predicates.add(cb.like(root.get("username"), "%" + username + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return userRepository.findAll(spec, pageable)
                .map(user -> convertToVO(user, UserVO.class));
    }

    @Override
    public UserVO findByUsername(String username) {
        Assert.hasText(username, "username must not be empty.");

        return userRepository.findByUsername(username)
                .map(user -> convertToVO(user, UserVO.class)).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVO fetch(Long id) {
        Assert.notNull(id, "id must not be null.");

        return userRepository.findById(id)
                .map(user -> convertToVO(user, UserVO.class)).orElse(null);
    }

    @Override
    public boolean enable(Long id) {
        return userRepository.updateEnabledById(id) > 0;
    }

    @Override
    public boolean unlock(Long id) {
        return userRepository.updateAccountNonLockedById(id) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String username, Long id) {
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
    public UserVO create(UserDTO dto) {
        User user = convertToDomain(dto, User.class);
        user.setPassword("{noop}123456");

        userRepository.saveAndFlush(user);
        return convertToVO(user, UserVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVO modify(Long id, UserDTO dto) {
        Assert.notNull(id, "id must not be null.");

        return userRepository.findById(id).map(existing -> {
            User user = convert(dto, existing);
            user = userRepository.save(user);
            return convertToVO(user, UserVO.class);
        }).orElseThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Long id) {
        Assert.notNull(id, "id must not be null.");
        userRepository.deleteById(id);
    }

}
