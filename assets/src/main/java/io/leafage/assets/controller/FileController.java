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

import io.leafage.assets.service.FileRecordService;
import io.leafage.assets.vo.FileRecordVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;

/**
 * file controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/files")
public class FileController {


    private final Logger logger = LoggerFactory.getLogger(FileController.class);

    private final FileRecordService fileRecordService;

    /**
     * <p>Constructor for RegionController.</p>
     * <p>
     * //     * @param regionService a {@link FileRecordService} object
     */
    public FileController(FileRecordService fileRecordService) {
        this.fileRecordService = fileRecordService;
    }

    /**
     * 分页查询
     *
     * @param page       页码
     * @param size       大小
     * @param sortBy     排序字段
     * @param descending 排序方向
     * @return 查询的数据集
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_files')")
    @GetMapping
    public Mono<ResponseEntity<Page<FileRecordVO>>> retrieve(@RequestParam int page, @RequestParam int size,
                                                             String sortBy, boolean descending, String filters) {
        return fileRecordService.retrieve(page, size, sortBy, descending, filters)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.error("Retrieve file records error: ", e);
                    return Mono.just(ResponseEntity.noContent().build());
                });
    }

    /**
     * 根据 id 查询
     *
     * @param id 主键 ID
     * @return 查询的数据
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_files')")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<FileRecordVO>> fetch(@PathVariable Long id) {
        return fileRecordService.fetch(id)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    logger.error("Fetch file record error: ", e);
                    return Mono.just(ResponseEntity.noContent().build());
                });
    }

    /**
     * 添加信息
     *
     * @param file 要添加的数据
     * @return 如果添加数据成功，返回添加后的信息，否则返回417状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_files:upload')")
    @PostMapping
    public Mono<ResponseEntity<FileRecordVO>> upload(MultipartFile file) {
        return fileRecordService.exists(file.getOriginalFilename(), null)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new FileAlreadyExistsException("File already exists."));
                    }
                    return fileRecordService.upload(file);
                })
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved))
                .onErrorResume(FileAlreadyExistsException.class, e -> {
                    logger.warn("Upload failed: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build());
                })
                .onErrorResume(e -> {
                    logger.error("Upload file error", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build());
                });
    }

    /**
     * 根据 id 查询
     *
     * @param id 主键 ID
     * @return 查询的数据
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_files:download')")
    @GetMapping("/{id}/download")
    public Mono<Void> download(@PathVariable Long id, ServerHttpResponse response) {
        return fileRecordService.download(id)
                .flatMap(fileData -> {
                    response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
                    response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileData.fileName() + "\"");
                    return response.writeWith(fileData.content());
                })
                .onErrorResume(FileNotFoundException.class, e -> {
                    response.setStatusCode(HttpStatus.NOT_FOUND);
                    return response.setComplete();
                })
                .onErrorResume(e -> {
                    response.setStatusCode(HttpStatus.EXPECTATION_FAILED);
                    return response.setComplete();
                });
    }

    /**
     * 删除信息
     *
     * @param id 主键
     * @return 如果删除成功，返回200状态码，否则返回417状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_files:remove')")
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> remove(@PathVariable Long id) {
        return fileRecordService.remove(id)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorResume(e -> {
                    logger.error("Remove file error: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build());
                });
    }
}
