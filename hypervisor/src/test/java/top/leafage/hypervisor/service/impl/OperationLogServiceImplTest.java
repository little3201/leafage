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
import top.leafage.hypervisor.domain.OperationLog;
import top.leafage.hypervisor.domain.vo.OperationLogVO;
import top.leafage.hypervisor.repository.OperationLogRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


/**
 * operation log service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class OperationLogServiceImplTest {

    @Mock
    private OperationLogRepository operationLogRepository;

    @InjectMocks
    private OperationLogServiceImpl operationLogService;

    private OperationLog entity;

    @BeforeEach
    void setUp() {
        entity = new OperationLog();
        entity.setModule("test");
        entity.setBody("body");
        entity.setParams("params");
        entity.setAction("test");
        entity.setStatusCode(200);
    }

    @Test
    void retrieve() {
        Page<OperationLog> page = new PageImpl<>(List.of(entity));

        when(operationLogRepository.findAll(ArgumentMatchers.<Specification<OperationLog>>any(),
                any(Pageable.class))).thenReturn(page);

        Page<OperationLogVO> voPage = operationLogService.retrieve(0, 2, "id", true, "module:like:test");
        assertEquals(1, voPage.getTotalElements());
        assertEquals(1, voPage.getContent().size());
        verify(operationLogRepository).findAll(ArgumentMatchers.<Specification<OperationLog>>any(), any(Pageable.class));
    }

    @Test
    void fetch() {
        when(operationLogRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        OperationLogVO vo = operationLogService.fetch(anyLong());
        assertNotNull(vo);
        assertEquals("test", vo.module());
        verify(operationLogRepository).findById(anyLong());
    }

    @Test
    void fetch_not_found() {
        when(operationLogRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> operationLogService.fetch(anyLong())
        );
        assertEquals("operation log not found: 0", exception.getMessage());
        verify(operationLogRepository).findById(anyLong());
    }

    @Test
    void remove() {
        when(operationLogRepository.existsById(anyLong())).thenReturn(true);
        operationLogService.remove(1L);

        verify(operationLogRepository).deleteById(anyLong());
    }

    @Test
    void remove_not_found() {
        when(operationLogRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> operationLogService.remove(anyLong())
        );
        assertEquals("operation log not found: 0", exception.getMessage());
    }

    @Test
    void clear() {
        operationLogService.clear();

        verify(operationLogRepository).deleteAll();
    }

}