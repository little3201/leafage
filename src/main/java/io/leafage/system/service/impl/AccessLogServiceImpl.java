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

import io.leafage.system.domain.AccessLog;
import io.leafage.system.dto.AccessLogDTO;
import io.leafage.system.repository.AccessLogRepository;
import io.leafage.system.service.AccessLogService;
import io.leafage.system.vo.AccessLogVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.leafage.common.DomainConverter;

/**
 * access log common impl.
 *
 * @author wq li
 */
@Service
public class AccessLogServiceImpl extends DomainConverter implements AccessLogService {

    private final AccessLogRepository accessLogRepository;

    /**
     * <p>Constructor for AccessLogServiceImpl.</p>
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
    public Page<AccessLogVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        return accessLogRepository.findAll(pageable).map(accessLog ->
                convertToVO(accessLog, AccessLogVO.class));
    }

    @Override
    public AccessLogVO fetch(Long id) {
        Assert.notNull(id, "id must not be null.");

        return accessLogRepository.findById(id).map(accessLog ->
                convertToVO(accessLog, AccessLogVO.class)).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessLogVO create(AccessLogDTO dto) {
        AccessLog accessLog = convertToDomain(dto, AccessLog.class);

        accessLogRepository.save(accessLog);
        return convertToVO(accessLog, AccessLogVO.class);
    }

    @Override
    public void remove(Long id) {
        Assert.notNull(id, "id must not be null.");
        accessLogRepository.deleteById(id);
    }

}
