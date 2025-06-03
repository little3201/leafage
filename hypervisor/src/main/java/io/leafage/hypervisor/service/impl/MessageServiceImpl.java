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

package io.leafage.hypervisor.service.impl;

import io.leafage.hypervisor.domain.Message;
import io.leafage.hypervisor.dto.MessageDTO;
import io.leafage.hypervisor.repository.MessageRepository;
import io.leafage.hypervisor.service.MessageService;
import io.leafage.hypervisor.vo.MessageVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

/**
 * message service impl
 *
 * @author wq li
 */
@Service
public class MessageServiceImpl implements MessageService {

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
    public Mono<Page<MessageVO>> retrieve(int page, int size, String sortBy, boolean descending, String receiver) {
        Pageable pageable = pageable(page, size, sortBy, descending);

        return messageRepository.findByReceiver(receiver, pageable)
                .map(m -> convertToVO(m, MessageVO.class))
                .collectList()
                .zipWith(messageRepository.countByReceiver(receiver))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<MessageVO> fetch(Long id) {
        Assert.notNull(id, "id must not be null.");

        return messageRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .doOnNext(message -> message.setUnread(Boolean.TRUE))
                .flatMap(messageRepository::save)
                .map(m -> convertToVO(m, MessageVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<MessageVO> create(MessageDTO dto) {
        return messageRepository.save(convertToDomain(dto, Message.class))
                .map(m -> convertToVO(m, MessageVO.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> remove(Long id) {
        Assert.notNull(id, "id must not be null.");
        return messageRepository.deleteById(id);
    }

}
