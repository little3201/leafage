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

package top.leafage.assets.service.impl;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveSelectOperation;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import top.leafage.assets.domain.FileRecord;
import top.leafage.assets.repository.FileRecordRepository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * tag service test
 *
 * @author wq li
 */
@ExtendWith(MockitoExtension.class)
class FileRecordServiceImplTest {

    @Mock
    private FileRecordRepository fileRecordRepository;

    @Mock
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    @InjectMocks
    private FileRecordServiceImpl fileRecordService;

    private FileRecord entity;

    @BeforeEach
    void setUp() {
        entity = new FileRecord();
        entity.setName("test");
    }

    @Test
    void retrieve() {
        ReactiveSelectOperation.ReactiveSelect<FileRecord> select = mock(ReactiveSelectOperation.ReactiveSelect.class);
        ReactiveSelectOperation.TerminatingSelect<FileRecord> terminating = mock(ReactiveSelectOperation.TerminatingSelect.class);

        given(r2dbcEntityTemplate.select(FileRecord.class)).willReturn(select);
        given(select.matching(any(Query.class))).willReturn(terminating);
        given(terminating.all()).willReturn(Flux.just(entity));
        given(r2dbcEntityTemplate.count(any(Query.class), eq(FileRecord.class))).willReturn(Mono.just(1L));

        StepVerifier.create(fileRecordService.retrieve(0, 2, "id", true, "name:like:test"))
                .assertNext(page -> {
                    assertThat(page.getContent()).hasSize(1);
                    AssertionsForClassTypes.assertThat(page.getTotalElements()).isEqualTo(1);
                    AssertionsForClassTypes.assertThat(page.getNumber()).isEqualTo(0);
                    AssertionsForClassTypes.assertThat(page.getSize()).isEqualTo(2);
                }).verifyComplete();
    }

    @Test
    void fetch() {
        given(this.fileRecordRepository.findById(anyLong()))
                .willReturn(Mono.just(mock(FileRecord.class)));

        StepVerifier.create(fileRecordService.fetch(anyLong())).expectNextCount(1).verifyComplete();
    }

    @Test
    void remove() {
        given(this.fileRecordRepository.deleteById(anyLong())).willReturn(Mono.empty());

        StepVerifier.create(fileRecordService.remove(anyLong())).verifyComplete();
    }
}