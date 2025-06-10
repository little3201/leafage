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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.BDDMockito.given;

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
    private FileRecordServiceImpl fileService;


    @BeforeEach
    void setUp() {
    }

    @Test
    void retrieve() {
        Page<FileRecord> page = new PageImpl<>(List.of(Mockito.mock(FileRecord.class)));

        given(this.fileRecordRepository.findAll(Mockito.any(Pageable.class))).willReturn(page);

        Page<FileRecordVO> voPage = fileService.retrieve(0, 2, "id", true, "test");
        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void upload() {
    }
}