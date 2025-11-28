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
package top.leafage.hypervisor.service.impl;

import org.jspecify.annotations.NonNull;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.leafage.hypervisor.domain.User;
import top.leafage.hypervisor.domain.dto.UserDTO;
import top.leafage.hypervisor.domain.vo.UserVO;
import top.leafage.hypervisor.repository.UserRepository;
import top.leafage.hypervisor.service.UserService;

/**
 * user service impl.
 *
 * @author wq li
 */
@Service
public class UserServiceImpl implements UserService {

    private static final BeanCopier copier = BeanCopier.create(UserDTO.class, User.class, false);
    private final UserRepository userRepository;

    /**
     * Constructor for UserServiceImpl.
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
    public Page<@NonNull UserVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<@NonNull User> spec = (root, query, cb) ->
                buildPredicate(filters, cb, root).orElse(null);

        return userRepository.findAll(spec, pageable)
                .map(UserVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return userRepository.findById(id)
                .map(UserVO::from)
                .orElse(null);
    }

    @Override
    public boolean enable(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return userRepository.updateEnabledById(id) > 0;
    }

    @Override
    public boolean unlock(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return userRepository.updateAccountNonLockedById(id) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String username, Long id) {
        Assert.hasText(username, String.format(_MUST_NOT_BE_EMPTY, "username"));

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
        User entity = userRepository.saveAndFlush(UserDTO.toEntity(dto, "{noop}123456"));
        return UserVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVO modify(Long id, UserDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        User entity = userRepository.findById(id).map(existing -> {
                    copier.copy(dto, existing, null);
                    return userRepository.save(existing);
                })
                .orElseThrow();
        return UserVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        userRepository.deleteById(id);
    }

}
