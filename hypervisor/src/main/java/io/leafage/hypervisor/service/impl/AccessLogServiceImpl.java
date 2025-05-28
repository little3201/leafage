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

import io.leafage.hypervisor.domain.AccessLog;
import io.leafage.hypervisor.dto.AccessLogDTO;
import io.leafage.hypervisor.repository.AccessLogRepository;
import io.leafage.hypervisor.service.AccessLogService;
import io.leafage.hypervisor.vo.AccessLogVO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * access log service impl.
 *
 * @author wq li
 */
@Service
public class AccessLogServiceImpl implements AccessLogService {

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
    public Page<AccessLogVO> retrieve(int page, int size, String sortBy, boolean descending, String url) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<AccessLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(url)) {
                predicates.add(cb.like(root.get("url"), "%" + url + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return accessLogRepository.findAll(spec, pageable)
                .map(accessLog -> convertToVO(accessLog, AccessLogVO.class));
    }

    @Override
    public AccessLogVO fetch(Long id) {
        Assert.notNull(id, "id must not be null.");

        return accessLogRepository.findById(id)
                .map(accessLog -> convertToVO(accessLog, AccessLogVO.class)).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessLogVO create(AccessLogDTO dto) {
        AccessLog accessLog = new AccessLog();
        BeanCopier copier = BeanCopier.create(AccessLogDTO.class, AccessLog.class, false);
        copier.copy(dto, accessLog, null);

        accessLogRepository.saveAndFlush(accessLog);
        return convertToVO(accessLog, AccessLogVO.class);
    }

    @Override
    public void remove(Long id) {
        Assert.notNull(id, "id must not be null.");
        accessLogRepository.deleteById(id);
    }

}
