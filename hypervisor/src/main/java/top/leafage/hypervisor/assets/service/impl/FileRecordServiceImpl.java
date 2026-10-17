/*
 *
 *  * Copyright(c) 2019-present the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *       https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package top.leafage.hypervisor.assets.service.impl;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import top.leafage.hypervisor.assets.domain.FileRecord;
import top.leafage.hypervisor.assets.domain.vo.FileRecordVO;
import top.leafage.hypervisor.assets.repository.FileRecordRepository;
import top.leafage.hypervisor.assets.service.FileRecordService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static org.springframework.data.relational.core.query.Query.query;

/**
 * file service impl.
 *
 * @author wq li
 */
@Service
public class FileRecordServiceImpl implements FileRecordService {

    private static final Logger logger = LoggerFactory.getLogger(FileRecordServiceImpl.class);

    private final FileRecordRepository fileRecordRepository;
    private final JdbcAggregateTemplate jdbcAggregateTemplate;

    public FileRecordServiceImpl(FileRecordRepository fileRecordRepository, JdbcAggregateTemplate jdbcAggregateTemplate) {
        this.fileRecordRepository = fileRecordRepository;
        this.jdbcAggregateTemplate = jdbcAggregateTemplate;
    }

    @Override
    public Page<@NonNull FileRecordVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        Criteria criteria = buildCriteria(filters, FileRecord.class);
        if (!StringUtils.hasText(filters) || !filters.contains("superiorId")) {
            criteria = criteria.and("superiorId").isNull();
        }

        List<FileRecordVO> voList = jdbcAggregateTemplate.findAll(query(criteria).with(pageable), FileRecord.class)
                .stream().map(FileRecordVO::from)
                .toList();
        return new PageImpl<>(voList, pageable, jdbcAggregateTemplate.count(query(criteria), FileRecord.class));
    }

    @Override
    public FileRecordVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return fileRecordRepository.findById(id)
                .map(FileRecordVO::from)
                .orElseThrow(() -> new NoSuchElementException("file record not found: " + id));
    }

    @Transactional
    @Override
    public FileRecordVO upload(MultipartFile file, Long superiorId) {
        FileRecord record = new FileRecord();
        record.setSuperiorId(superiorId);
        record.setName(file.getName());
        // get extension
        if (file.getOriginalFilename() != null) {
            String originalFilename = file.getOriginalFilename();
            int lastDot = originalFilename.lastIndexOf('.');
            if (lastDot > 0) {
                record.setExtension(originalFilename.substring(lastDot + 1));
            }
        }
        record.setPath("");
        record.setContentType(Objects.requireNonNull(file.getContentType()));
        record.setSize(file.getSize());
        record.setDirectory(false);
        record.setRegularFile(true);
        record.setSymbolicLink(false);
        FileRecord entity = fileRecordRepository.save(record);
        return FileRecordVO.from(entity);
    }

    @Transactional
    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        if (!fileRecordRepository.existsById(id)) {
            throw new NoSuchElementException("file record not found: " + id);
        }
        fileRecordRepository.deleteById(id);
    }
}
