/*
 *
 *  * Copyright(c) 2019-present the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *       https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package top.leafage.hypervisor.system.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Query;
import top.leafage.hypervisor.system.domain.OperationLog;
import top.leafage.hypervisor.system.domain.vo.OperationLogVO;
import top.leafage.hypervisor.system.repository.OperationLogRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
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

    @Mock
    private JdbcAggregateTemplate jdbcAggregateTemplate;

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
        when(jdbcAggregateTemplate.findAll(any(Query.class), eq(OperationLog.class)))
                .thenReturn(List.of(entity));

        when(jdbcAggregateTemplate.count(any(Query.class), eq(OperationLog.class)))
                .thenReturn(1L);

        Page<OperationLogVO> voPage = operationLogService.retrieve(0, 2, "id", true, "module:like:test");
        assertEquals(1, voPage.getTotalElements());
        assertEquals(1, voPage.getContent().size());
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

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
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

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
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