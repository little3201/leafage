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

package io.leafage.hypervisor.service.impl;

import io.leafage.hypervisor.domain.Message;
import io.leafage.hypervisor.dto.MessageDTO;
import io.leafage.hypervisor.repository.MessageRepository;
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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * message service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Mock
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    private MessageDTO dto;
    private Message entity;

    @BeforeEach
    void setUp() {
        dto = new MessageDTO();
        dto.setTitle("标题");
        dto.setSummary("这个是摘要内容");
        dto.setBody("这个是正文内容");
        dto.setReceiver("test");

        entity = new Message();
        entity.setTitle("标题");
        entity.setSummary("这个是摘要内容");
        entity.setBody("这个是正文内容");
        entity.setReceiver("test");
    }

    @Test
    void retrieve() {
        ReactiveSelectOperation.ReactiveSelect<Message> select = mock(ReactiveSelectOperation.ReactiveSelect.class);
        ReactiveSelectOperation.TerminatingSelect<Message> terminating = mock(ReactiveSelectOperation.TerminatingSelect.class);

        given(r2dbcEntityTemplate.select(Message.class)).willReturn(select);
        given(select.matching(any(Query.class))).willReturn(terminating);
        given(terminating.all()).willReturn(Flux.just(entity));
        given(r2dbcEntityTemplate.count(any(Query.class), eq(Message.class))).willReturn(Mono.just(1L));

        StepVerifier.create(messageService.retrieve(0, 2, "id", true, "test")).assertNext(page -> {
            assertThat(page.getContent()).hasSize(1);
            AssertionsForClassTypes.assertThat(page.getTotalElements()).isEqualTo(1);
            AssertionsForClassTypes.assertThat(page.getNumber()).isEqualTo(0);
            AssertionsForClassTypes.assertThat(page.getSize()).isEqualTo(2);
        }).verifyComplete();
    }

    @Test
    void fetch() {
        given(this.messageRepository.findById(anyLong())).willReturn(Mono.just(mock(Message.class)));

        given(this.messageRepository.save(any(Message.class))).willReturn(Mono.just(mock(Message.class)));

        StepVerifier.create(messageService.fetch(1L)).expectNextCount(1).verifyComplete();
    }

    @Test
    void create() {
        given(this.messageRepository.save(any(Message.class))).willReturn(Mono.just(mock(Message.class)));

        StepVerifier.create(messageService.create(dto)).expectNextCount(1).verifyComplete();
    }

    @Test
    void remove() {
        given(this.messageRepository.deleteById(anyLong())).willReturn(Mono.empty());

        StepVerifier.create(messageService.remove(1L)).verifyComplete();
    }
}