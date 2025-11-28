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
import top.leafage.hypervisor.domain.SchedulerLog;
import top.leafage.hypervisor.domain.vo.SchedulerLogVO;
import top.leafage.hypervisor.repository.SchedulerLogRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

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
        Page<SchedulerLog> page = new PageImpl<>(List.of(mock(SchedulerLog.class)));

        given(this.schedulerLogRepository.findAll(ArgumentMatchers.<Specification<SchedulerLog>>any(),
                any(Pageable.class))).willReturn(page);

        Page<SchedulerLogVO> voPage = schedulerLogService.retrieve(0, 2, "id", true, "test");

        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        given(this.schedulerLogRepository.findById(anyLong())).willReturn(Optional.of(mock(SchedulerLog.class)));

        SchedulerLogVO vo = schedulerLogService.fetch(anyLong());

        Assertions.assertNotNull(vo);
    }

    @Test
    void remove() {
        schedulerLogService.remove(1L);

        verify(this.schedulerLogRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void clear() {
        schedulerLogService.clear();

        verify(this.schedulerLogRepository, times(1)).deleteAll();
    }

}