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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import top.leafage.hypervisor.domain.AuditLog;
import top.leafage.hypervisor.domain.vo.AuditLogVO;
import top.leafage.hypervisor.repository.AuditLogRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * audit log service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class AuditLogServiceImplTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    @Test
    void retrieve() {
        Page<AuditLog> page = new PageImpl<>(List.of(mock(AuditLog.class)));

        given(this.auditLogRepository.findAll(ArgumentMatchers.<Specification<AuditLog>>any(),
                any(Pageable.class))).willReturn(page);

        Page<AuditLogVO> voPage = auditLogService.retrieve(0, 2, "id", true, "test");

        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        given(this.auditLogRepository.findById(anyLong())).willReturn(Optional.of(mock(AuditLog.class)));

        AuditLogVO vo = auditLogService.fetch(anyLong());

        Assertions.assertNotNull(vo);
    }

    @Test
    void remove() {
        auditLogService.remove(1L);

        verify(this.auditLogRepository, times(1)).deleteById(anyLong());
    }

}