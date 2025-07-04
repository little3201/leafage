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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import top.leafage.common.DomainConverter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * file service impl.
 *
 * @author wq li
 */
@Service
public class FileRecordServiceImpl extends DomainConverter implements FileRecordService {

    private static final Logger logger = LoggerFactory.getLogger(FileRecordServiceImpl.class);

    private final FileRecordRepository fileRecordRepository;

    public FileRecordServiceImpl(FileRecordRepository fileRecordRepository) {
        this.fileRecordRepository = fileRecordRepository;
    }

    @Override
    public Page<FileRecordVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<FileRecord> spec = (root, query, cb) ->
                buildPredicate(filters, cb, root).orElse(null);

        return fileRecordRepository.findAll(spec, pageable)
                .map(fileRecord -> convertToVO(fileRecord, FileRecordVO.class));
    }

    @Override
    public FileRecordVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return fileRecordRepository.findById(id)
                .map(fileRecord -> convertToVO(fileRecord, FileRecordVO.class))
                .orElse(null);
    }

    @Override
    public boolean exists(String name, Long id) {
        Assert.hasText(name, NAME_MUST_NOT_BE_EMPTY);
        if (id == null) {
            return fileRecordRepository.existsByName(name);
        }
        return fileRecordRepository.existsByNameAndIdNot(name, id);
    }

    @Override
    public FileRecordVO upload(MultipartFile file) {
        FileRecord fileRecord = new FileRecord();
        fileRecord.setName(file.getName());
        fileRecord.setMimeType(file.getContentType());
        fileRecord.setSize(file.getSize());
        fileRecord = fileRecordRepository.saveAndFlush(fileRecord);
        return convertToVO(fileRecord, FileRecordVO.class);
    }

    @Override
    public String download(Long id, OutputStream outputStream) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

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
