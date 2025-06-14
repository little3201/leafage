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

import io.leafage.hypervisor.domain.Message;
import io.leafage.hypervisor.dto.MessageDTO;
import io.leafage.hypervisor.repository.MessageRepository;
import io.leafage.hypervisor.vo.MessageVO;
import org.junit.jupiter.api.Assertions;
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
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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


    @Test
    void retrieve() {
        Page<Message> page = new PageImpl<>(List.of(Mockito.mock(Message.class)));

        given(this.messageRepository.findAll(Mockito.any(Pageable.class))).willReturn(page);

        Page<MessageVO> voPage = messageService.retrieve(0, 2, "id", true, "test");
        Assertions.assertNotNull(voPage.getContent());
    }

    @Test
    void fetch() {
        given(this.messageRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(Mockito.mock(Message.class)));

        MessageVO vo = messageService.fetch(Mockito.anyLong());

        Assertions.assertNotNull(vo);
    }


    @Test
    void create() {
        given(this.messageRepository.saveAndFlush(Mockito.any(Message.class))).willReturn(Mockito.mock(Message.class));

        MessageVO vo = messageService.create(Mockito.mock(MessageDTO.class));

        verify(this.messageRepository, times(1)).saveAndFlush(Mockito.any(Message.class));
        Assertions.assertNotNull(vo);
    }
}