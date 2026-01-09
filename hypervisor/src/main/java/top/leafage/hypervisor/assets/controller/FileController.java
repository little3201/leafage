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

package top.leafage.hypervisor.assets.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.assets.domain.vo.FileRecordVO;
import top.leafage.hypervisor.assets.service.FileRecordService;

import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * file controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/files")
public class FileController {

    private final FileRecordService fileRecordService;

    /**
     * Constructor for RegionController.
     *
     * @param fileRecordService a {@link FileRecordService} object
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
    public Mono<Page<FileRecordVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                             String sortBy, boolean descending, String filters) {
        return fileRecordService.retrieve(page, size, sortBy, descending, filters);
    }

    /**
     * 根据 id 查询
     *
     * @param id the pk. ID
     * @return 查询的数据
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_files')")
    @GetMapping("/{id}")
    public Mono<FileRecordVO> fetch(@PathVariable Long id) {
        return fileRecordService.fetch(id);
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
        return fileRecordService.upload(file);
    }

    /**
     * 根据 id 查询
     *
     * @param id the pk. ID
     * @return 查询的数据
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_files:download')")
    @GetMapping("/{id}/download")
    public Mono<ServerResponse> download(@PathVariable Long id) {
        return fileRecordService.fetch(id)
                .switchIfEmpty(Mono.error(new FileNotFoundException("File not found.")))
                .flatMap(vo -> {
                    Path filePath = Paths.get(vo.path());
                    String encodedName = URLEncoder.encode(vo.name(), StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20");

                    Resource resource = new FileSystemResource(filePath);
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .header(HttpHeaders.CONTENT_DISPOSITION,
                                    "attachment; filename=\"" + encodedName + "\"; " +
                                            "filename*=UTF-8''" + encodedName)
                            .body(BodyInserters.fromResource(resource));
                })
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    /**
     * 删除信息
     *
     * @param id the pk.
     * @return 如果删除成功，返回200状态码，否则返回417状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_files:remove')")
    @DeleteMapping("/{id}")
    public Mono<Void> remove(@PathVariable Long id) {
        return fileRecordService.remove(id);
    }
}
