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

package io.leafage.file.service.impl;


import io.leafage.file.mapper.FileRecordMapper;
import io.leafage.file.repository.FileRecordRepository;
import io.leafage.file.service.FileRecordService;
import io.leafage.file.vo.FileRecordVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.leafage.common.DomainConverter;

import java.io.File;

/**
 * file common impl.
 *
 * @author wq li
 */
@Service
public class FileRecordServiceImpl extends DomainConverter implements FileRecordService {

    private final FileRecordRepository fileRecordRepository;
    private final FileRecordMapper fileRecordMapper;

    private JdbcAggregateTemplate jdbcAggregateTemplate;

    public FileRecordServiceImpl(FileRecordRepository fileRecordRepository, FileRecordMapper fileRecordMapper) {
        this.fileRecordRepository = fileRecordRepository;
        this.fileRecordMapper = fileRecordMapper;
    }

    @Override
    public Page<FileRecordVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        return fileRecordMapper.findAll(filters, pageable)
                .map(fileRecord -> convertToVO(fileRecord, FileRecordVO.class));
    }

    @Override
    public FileRecordVO fetch(Long id) {
        return fileRecordRepository.findById(id)
                .map(fileRecord -> convertToVO(fileRecord, FileRecordVO.class)).orElse(null);
    }

    @Override
    public FileRecordVO upload(MultipartFile file) {
        return null;
    }

}
