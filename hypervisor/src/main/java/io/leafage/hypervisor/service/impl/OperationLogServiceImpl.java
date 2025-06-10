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

package io.leafage.hypervisor.service.impl;

import io.leafage.hypervisor.domain.OperationLog;
import io.leafage.hypervisor.dto.OperationLogDTO;
import io.leafage.hypervisor.repository.OperationLogRepository;
import io.leafage.hypervisor.service.OperationLogService;
import io.leafage.hypervisor.vo.OperationLogVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.leafage.common.DomainConverter;

/**
 * operation log service impl.
 *
 * @author wq li
 */
@Service
public class OperationLogServiceImpl extends DomainConverter implements OperationLogService {

    private final OperationLogRepository operationLogRepository;

    /**
     * <p>Constructor for AccessLogServiceImpl.</p>
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
    public Page<OperationLogVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        return operationLogRepository.findAll(pageable)
                .map(operationLog -> convertToVO(operationLog, OperationLogVO.class));
    }

    @Override
    public OperationLogVO fetch(Long id) {
        Assert.notNull(id, "id must not be null.");

        return operationLogRepository.findById(id)
                .map(operationLog -> convertToVO(operationLog, OperationLogVO.class))
                .orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OperationLogVO create(OperationLogDTO dto) {
        OperationLog operationLog = convertToDomain(dto, OperationLog.class);

        operationLogRepository.saveAndFlush(operationLog);
        return convertToVO(operationLog, OperationLogVO.class);
    }

    @Override
    public void remove(Long id) {
        Assert.notNull(id, "id must not be null.");
        operationLogRepository.deleteById(id);
    }

    @Override
    public void clear() {
        operationLogRepository.deleteAll();
    }
}
