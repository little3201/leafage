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
import top.leafage.hypervisor.domain.AccessLog;
import top.leafage.hypervisor.domain.vo.AccessLogVO;
import top.leafage.hypervisor.repository.AccessLogRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * access log service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class AccessLogServiceImplTest {

    @Mock
    private AccessLogRepository accessLogRepository;

    @InjectMocks
    private AccessLogServiceImpl accessLogService;

    @Test
    void retrieve() {
        Page<AccessLog> page = new PageImpl<>(List.of(mock(AccessLog.class)));

        given(this.accessLogRepository.findAll(ArgumentMatchers.<Specification<AccessLog>>any(),
                any(Pageable.class))).willReturn(page);

        Page<AccessLogVO> voPage = accessLogService.retrieve(0, 2, "id", true, "test");

        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        given(this.accessLogRepository.findById(anyLong())).willReturn(Optional.of(mock(AccessLog.class)));

        AccessLogVO vo = accessLogService.fetch(anyLong());

        Assertions.assertNotNull(vo);
    }

    @Test
    void remove() {
        accessLogService.remove(1L);

        verify(this.accessLogRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void clear() {
        accessLogService.clear();

        verify(this.accessLogRepository, times(1)).deleteAll();
    }

}