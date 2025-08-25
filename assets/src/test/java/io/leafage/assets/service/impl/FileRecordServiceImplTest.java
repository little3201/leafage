/*
 *  Copyright 2018-2025 little3201.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.leafage.assets.service.impl;

import io.leafage.assets.domain.FileRecord;
import io.leafage.assets.dto.FileRecordDTO;
import io.leafage.assets.repository.FileRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.BDDMockito.given;

/**
 * tag service test
 *
 * @author wq li
 */
@ExtendWith(MockitoExtension.class)
class FileRecordServiceImplTest {

    @Mock
    private FileRecordRepository fileRecordRepository;

    @InjectMocks
    private FileRecordServiceImpl fileRecordService;

    private FileRecordDTO dto;

    @BeforeEach
    void setUp() {
        dto = new FileRecordDTO();
        dto.setName("test");
    }

    @Test
    void retrieve() {
        given(this.fileRecordRepository.findAllBy(Mockito.any(PageRequest.class))).willReturn(Flux.just(Mockito.mock(FileRecord.class)));

        given(this.fileRecordRepository.count()).willReturn(Mono.just(Mockito.anyLong()));

        StepVerifier.create(this.fileRecordService.retrieve(0, 2, "id", true, "name:like:a")).expectNextCount(1).verifyComplete();
    }

    @Test
    void fetch() {
        given(this.fileRecordRepository.findById(Mockito.anyLong()))
                .willReturn(Mono.just(Mockito.mock(FileRecord.class)));

        StepVerifier.create(fileRecordService.fetch(Mockito.anyLong())).expectNextCount(1).verifyComplete();
    }

    @Test
    void exists() {
        given(this.fileRecordRepository.existsByNameAndIdNot(Mockito.anyString(), Mockito.anyLong())).willReturn(Mono.just(Boolean.TRUE));

        StepVerifier.create(fileRecordService.exists("test", 1L)).expectNext(Boolean.TRUE).verifyComplete();
    }

    @Test
    void exists_id_null() {
        given(this.fileRecordRepository.existsByName(Mockito.anyString())).willReturn(Mono.just(Boolean.TRUE));

        StepVerifier.create(fileRecordService.exists("test", null)).expectNext(Boolean.TRUE).verifyComplete();
    }

    @Test
    void upload() {
        given(this.fileRecordRepository.save(Mockito.any(FileRecord.class))).willReturn(Mono.just(Mockito.mock(FileRecord.class)));

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());
        StepVerifier.create(fileRecordService.upload(file)).expectNextCount(1).verifyComplete();
    }

    @Test
    void download() {
        FileRecord fileRecord = new FileRecord();
        fileRecord.setName("test.jpg");
        fileRecord.setPath("/text.jpg");
        given(this.fileRecordRepository.findById(Mockito.anyLong())).willReturn(Mono.just(fileRecord));

        StepVerifier.create(fileRecordService.download(1L)).expectNextCount(1).verifyComplete();
    }

    @Test
    void remove() {
        given(this.fileRecordRepository.deleteById(Mockito.anyLong())).willReturn(Mono.empty());

        StepVerifier.create(fileRecordService.remove(Mockito.anyLong())).verifyComplete();
    }
}