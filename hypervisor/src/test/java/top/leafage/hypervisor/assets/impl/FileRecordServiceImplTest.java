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

package top.leafage.hypervisor.assets.impl;

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
import org.springframework.mock.web.MockMultipartFile;
import top.leafage.hypervisor.assets.domain.FileRecord;
import top.leafage.hypervisor.assets.domain.vo.FileRecordVO;
import top.leafage.hypervisor.assets.repository.FileRecordRepository;
import top.leafage.hypervisor.assets.service.impl.FileRecordServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.verify;

/**
 * file record service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class FileRecordServiceImplTest {

    @Mock
    private FileRecordRepository fileRecordRepository;

    @InjectMocks
    private FileRecordServiceImpl fileRecordService;


    private FileRecord entity;

    @BeforeEach
    void setUp() {
        entity = new FileRecord(null, "test", ".txt", "/text/plain", "test", 121L, false, true, false);
    }

    @Test
    void retrieve() {
        Page<FileRecord> page = new PageImpl<>(List.of(entity));

        when(fileRecordRepository.findAll(ArgumentMatchers.<Specification<FileRecord>>any(),
                any(Pageable.class))).thenReturn(page);

        Page<FileRecordVO> voPage = fileRecordService.retrieve(0, 2, "id", true, "name:like:test");
        assertEquals(1, voPage.getTotalElements());
        assertEquals(1, voPage.getContent().size());
        verify(fileRecordRepository).findAll(ArgumentMatchers.<Specification<FileRecord>>any(), any(Pageable.class));
    }

    @Test
    void fetch() {
        when(fileRecordRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        FileRecordVO vo = fileRecordService.fetch(anyLong());
        assertNotNull(vo);
        assertEquals("test", vo.name());
        verify(fileRecordRepository).findById(anyLong());
    }

    @Test
    void fetch_not_found() {
        when(fileRecordRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> fileRecordService.fetch(anyLong())
        );
        assertEquals("file record not found: 0", exception.getMessage());
        verify(fileRecordRepository).findById(anyLong());
    }

    @Test
    void upload() {
        when(fileRecordRepository.saveAndFlush(any(FileRecord.class))).thenReturn(entity);

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());
        FileRecordVO vo = fileRecordService.upload(file);

        assertNotNull(vo);
        assertEquals("test", vo.name());
        verify(fileRecordRepository).saveAndFlush(any(FileRecord.class));
    }

    @Test
    void remove() {
        when(fileRecordRepository.existsById(anyLong())).thenReturn(true);

        fileRecordService.remove(11L);
        verify(fileRecordRepository).deleteById(anyLong());
    }

    @Test
    void remove_not_found() {
        when(fileRecordRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> fileRecordService.remove(anyLong())
        );
        assertEquals("file record not found: 0", exception.getMessage());
    }

}