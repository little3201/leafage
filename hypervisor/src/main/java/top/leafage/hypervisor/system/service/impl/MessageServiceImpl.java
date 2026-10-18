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

package top.leafage.hypervisor.system.service.impl;

import org.jspecify.annotations.NonNull;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import top.leafage.hypervisor.system.domain.Message;
import top.leafage.hypervisor.system.domain.dto.MessageDTO;
import top.leafage.hypervisor.system.domain.vo.MessageVO;
import top.leafage.hypervisor.system.repository.MessageRepository;
import top.leafage.hypervisor.system.service.MessageService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.data.relational.core.query.Query.query;
import static top.leafage.hypervisor.constants.GlobalConstant.ID_MUST_NOT_BE_NULL;

/**
 * message service impl.
 *
 * @author wq li
 */
@Service
public class MessageServiceImpl implements MessageService {

    private static final BeanCopier copier = BeanCopier.create(MessageDTO.class, Message.class, false);
    private final MessageRepository messageRepository;
    private final JdbcAggregateTemplate jdbcAggregateTemplate;

    /**
     * Constructor for MessageServiceImpl.
     *
     * @param messageRepository a {@link MessageRepository} object
     */
    public MessageServiceImpl(MessageRepository messageRepository, JdbcAggregateTemplate jdbcAggregateTemplate) {
        this.messageRepository = messageRepository;
        this.jdbcAggregateTemplate = jdbcAggregateTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<MessageVO> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        Criteria criteria = buildCriteria(filters, Message.class);

        List<MessageVO> voList = jdbcAggregateTemplate.findAll(query(criteria).with(pageable), Message.class)
                .stream().map(MessageVO::from)
                .toList();
        return new PageImpl<>(voList, pageable, jdbcAggregateTemplate.count(query(criteria), Message.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageVO fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return messageRepository.findById(id)
                .map(MessageVO::from)
                .orElseThrow(() -> new NoSuchElementException("message not found: " + id));
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
        Message entity = messageRepository.save(MessageDTO.toEntity(dto));
        return MessageVO.from(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public MessageVO modify(Long id, MessageDTO dto) {
        Message existing = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("message not found: " + id));
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
            throw new NoSuchElementException("message not found: " + id);
        }
        messageRepository.deleteById(id);
    }
}
