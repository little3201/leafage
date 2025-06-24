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

package io.leafage.system.service.impl;

import io.leafage.system.domain.Message;
import io.leafage.system.dto.MessageDTO;
import io.leafage.system.repository.MessageRepository;
import io.leafage.system.service.MessageService;
import io.leafage.system.vo.MessageVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import top.leafage.common.DomainConverter;

/**
 * message common impl.
 *
 * @author wq li
 */
@Service
public class MessageServiceImpl extends DomainConverter implements MessageService {

    private final MessageRepository messageRepository;

    /**
     * <p>Constructor for MessageServiceImpl.</p>
     *
     * @param messageRepository a {@link MessageRepository} object
     */
    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<MessageVO> retrieve(int page, int size, String sortBy, boolean descending, String title) {
        Sort sort = Sort.by(descending ? Sort.Direction.DESC : Sort.Direction.ASC,
                StringUtils.hasText(sortBy) ? sortBy : "id");
        Pageable pageable = PageRequest.of(page, size, sort);

        return messageRepository.findAll(pageable)
                .map(message -> convertToVO(message, MessageVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageVO fetch(Long id) {
        Assert.notNull(id, "id must not be null.");

        return messageRepository.findById(id)
                .map(message -> convertToVO(message, MessageVO.class)).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageVO create(MessageDTO dto) {
        Message message = convertToDomain(dto, Message.class);

        messageRepository.save(message);
        return convertToVO(message, MessageVO.class);
    }

}
