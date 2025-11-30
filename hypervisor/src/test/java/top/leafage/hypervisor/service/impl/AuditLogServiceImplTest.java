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

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

    private AuditLog entity;

    @BeforeEach
    void setUp() {
        entity = new AuditLog();
        entity.setOperation("test");
        entity.setResource("test");
        entity.setOldValue("old");
        entity.setNewValue("new");
        entity.setStatusCode(200);
    }

    @Test
    void retrieve() {
        Page<AuditLog> page = new PageImpl<>(List.of(entity));

        when(auditLogRepository.findAll(ArgumentMatchers.<Specification<AuditLog>>any(),
                any(Pageable.class))).thenReturn(page);

        Page<AuditLogVO> voPage = auditLogService.retrieve(0, 2, "id", true, "test");
        assertEquals(1, voPage.getTotalElements());
        assertEquals(1, voPage.getContent().size());
        verify(auditLogRepository).findAll(ArgumentMatchers.<Specification<AuditLog>>any(), any(Pageable.class));
    }

    @Test
    void fetch() {
        when(auditLogRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        AuditLogVO vo = auditLogService.fetch(anyLong());
        assertNotNull(vo);
        assertEquals("test", vo.operation());
        verify(auditLogRepository).findById(anyLong());
    }

    @Test
    void fetch_not_found() {
        when(auditLogRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> auditLogService.fetch(anyLong())
        );
        assertEquals("audit log not found: 0", exception.getMessage());
        verify(auditLogRepository).findById(anyLong());
    }

    @Test
    void remove() {
        when(auditLogRepository.existsById(anyLong())).thenReturn(true);
        auditLogService.remove(1L);

        verify(auditLogRepository).deleteById(anyLong());
    }

    @Test
    void remove_not_found() {
        when(auditLogRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> auditLogService.remove(anyLong())
        );
        assertEquals("audit log not found: 0", exception.getMessage());
    }

}