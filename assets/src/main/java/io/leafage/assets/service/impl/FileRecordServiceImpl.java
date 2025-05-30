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

package io.leafage.assets.service.impl;

import io.leafage.assets.domain.FileRecord;
import io.leafage.assets.repository.FileRecordRepository;
import io.leafage.assets.service.FileRecordService;
import io.leafage.assets.vo.FileRecordVO;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * file service impl.
 *
 * @author wq li
 */
@Service
public class FileRecordServiceImpl implements FileRecordService {

    private static final Logger logger = LoggerFactory.getLogger(FileRecordServiceImpl.class);

    private final FileRecordRepository fileRecordRepository;

    public FileRecordServiceImpl(FileRecordRepository fileRecordRepository) {
        this.fileRecordRepository = fileRecordRepository;
    }

    @Override
    public Page<FileRecordVO> retrieve(int page, int size, String sortBy, boolean descending, String name) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<FileRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return fileRecordRepository.findAll(spec, pageable)
                .map(fileRecord -> convertToVO(fileRecord, FileRecordVO.class));
    }

    @Override
    public boolean exists(String name, Long id) {
        Assert.hasText(name, "name must not be empty.");

        return fileRecordRepository.existsByName(name);
    }

    @Override
    public FileRecordVO upload(MultipartFile file) {
        FileRecord fileRecord = new FileRecord();
        fileRecord.setName(file.getName());
        fileRecord.setType(file.getContentType());
        fileRecord.setSize(file.getSize());
        fileRecord = fileRecordRepository.save(fileRecord);
        return convertToVO(fileRecord, FileRecordVO.class);
    }

    @Override
    public String download(Long id, OutputStream outputStream) {
        Assert.notNull(id, "id must not be null.");

        return fileRecordRepository.findById(id).map(fileRecord -> {
            File file = new File(fileRecord.getPath());
            try {
                Files.copy(file.toPath(), outputStream);
                outputStream.close();
            } catch (IOException e) {
                logger.error("Failed to process template: {}", fileRecord.getName(), e);
            }
            return fileRecord.getName();
        }).orElseThrow(() -> new RuntimeException("File not found"));
    }

    @Override
    public void remove(Long id) {
        fileRecordRepository.deleteById(id);
    }
}
