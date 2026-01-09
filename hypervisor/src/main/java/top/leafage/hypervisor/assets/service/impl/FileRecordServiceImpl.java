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

package top.leafage.hypervisor.assets.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.assets.domain.FileRecord;
import top.leafage.hypervisor.assets.domain.vo.FileRecordVO;
import top.leafage.hypervisor.assets.repository.FileRecordRepository;
import top.leafage.hypervisor.assets.service.FileRecordService;

import java.util.Objects;


/**
 * file service impl
 *
 * @author wq li
 */
@Service
public class FileRecordServiceImpl implements FileRecordService {

    private final FileRecordRepository fileRecordRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    public FileRecordServiceImpl(FileRecordRepository fileRecordRepository, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.fileRecordRepository = fileRecordRepository;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Page<FileRecordVO>> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        Criteria criteria = buildCriteria(filters, FileRecord.class);
        if (!StringUtils.hasText(filters) || !filters.contains("superiorId")) {
            criteria = criteria.and("superiorId").isNull();
        }
        return r2dbcEntityTemplate.select(FileRecord.class)
                .matching(Query.query(criteria).with(pageable))
                .all()
                .map(FileRecordVO::from)
                .collectList()
                .zipWith(r2dbcEntityTemplate.count(Query.query(criteria), FileRecord.class))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<FileRecordVO> fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return fileRecordRepository.findById(id)
                .map(FileRecordVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        return fileRecordRepository.deleteById(id);
    }

    @Override
    public Mono<FileRecordVO> upload(FilePart file) {
        FileRecord record = new FileRecord();

        String filename = file.filename();
        record.setName(filename);
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0) {
            record.setExtension(filename.substring(lastDot + 1));
        }
        record.setPath("");
        record.setContentType(Objects.requireNonNull(file.headers().getContentType()).getType());
        record.setSize(file.headers().size());
        record.setDirectory(false);
        record.setRegularFile(true);
        record.setSymbolicLink(false);
        return fileRecordRepository.save(record)
                .map(FileRecordVO::from);
    }
}
