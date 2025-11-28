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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.leafage.hypervisor.domain.AccessLog;
import top.leafage.hypervisor.domain.vo.AccessLogVO;
import top.leafage.hypervisor.repository.AccessLogRepository;
import top.leafage.hypervisor.service.AccessLogService;

/**
 * access log service impl.
 *
 * @author wq li
 */
@Service
public class AccessLogServiceImpl implements AccessLogService {

    private final AccessLogRepository accessLogRepository;

    /**
     * Constructor for AccessLogServiceImpl.
     *
     * @param accessLogRepository a {@link AccessLogRepository} object
     */
    public AccessLogServiceImpl(AccessLogRepository accessLogRepository) {
        this.accessLogRepository = accessLogRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<@NonNull AccessLogVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<@NonNull AccessLog> spec = (root, query, cb) ->
                buildPredicate(filters, cb, root).orElse(null);

        return accessLogRepository.findAll(spec, pageable).map(AccessLogVO::from);
    }

    @Override
    public AccessLogVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return accessLogRepository.findById(id)
                .map(AccessLogVO::from)
                .orElse(null);
    }

    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        accessLogRepository.deleteById(id);
    }

    @Override
    public void clear() {
        accessLogRepository.deleteAll();
    }
}
