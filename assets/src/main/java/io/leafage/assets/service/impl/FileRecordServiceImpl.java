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

package io.leafage.assets.service.impl;

import io.leafage.assets.domain.FileRecord;
import io.leafage.assets.dto.FileRecordDTO;
import io.leafage.assets.repository.FileRecordRepository;
import io.leafage.assets.service.FileRecordService;
import io.leafage.assets.vo.FileRecordVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import top.leafage.common.DomainConverter;

/**
 * file service impl
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Page<FileRecordVO>> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        return fileRecordRepository.findAllBy(pageable)
                .map(f -> convertToVO(f, FileRecordVO.class))
                .collectList()
                .zipWith(fileRecordRepository.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<FileRecordVO> fetch(Long id) {
        Assert.notNull(id, "id must not be null.");

        return fileRecordRepository.findById(id)
                .map(f -> convertToVO(f, FileRecordVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Boolean> exists(String name, Long id) {
        Assert.hasText(name, "name must not be empty.");
        if (id == null) {
            return fileRecordRepository.existsByName(name);
        }
        return fileRecordRepository.existsByNameAndIdNot(name, id);
    }

    @Override
    public Mono<FileRecordVO> create(FileRecordDTO dto) {
        return fileRecordRepository.save(convertToDomain(dto, FileRecord.class))
                .map(f -> convertToVO(f, FileRecordVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> remove(Long id) {
        Assert.notNull(id, "id must not be null.");
        return fileRecordRepository.deleteById(id);
    }

}
