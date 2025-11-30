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

package top.leafage.hypervisor.controller;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;
import top.leafage.hypervisor.domain.dto.MessageDTO;
import top.leafage.hypervisor.domain.vo.MessageVO;
import top.leafage.hypervisor.service.MessageService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/**
 * message controller test
 *
 * @author wq li
 **/
@WithMockUser
@WebMvcTest(MessageController.class)
class MessageControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private MessageService messageService;

    private MessageVO vo;

    private MessageDTO dto;

    @BeforeEach
    void setUp() {
        vo = new MessageVO(1L, "test", "test", "admin", false);

        dto = new MessageDTO();
        dto.setTitle("test");
        dto.setReceiver("23234");
        dto.setBody("content");
    }

    @Test
    void retrieve() {
        Page<@NonNull MessageVO> voPage = new PageImpl<>(List.of(vo), mock(PageRequest.class), 2L);

        when(messageService.retrieve(anyInt(), anyInt(), eq("id"),
                anyBoolean(), anyString())).thenReturn(voPage);

        assertThat(mvc.get().uri("/messages")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "true")
                .queryParam("filters", "title:like:test")
        )
                .hasStatusOk()
                .bodyJson().extractingPath("$.content")
                .convertTo(InstanceOfAssertFactories.list(MessageVO.class))
                .hasSize(1)
                .element(0).satisfies(vo -> assertThat(vo.title()).isEqualTo("test"));

        verify(messageService).retrieve(anyInt(), anyInt(), anyString(), anyBoolean(), anyString());
    }

    @Test
    void retrieve_error() {
        when(messageService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/messages")
                .queryParam("page", "0")
                .queryParam("size", "2")
                .queryParam("sortBy", "id")
                .queryParam("descending", "false")
                .queryParam("filters", "title:like:test")
        )
                .hasStatus5xxServerError();
    }

    @Test
    void fetch() {
        when(messageService.fetch(anyLong())).thenReturn(vo);

        assertThat(mvc.get().uri("/messages/{id}", anyLong()))
                .hasStatusOk()
                .bodyJson()
                .convertTo(MessageVO.class)
                .satisfies(vo -> assertThat(vo.title()).isEqualTo("test"));
    }

    @Test
    void fetch_error() {
        when(messageService.fetch(anyLong())).thenThrow(new RuntimeException());

        assertThat(mvc.get().uri("/messages/{id}", anyLong()))
                .hasStatus5xxServerError();
    }

    @Test
    void create() {
        when(messageService.create(any(MessageDTO.class))).thenReturn(vo);

        assertThat(mvc.post().uri("/messages").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader())
        )
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(MessageVO.class)
                .satisfies(vo -> assertThat(vo.title()).isEqualTo("test"));
    }

    @Test
    void create_error() {
        when(messageService.create(any(MessageDTO.class))).thenThrow(new RuntimeException());

        assertThat(mvc.post().uri("/messages").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader())
        )
                .hasStatus5xxServerError();
    }

    @Test
    void modify() {
        when(messageService.modify(anyLong(), any(MessageDTO.class))).thenReturn(vo);

        assertThat(mvc.put().uri("/messages/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader())
        )
                .hasStatus(HttpStatus.ACCEPTED)
                .bodyJson()
                .convertTo(MessageVO.class)
                .satisfies(vo -> assertThat(vo.title()).isEqualTo("test"));
    }

    @Test
    void modify_error() {
        when(messageService.modify(anyLong(), any(MessageDTO.class))).thenThrow(new RuntimeException());

        assertThat(mvc.put().uri("/messages/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)).with(csrf().asHeader())
        )
                .hasStatus5xxServerError();
    }

    @Test
    void remove() {
        this.messageService.remove(anyLong());

        assertThat(mvc.delete().uri("/messages/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void remove_error() {
        doThrow(new RuntimeException()).when(messageService).remove(anyLong());

        assertThat(mvc.delete().uri("/messages/{id}", anyLong()).with(csrf().asHeader()))
                .hasStatus5xxServerError();
    }

}