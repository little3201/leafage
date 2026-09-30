/*
 * Copyright(c) 2019-present the original author or authors.
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

package top.leafage.hypervisor.docs.controller;

import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.leafage.common.poi.excel.ExcelReader;
import top.leafage.hypervisor.docs.domain.dto.ReportDTO;
import top.leafage.hypervisor.docs.domain.vo.ReportVO;
import top.leafage.hypervisor.docs.service.ReportService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Report controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * 分页查询
     *
     * @param page       页码
     * @param size       分页数据大小，最大 500
     * @param sortBy     排序字段，若为 null，默认为主键 id
     * @param descending 排序方向，若为 false, 即正序排列
     * @param filters    过滤条件，格式：field:condition:value，如：name:like:test
     * @return 查询的数据集，异常时返回204状态码
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('reports')")
    @GetMapping
    public ResponseEntity<Page<ReportVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                   String sortBy, boolean descending, String filters) {
        Page<ReportVO> voPage = reportService.retrieve(page, size, sortBy, descending, filters);
        return ResponseEntity.ok(voPage);
    }

    /**
     * Fetch.
     *
     * @param id The pk.
     * @return the result.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('reports')")
    @GetMapping("/{id}")
    public ResponseEntity<ReportVO> fetch(@PathVariable Long id) {
        ReportVO vo = reportService.fetch(id);
        return ResponseEntity.ok(vo);
    }

    /**
     * create.
     *
     * @param dto the request body.
     * @return the result.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('reports:create')")
    @PostMapping
    public ResponseEntity<ReportVO> create(@Valid @RequestBody ReportDTO dto) {
        ReportVO vo = reportService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    /**
     * modify.
     *
     * @param id  The pk.
     * @param dto the request body.
     * @return the result.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('reports:modify')")
    @PutMapping("/{id}")
    public ResponseEntity<ReportVO> modify(@PathVariable Long id, @RequestBody ReportDTO dto) {
        ReportVO vo = reportService.modify(id, dto);
        return ResponseEntity.accepted().body(vo);
    }

    /**
     * Remove.
     *
     * @param id The pk.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('reports:remove')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        reportService.remove(id);
        return ResponseEntity.ok().build();
    }

    /**
     * import.
     *
     * @return the result.
     */
    @PreAuthorize("hasRole('USER') || hasAuthority('reports:import')")
    @PostMapping("/import")
    public ResponseEntity<List<ReportVO>> importFromFile(MultipartFile file) throws IOException {
        List<ReportDTO> dtoList = ExcelReader.read(file.getInputStream(), ReportDTO.class);
        List<ReportVO> voList = reportService.createAll(dtoList);
        return ResponseEntity.ok().body(voList);
    }

    /**
     * Generate to file.
     *
     * @param id The pk.
     * @return the result.
     */
    @PreAuthorize("hasRole('DEVELOP') || hasAuthority('reports:generate')")
    @GetMapping("/{id}/generate")
    public ResponseEntity<Resource> generate(@PathVariable Long id) {
        byte[] zipBytes = reportService.generate(id);
        ByteArrayResource resource = new ByteArrayResource(zipBytes);

        String fileName = String.format("report_%s.zip", id);
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(zipBytes.length))
                .body(resource);
    }

    /**
     * Preview a record.
     *
     * @param id The pk.
     * @return The list of records value objects, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasRole('DEVELOP') || hasAuthority('reports')")
    @GetMapping("/{id}/preview")
    public ResponseEntity<String> preview(@PathVariable Long id) {
        String content = reportService.preview(id);
        return ResponseEntity.ok().body(content);
    }
}
