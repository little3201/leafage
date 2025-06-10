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

package io.leafage.hypervisor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.leafage.hypervisor.dto.MessageDTO;
import io.leafage.hypervisor.service.MessageService;
import io.leafage.hypervisor.vo.MessageVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * message controller test
 *
 * @author wq li
 **/
@WithMockUser
@ExtendWith(SpringExtension.class)
@WebMvcTest(MessageController.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private MessageService messageService;

    private MessageVO vo;

    private MessageDTO dto;

    @BeforeEach
    void setUp() {
        vo = new MessageVO();
        vo.setId(1L);
        vo.setTitle("test");
        vo.setReceiver("23234");
        vo.setContent("content");

        dto = new MessageDTO();
        dto.setTitle("test");
        dto.setReceiver("23234");
        dto.setContent("content");
    }

    @Test
    void retrieve() throws Exception {
        Page<MessageVO> voPage = new PageImpl<>(List.of(vo), Mockito.mock(PageRequest.class), 2L);

        given(this.messageService.retrieve(Mockito.anyInt(), Mockito.anyInt(), eq("id"),
                Mockito.anyBoolean(), eq("test"))).willReturn(voPage);

        mvc.perform(get("/messages")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "true")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andDo(print())
                .andReturn();
    }

    @Test
    void retrieve_error() throws Exception {
        given(this.messageService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyBoolean(), Mockito.anyString())).willThrow(new RuntimeException());

        mvc.perform(get("/messages")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("sortBy", "id")
                        .queryParam("name", "test"))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn();
    }

    @Test
    void fetch() throws Exception {
        given(this.messageService.fetch(Mockito.anyLong())).willReturn(vo);

        mvc.perform(get("/messages/{id}", Mockito.anyLong())).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("content")).andDo(print()).andReturn();
    }

    @Test
    void fetch_error() throws Exception {
        given(this.messageService.fetch(Mockito.anyLong())).willThrow(new RuntimeException());

        mvc.perform(get("/messages/{id}", Mockito.anyLong())).andExpect(status().isNoContent())
                .andDo(print()).andReturn();
    }

    @Test
    void create() throws Exception {
        given(this.messageService.create(Mockito.any(MessageDTO.class))).willReturn(vo);

        mvc.perform(post("/messages").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("test"))
                .andDo(print()).andReturn();
    }

    @Test
    void create_error() throws Exception {
        given(this.messageService.create(Mockito.any(MessageDTO.class))).willThrow(new RuntimeException());

        mvc.perform(post("/messages").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)).with(csrf().asHeader()))
                .andExpect(status().isExpectationFailed())
                .andDo(print()).andReturn();
    }
}