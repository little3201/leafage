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
import top.leafage.hypervisor.system.domain.SchedulerLog;
import top.leafage.hypervisor.system.domain.vo.SchedulerLogVO;
import top.leafage.hypervisor.system.repository.SchedulerLogRepository;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.verify;

/**
 * scheduler log service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class SchedulerLogServiceImplTest {

    @Mock
    private SchedulerLogRepository schedulerLogRepository;

    @Mock
    private JdbcAggregateTemplate jdbcAggregateTemplate;

    @InjectMocks
    private SchedulerLogServiceImpl schedulerLogService;

    private SchedulerLog entity;

    @BeforeEach
    void setUp() {
        entity = new SchedulerLog();
        entity.setName("test");
        entity.setStartTime(Instant.now());
        entity.setStatus(SchedulerLog.ScheduleStatus.RUNNING);
        entity.setRecord("description");
    }

    @Test
    void retrieve() {
        when(jdbcAggregateTemplate.findAll(any(Query.class), eq(SchedulerLog.class)))
                .thenReturn(List.of(entity));

        when(jdbcAggregateTemplate.count(any(Query.class), eq(SchedulerLog.class)))
                .thenReturn(1L);

        Page<SchedulerLogVO> voPage = schedulerLogService.retrieve(0, 2, "id", true, "name:eq:test");
        assertEquals(1, voPage.getTotalElements());
        assertEquals(1, voPage.getContent().size());
    }

    @Test
    void fetch() {
        when(schedulerLogRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        SchedulerLogVO vo = schedulerLogService.fetch(anyLong());
        assertNotNull(vo);
        assertEquals("test", vo.name());
        verify(schedulerLogRepository).findById(anyLong());
    }

    @Test
    void fetch_not_found() {
        when(schedulerLogRepository.findById(anyLong())).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> schedulerLogService.fetch(anyLong())
        );
        assertEquals("scheduler log not found: 0", exception.getMessage());
        verify(schedulerLogRepository).findById(anyLong());
    }

    @Test
    void remove() {
        when(schedulerLogRepository.existsById(anyLong())).thenReturn(true);
        schedulerLogService.remove(1L);

        verify(schedulerLogRepository).deleteById(anyLong());
    }

    @Test
    void remove_not_found() {
        when(schedulerLogRepository.existsById(anyLong())).thenReturn(false);

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> schedulerLogService.remove(anyLong())
        );
        assertEquals("scheduler log not found: 0", exception.getMessage());
    }

    @Test
    void clear() {
        schedulerLogService.clear();

        verify(schedulerLogRepository).deleteAll();
    }

}