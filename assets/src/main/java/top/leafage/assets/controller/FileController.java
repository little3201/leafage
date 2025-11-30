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

package top.leafage.assets.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.leafage.assets.domain.vo.FileRecordVO;
import top.leafage.assets.service.FileRecordService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * file controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/files")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    private final FileRecordService fileRecordService;

    /**
     * Constructor for FileController.
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
     * @return 查询的数据集，异常时返回204状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_files')")
    @GetMapping
    public ResponseEntity<Page<FileRecordVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                       String sortBy, boolean descending, String filters) {
        Page<FileRecordVO> voPage = fileRecordService.retrieve(page, size, sortBy, descending, filters);
        return ResponseEntity.ok(voPage);
    }

    /**
     * fetch.
     *
     * @param id th pk.
     * @return th result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_files')")
    @GetMapping("/{id}")
    public ResponseEntity<FileRecordVO> fetch(@PathVariable Long id) {
        FileRecordVO vo = fileRecordService.fetch(id);
        return ResponseEntity.ok(vo);
    }

    /**
     * create.
     *
     * @param file the request body.
     * @return the result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_files:upload')")
    @PostMapping
    public ResponseEntity<FileRecordVO> upload(MultipartFile file) {
        FileRecordVO vo = fileRecordService.upload(file);
        return ResponseEntity.ok(vo);
    }

    /**
     * fetch.
     *
     * @param id th pk.
     * @return th result.
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_files:download')")
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id, HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        FileRecordVO vo = fileRecordService.fetch(id);
        Resource resource = new FileSystemResource(vo.path());

        if (!resource.exists() || !resource.isReadable()) {
            logger.warn("resource not exists or not readable: path={}", vo.path());
            return ResponseEntity.notFound().build();
        }

        String fileName = vo.name();
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"; " +
                        "filename*=UTF-8''" + encodedFileName)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    /**
     * remove.
     *
     * @param id the pk.
     * @return 如果删除成功，返回200状态码，否则返回417状态码
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_files:remove')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        fileRecordService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
