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
package io.leafage.system.service.impl;

import io.leafage.system.domain.User;
import io.leafage.system.dto.UserDTO;
import io.leafage.system.repository.UserRepository;
import io.leafage.system.service.UserService;
import io.leafage.system.vo.UserVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import top.leafage.common.DomainConverter;

/**
 * user common impl.
 *
 * @author wq li
 */
@Service
public class UserServiceImpl extends DomainConverter implements UserService {

    private final UserRepository userRepository;

    /**
     * <p>Constructor for UserServiceImpl.</p>
     *
     * @param userRepository a {@link UserRepository} object
     */
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<UserVO> retrieve(int page, int size, String sortBy, boolean descending, String name) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        if (StringUtils.hasText(name)) {
            return userRepository.findAllByUsernameContaining(name, pageable)
                    .map(user -> convertToVO(user, UserVO.class));
        }
        return userRepository.findAll(pageable).map(user -> convertToVO(user, UserVO.class));
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
        return userRepository.updateEnabledById(id);
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

        userRepository.save(user);
        return convertToVO(user, UserVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVO modify(Long id, UserDTO dto) {
        Assert.notNull(id, "id must not be null.");

        return userRepository.findById(id).map(existing -> {
            existing = convert(dto, existing);
            existing = userRepository.save(existing);
            return convertToVO(existing, UserVO.class);
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
