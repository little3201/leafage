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
import io.leafage.hypervisor.vo.OperationLogVO;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;


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

    @Test
    void retrieve() {
        Page<OperationLog> page = new PageImpl<>(List.of(mock(OperationLog.class)));

        given(this.operationLogRepository.findAll(ArgumentMatchers.<Specification<OperationLog>>any(),
                any(Pageable.class))).willReturn(page);

        Page<OperationLogVO> voPage = operationLogService.retrieve(0, 2, "id", true, "test");

        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        given(this.operationLogRepository.findById(anyLong())).willReturn(Optional.ofNullable(mock(OperationLog.class)));

        OperationLogVO vo = operationLogService.fetch(anyLong());

        Assertions.assertNotNull(vo);
    }

    @Test
    void create() {
        given(this.operationLogRepository.saveAndFlush(any(OperationLog.class))).willReturn(mock(OperationLog.class));

        OperationLogVO vo = operationLogService.create(mock(OperationLogDTO.class));

        verify(this.operationLogRepository, times(1)).saveAndFlush(any(OperationLog.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void remove() {
        operationLogService.remove(1L);

        verify(this.operationLogRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void clear() {
        operationLogService.clear();

        verify(this.operationLogRepository, times(1)).deleteAll();
    }

}