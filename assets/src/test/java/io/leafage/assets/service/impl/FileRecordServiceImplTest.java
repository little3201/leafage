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

package io.leafage.assets.service.impl;

import io.leafage.assets.domain.FileRecord;
import io.leafage.assets.repository.FileRecordRepository;
import io.leafage.assets.vo.FileRecordVO;
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
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

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


    @Test
    void retrieve() {
        Page<FileRecord> page = new PageImpl<>(List.of(mock(FileRecord.class)));

        given(this.fileRecordRepository.findAll(ArgumentMatchers.<Specification<FileRecord>>any(),
                any(Pageable.class))).willReturn(page);

        Page<FileRecordVO> voPage = fileRecordService.retrieve(0, 2, "id", true, "test");
        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        given(fileRecordRepository.findById(anyLong())).willReturn(Optional.ofNullable(mock(FileRecord.class)));

        FileRecordVO vo = fileRecordService.fetch(anyLong());

        Assertions.assertNotNull(vo);
    }

    @Test
    void exists() {
        given(fileRecordRepository.existsByNameAndIdNot(anyString(), anyLong())).willReturn(true);

        boolean exists = fileRecordService.exists("test", 1L);

        Assertions.assertTrue(exists);
    }

    @Test
    void exists_id_null() {
        given(fileRecordRepository.existsByName(anyString())).willReturn(true);

        boolean exists = fileRecordService.exists("test", null);

        Assertions.assertTrue(exists);
    }

    @Test
    void upload() {
        given(fileRecordRepository.saveAndFlush(any(FileRecord.class))).willReturn(mock(FileRecord.class));

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());
        FileRecordVO vo = fileRecordService.upload(file);

        verify(fileRecordRepository, times(1)).saveAndFlush(any(FileRecord.class));
        Assertions.assertNotNull(vo);
    }

    @Test
    void download() {
        FileRecord fileRecord = new FileRecord();
        fileRecord.setName("test.jpg");
        fileRecord.setPath("/text.jpg");
        given(fileRecordRepository.findById(anyLong())).willReturn(Optional.of(fileRecord));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String fileName = fileRecordService.download(1L, out);
        Assertions.assertNotNull(fileName);
    }

    @Test
    void remove() {
        fileRecordService.remove(11L);

        verify(this.fileRecordRepository, times(1)).deleteById(anyLong());
    }
}