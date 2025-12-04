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

package top.leafage.assets.controller;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import top.leafage.assets.domain.vo.FileRecordVO;
import top.leafage.assets.service.FileRecordService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

/**
 * tag controller test
 *
 * @author wq li
 */
@WithMockUser
@WebFluxTest(FileController.class)
class FileControllerTest {

    @MockitoBean
    private FileRecordService fileRecordService;

    @Autowired
    private WebTestClient webTestClient;

    private FileRecordVO vo;

    @BeforeEach
    void setUp() {
        vo = new FileRecordVO(1L, "test", ".txt", "src/test/resources/test.txt", "text/plain", 312123, false, true, false, LocalDateTime.now());
    }

    @Test
    void retrieve() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<@NonNull FileRecordVO> page = new PageImpl<>(List.of(vo), pageable, 1L);
        given(this.fileRecordService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willReturn(Mono.just(page));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/files")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                        .queryParam("filters", "")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FileRecordVO.class);
    }

    @Test
    void retrieve_error() {
        given(this.fileRecordService.retrieve(anyInt(), anyInt(), anyString(),
                anyBoolean(), anyString())).willThrow(new RuntimeException());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/files")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "id")
                        .queryParam("descending", "false")
                        .queryParam("filters", "name:like:test")
                        .build())
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void fetch() {
        given(this.fileRecordService.fetch(anyLong())).willReturn(Mono.just(vo));

        webTestClient.get().uri("/files/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("test");
    }

    @Test
    void fetch_error() {
        given(this.fileRecordService.fetch(anyLong())).willThrow(new RuntimeException());

        webTestClient.get().uri("/files/{id}", 1L)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void upload() {
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "test".getBytes());

        given(this.fileRecordService.upload(any(FilePart.class))).willReturn(Mono.just(vo));

        webTestClient.mutateWith(csrf()).post().uri("/files")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", file.getResource()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(FileRecordVO.class);
    }

    @Test
    void upload_error() {
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);

        given(this.fileRecordService.upload(any(FilePart.class))).willThrow(new NoSuchElementException());

        webTestClient.mutateWith(csrf()).post().uri("/files")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", file.getResource()))
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void download() throws IOException {
        given(this.fileRecordService.fetch(anyLong())).willReturn(Mono.just(vo));

        webTestClient.get().uri("/files/{id}/download", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_OCTET_STREAM);
    }

    @Test
    void download_file_not_found() {
        given(this.fileRecordService.fetch(anyLong())).willReturn(Mono.empty());

        webTestClient.get().uri("/files/{id}/download", 1L)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void remove() {
        given(this.fileRecordService.remove(anyLong())).willReturn(Mono.empty());

        webTestClient.mutateWith(csrf()).delete().uri("/files/{id}", 1L)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void remove_error() {
        given(this.fileRecordService.remove(anyLong())).willThrow(new RuntimeException());

        webTestClient.delete().uri("/files/{id}", 1L)
                .exchange()
                .expectStatus().is4xxClientError();
    }
}