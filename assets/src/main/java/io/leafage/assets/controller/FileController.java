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
import io.leafage.assets.service.FileStorageService;
import io.leafage.assets.vo.FileRecordVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private final FileStorageService fileStorageService;

    /**
     * <p>Constructor for RegionController.</p>
     * <p>
     * //     * @param regionService a {@link FileRecordService} object
     */
    public FileController(FileRecordService fileRecordService, FileStorageService fileStorageService) {
        this.fileRecordService = fileRecordService;
        this.fileStorageService = fileStorageService;
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
    public Mono<Page<FileRecordVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                             String sortBy, boolean descending, String filters) {
        return fileRecordService.retrieve(page, size, sortBy, descending, filters)
                .doOnError(e -> logger.error("Retrieve file records error: ", e));
    }

    /**
     * 根据 id 查询
     *
     * @param id 主键 ID
     * @return 查询的数据
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_files')")
    @GetMapping("/{id}")
    public Mono<FileRecordVO> fetch(@PathVariable Long id) {
        return fileRecordService.fetch(id)
                .doOnError(e -> logger.error("Fetch file record error: ", e));
    }

    /**
     * 添加信息
     *
     * @param file 要添加的数据
     * @return 如果添加数据成功，返回添加后的信息，否则返回417状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_files:upload')")
    @PostMapping
    public Mono<FileRecordVO> upload(FilePart file) {
        return fileRecordService.exists(file.filename(), null)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new FileAlreadyExistsException("File already exists: " + file.filename()));
                    } else {
                        return fileStorageService.upload(file)
                                .flatMap(fileRecordService::create);
                    }
                })
                .doOnSuccess(vo -> logger.debug("File uploaded successfully: {}", file.filename()))
                .doOnError(e -> logger.error("Upload file error: {}", file.filename(), e));
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
        return fileRecordService.fetch(id)
                .switchIfEmpty(Mono.error(new FileNotFoundException("File not found.")))
                .flatMap(vo -> {
                    Resource resource = new PathResource(vo.getPath());
                    String fileName = URLEncoder.encode(vo.getName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
                    response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
                    response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=" + fileName + ";filename*=UTF_8''" + fileName);

                    Flux<DataBuffer> content = DataBufferUtils.readInputStream(resource::getInputStream, new DefaultDataBufferFactory(), 4096);
                    return response.writeWith(content);
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
    public Mono<Void> remove(@PathVariable Long id) {
        return fileRecordService.remove(id)
                .doOnEach(e -> logger.error("Remove file error: {}", e));
    }
}
