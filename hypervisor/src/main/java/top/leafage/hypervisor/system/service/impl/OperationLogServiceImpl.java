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

package top.leafage.hypervisor.system.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import top.leafage.hypervisor.system.domain.OperationLog;
import top.leafage.hypervisor.system.domain.vo.OperationLogVO;
import top.leafage.hypervisor.system.repository.OperationLogRepository;
import top.leafage.hypervisor.system.service.OperationLogService;

/**
 * operation log service impl.
 *
 * @author wq li
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogRepository operationLogRepository;

    /**
     * Constructor for AccessLogServiceImpl.
     *
     * @param operationLogRepository a {@link OperationLogRepository} object
     */
    public OperationLogServiceImpl(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<@NonNull OperationLogVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<@NonNull OperationLog> spec = (root, query, cb) ->
                buildPredicate(filters, cb, root).orElse(null);

        return operationLogRepository.findAll(spec, pageable)
                .map(OperationLogVO::from);
    }

    @Override
    public OperationLogVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return operationLogRepository.findById(id)
                .map(OperationLogVO::from)
                .orElseThrow(() -> new EntityNotFoundException("operation log not found: " + id));
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        if (!operationLogRepository.existsById(id)) {
            throw new EntityNotFoundException("operation log not found: " + id);
        }
        operationLogRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void clear() {
        operationLogRepository.deleteAll();
    }
}
