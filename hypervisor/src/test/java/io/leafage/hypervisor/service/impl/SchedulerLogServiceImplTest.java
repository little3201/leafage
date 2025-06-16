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

import io.leafage.hypervisor.domain.SchedulerLog;
import io.leafage.hypervisor.dto.SchedulerLogDTO;
import io.leafage.hypervisor.repository.SchedulerLogRepository;
import io.leafage.hypervisor.vo.SchedulerLogVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
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

    @InjectMocks
    private SchedulerLogServiceImpl schedulerLogService;

    @Test
    void retrieve() {
        Page<SchedulerLog> page = new PageImpl<>(List.of(Mockito.mock(SchedulerLog.class)));

        given(this.schedulerLogRepository.findAll(ArgumentMatchers.<Specification<SchedulerLog>>any(),
                Mockito.any(Pageable.class))).willReturn(page);

        Page<SchedulerLogVO> voPage = schedulerLogService.retrieve(0, 2, "id", true, "test");

        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        given(this.schedulerLogRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(Mockito.mock(SchedulerLog.class)));

        SchedulerLogVO vo = schedulerLogService.fetch(Mockito.anyLong());

        Assertions.assertNotNull(vo);
    }

    @Test
    void create() {
        given(this.schedulerLogRepository.saveAndFlush(Mockito.any(SchedulerLog.class))).willReturn(Mockito.mock(SchedulerLog.class));

        SchedulerLogVO vo = schedulerLogService.create(Mockito.mock(SchedulerLogDTO.class));

        verify(this.schedulerLogRepository, times(1)).saveAndFlush(Mockito.any(SchedulerLog.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void remove() {
        schedulerLogService.remove(1L);

        verify(this.schedulerLogRepository, times(1)).deleteById(Mockito.anyLong());
    }

    @Test
    void clear() {
        schedulerLogService.clear();

        verify(this.schedulerLogRepository, times(1)).deleteAll();
    }

}