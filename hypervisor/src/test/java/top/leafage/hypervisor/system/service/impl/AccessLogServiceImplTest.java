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

package top.leafage.hypervisor.system.service.impl;

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
import top.leafage.hypervisor.system.domain.AccessLog;
import top.leafage.hypervisor.system.domain.vo.AccessLogVO;
import top.leafage.hypervisor.system.repository.AccessLogRepository;
import top.leafage.hypervisor.system.service.impl.AccessLogServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

    private AccessLog entity;

    @BeforeEach
    void setUp() {
        entity = new AccessLog();
        entity.setUrl("test");
        entity.setHttpMethod("test");
        entity.setParams("test");
    }

    @Test
    void retrieve() {
        Page<AccessLog> page = new PageImpl<>(List.of(entity));

        when(accessLogRepository.findAll(ArgumentMatchers.<Specification<AccessLog>>any(),
                any(Pageable.class))).thenReturn(page);

        Page<AccessLogVO> voPage = accessLogService.retrieve(0, 2, "id", true, "test");
        assertEquals(1, voPage.getTotalElements());
        assertEquals(1, voPage.getContent().size());
        verify(accessLogRepository).findAll(ArgumentMatchers.<Specification<AccessLog>>any(), any(Pageable.class));
    }

    @Test
    void fetch() {
        when(accessLogRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        AccessLogVO vo = accessLogService.fetch(anyLong());
        assertNotNull(vo);
        assertEquals("test", vo.url());
        verify(accessLogRepository).findById(anyLong());
    }

    @Test
    void fetch_not_found() {
        when(accessLogRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> accessLogService.fetch(anyLong())
        );
        assertEquals("access log not found: 0", exception.getMessage());
        verify(accessLogRepository).findById(anyLong());
    }

    @Test
    void remove() {
        when(accessLogRepository.existsById(anyLong())).thenReturn(true);
        accessLogService.remove(1L);

        verify(accessLogRepository).deleteById(anyLong());
    }

    @Test
    void remove_not_found() {
        when(accessLogRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> accessLogService.remove(anyLong())
        );
        assertEquals("access log not found: 0", exception.getMessage());
    }

    @Test
    void clear() {
        accessLogService.clear();

        verify(accessLogRepository).deleteAll();
    }

}