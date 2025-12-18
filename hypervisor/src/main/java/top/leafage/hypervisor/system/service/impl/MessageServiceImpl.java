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

package top.leafage.hypervisor.system.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import top.leafage.hypervisor.system.domain.Message;
import top.leafage.hypervisor.system.domain.dto.MessageDTO;
import top.leafage.hypervisor.system.domain.vo.MessageVO;
import top.leafage.hypervisor.system.repository.MessageRepository;
import top.leafage.hypervisor.system.service.MessageService;

/**
 * message service impl.
 *
 * @author wq li
 */
@Service
public class MessageServiceImpl implements MessageService {

    private static final BeanCopier copier = BeanCopier.create(MessageDTO.class, Message.class, false);
    private final MessageRepository messageRepository;

    /**
     * Constructor for MessageServiceImpl.
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
    public Page<@NonNull MessageVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        Specification<@NonNull Message> spec = (root, query, cb) ->
                buildPredicate(filters, cb, root).orElse(null);

        return messageRepository.findAll(spec, pageable)
                .map(MessageVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return messageRepository.findById(id)
                .map(MessageVO::from)
                .orElseThrow(() -> new EntityNotFoundException("message not found: " + id));
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public MessageVO create(MessageDTO dto) {
        if (messageRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("title already exists: " + dto.getTitle());
        }
        Message entity = messageRepository.saveAndFlush(MessageDTO.toEntity(dto));
        return MessageVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public MessageVO modify(Long id, MessageDTO dto) {
        Message existing = messageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("message not found: " + id));
        if (!existing.getTitle().equals(dto.getTitle()) &&
                messageRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("title already exists: " + dto.getTitle());
        }
        copier.copy(dto, existing, null);
        Message entity = messageRepository.save(existing);
        return MessageVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        if (!messageRepository.existsById(id)) {
            throw new EntityNotFoundException("message not found: " + id);
        }
        messageRepository.deleteById(id);
    }
}
