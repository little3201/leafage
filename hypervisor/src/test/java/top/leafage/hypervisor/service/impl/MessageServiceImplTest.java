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
import top.leafage.hypervisor.domain.Message;
import top.leafage.hypervisor.domain.dto.MessageDTO;
import top.leafage.hypervisor.domain.vo.MessageVO;
import top.leafage.hypervisor.repository.MessageRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.*;


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

    private MessageDTO dto;
    private Message entity;

    @BeforeEach
    void setUp() {
        dto = new MessageDTO();
        dto.setTitle("test");
        dto.setBody("body");
        dto.setReceiver("demo");

        entity = MessageDTO.toEntity(dto);
    }

    @Test
    void retrieve() {
        Page<Message> page = new PageImpl<>(List.of(mock(Message.class)));

        when(messageRepository.findAll(ArgumentMatchers.<Specification<Message>>any(),
                any(Pageable.class))).thenReturn(page);

        Page<MessageVO> voPage = messageService.retrieve(0, 2, "id", true, "test");
        assertEquals(1, voPage.getTotalElements());
        assertEquals(1, voPage.getContent().size());
        verify(messageRepository).findAll(ArgumentMatchers.<Specification<Message>>any(), any(Pageable.class));
    }

    @Test
    void fetch() {
        when(messageRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        MessageVO vo = messageService.fetch(anyLong());
        assertNotNull(vo);
        assertEquals("test", vo.title());
        verify(messageRepository).findById(anyLong());
    }

    @Test
    void fetch_not_found() {
        when(messageRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> messageService.fetch(anyLong())
        );
        assertEquals("message not found: 0", exception.getMessage());
        verify(messageRepository).findById(anyLong());
    }

    @Test
    void create() {
        when(messageRepository.existsByTitle("test")).thenReturn(false);
        when(messageRepository.saveAndFlush(any(Message.class))).thenReturn(entity);

        MessageVO vo = messageService.create(dto);
        assertNotNull(vo);
        assertEquals("test", vo.title());
        verify(messageRepository).saveAndFlush(any(Message.class));
    }

    @Test
    void create_name_conflict() {
        when(messageRepository.existsByTitle("test")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> messageService.create(dto)
        );
        assertEquals("title already exists: test", exception.getMessage());
        verify(messageRepository, never()).save(any());
    }

    @Test
    void modify() {
        when(messageRepository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(messageRepository.existsByTitle("demo")).thenReturn(false);
        when(messageRepository.save(any(Message.class))).thenReturn(entity);

        dto.setTitle("demo");
        MessageVO vo = messageService.modify(1L, dto);
        assertNotNull(vo);
        assertEquals("demo", vo.title());
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void modify_username_conflict() {
        when(messageRepository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(messageRepository.existsByTitle("demo")).thenReturn(true);

        dto.setTitle("demo");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> messageService.modify(1L, dto)
        );
        assertEquals("title already exists: demo", exception.getMessage());
    }

    @Test
    void remove() {
        when(messageRepository.existsById(anyLong())).thenReturn(true);
        messageService.remove(1L);

        verify(messageRepository).deleteById(anyLong());
    }

    @Test
    void remove_not_found() {
        when(messageRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> messageService.remove(anyLong())
        );
        assertEquals("message not found: 0", exception.getMessage());
    }
}