/*
 * Copyright (c) 2026.  little3201.
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
import top.leafage.hypervisor.system.domain.AccessLog;
import top.leafage.hypervisor.system.repository.AccessLogRepository;
import top.leafage.hypervisor.system.service.impl.AccessLogServiceImpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * access log service test
 *
 * @author wq li
 */
@ExtendWith(MockitoExtension.class)
class AccessLogServiceImplTest {

    @Mock
    private AccessLogRepository accessLogRepository;

    @InjectMocks
    private AccessLogServiceImpl accessLogService;

    @Mock
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    private AccessLog entity;

    @BeforeEach
    void setUp() {
        entity = new AccessLog();
        entity.setUrl("test");
        entity.setHttpMethod("test");
        entity.setParams("test");
        entity.setBody("test");
    }

    @Test
    void retrieve() {
        ReactiveSelectOperation.ReactiveSelect<AccessLog> select = mock(ReactiveSelectOperation.ReactiveSelect.class);
        ReactiveSelectOperation.TerminatingSelect<AccessLog> terminating = mock(ReactiveSelectOperation.TerminatingSelect.class);

        given(r2dbcEntityTemplate.select(AccessLog.class)).willReturn(select);
        given(select.matching(any(Query.class))).willReturn(terminating);
        given(terminating.all()).willReturn(Flux.just(entity));
        given(r2dbcEntityTemplate.count(any(Query.class), eq(AccessLog.class))).willReturn(Mono.just(1L));

        StepVerifier.create(accessLogService.retrieve(0, 2, "id", true, "url:like:test"))
                .assertNext(page -> {
                    assertThat(page.getContent()).hasSize(1);
                    AssertionsForClassTypes.assertThat(page.getTotalElements()).isEqualTo(1);
                    AssertionsForClassTypes.assertThat(page.getNumber()).isEqualTo(0);
                    AssertionsForClassTypes.assertThat(page.getSize()).isEqualTo(2);
                }).verifyComplete();
    }

    @Test
    void fetch() {
        given(this.accessLogRepository.findById(anyLong())).willReturn(Mono.just(mock(AccessLog.class)));
        StepVerifier.create(accessLogService.fetch(anyLong())).expectNextCount(1).verifyComplete();
    }

}