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

package io.leafage.assets.controller;

import io.leafage.assets.dto.FileDataDTO;
import io.leafage.assets.dto.FileRecordDTO;
import io.leafage.assets.service.FileRecordService;
import io.leafage.assets.vo.FileRecordVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

/**
 * tag controller test
 *
 * @author wq li
 */
@WithMockUser
@ExtendWith(SpringExtension.class)
@WebFluxTest(FileController.class)
class FileControllerTest {

    @MockitoBean
    private FileRecordService fileRecordService;

    @Autowired
    private WebTestClient webTestClient;

    private FileRecordDTO dto;
    private FileRecordVO vo;

    @BeforeEach
    void setUp() {
        // 构造请求对象
        dto = new FileRecordDTO();
        dto.setName("test");

        vo = new FileRecordVO();
        vo.setId(1L);
        vo.setName(dto.getName());
    }

    @Test
    void retrieve() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<FileRecordVO> page = new PageImpl<>(List.of(vo), pageable, 1L);
        given(this.fileRecordService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyBoolean(), Mockito.anyString())).willReturn(Mono.just(page));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/files")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FileRecordVO.class);
    }

    @Test
    void retrieve_error() {
        given(this.fileRecordService.retrieve(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyBoolean(), Mockito.anyString())).willThrow(new RuntimeException());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/files")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                        .queryParam("filters", "name:like:a")
                        .build())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void fetch() {
        given(this.fileRecordService.fetch(Mockito.anyLong())).willReturn(Mono.just(vo));

        webTestClient.get().uri("/files/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void fetch_error() {
        given(this.fileRecordService.fetch(Mockito.anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/files/{id}", 1)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void upload() {
        byte[] content = "Sample file".getBytes(StandardCharsets.UTF_8);

        given(this.fileRecordService.exists(Mockito.anyString(), Mockito.anyLong())).willReturn(Mono.just(false));
        given(this.fileRecordService.upload(Mockito.any())).willReturn(Mono.just(vo));

        webTestClient.mutateWith(csrf()).post().uri("/files")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("text.txt", ""))
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void upload_error() {
        byte[] content = "Sample file".getBytes(StandardCharsets.UTF_8);

        given(this.fileRecordService.exists(Mockito.anyString(), Mockito.anyLong())).willReturn(Mono.just(false));
        given(this.fileRecordService.upload(Mockito.any())).willThrow(new RuntimeException());

        webTestClient.mutateWith(csrf()).post().uri("/files")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("text.txt", ""))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void download() {
        given(this.fileRecordService.download(Mockito.anyLong())).willReturn(Mono.just(Mockito.mock(FileDataDTO.class)));

        webTestClient.mutateWith(csrf()).get().uri("/files/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Disposition", "attachment; filename=\"test.txt\"")
                .expectHeader().contentType(MediaType.APPLICATION_OCTET_STREAM);
    }

    @Test
    void remove() {
        given(this.fileRecordService.remove(Mockito.anyLong())).willReturn(Mono.empty());

        webTestClient.mutateWith(csrf()).delete().uri("/files/{id}", 1)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void remove_error() {
        given(this.fileRecordService.remove(Mockito.anyLong())).willThrow(new RuntimeException());

        webTestClient.delete().uri("/files/{id}", 1)
                .exchange()
                .expectStatus().is4xxClientError();
    }
}