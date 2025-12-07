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

package top.leafage.hypervisor.service.impl;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.domain.Message;
import top.leafage.hypervisor.domain.dto.MessageDTO;
import top.leafage.hypervisor.domain.vo.MessageVO;
import top.leafage.hypervisor.repository.MessageRepository;
import top.leafage.hypervisor.service.MessageService;

import java.util.NoSuchElementException;

/**
 * message service impl
 *
 * @author wq li
 */
@Service
public class MessageServiceImpl implements MessageService {

    private static final BeanCopier copier = BeanCopier.create(MessageDTO.class, Message.class, false);
    private final MessageRepository messageRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    /**
     * <p>Constructor for MessageServiceImpl.</p>
     *
     * @param messageRepository a {@link MessageRepository} object
     */
    public MessageServiceImpl(MessageRepository messageRepository, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.messageRepository = messageRepository;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Page<MessageVO>> retrieve(int page, int size, String sortBy, boolean descending, String filters) {
        Pageable pageable = pageable(page, size, sortBy, descending);
        Criteria criteria = buildCriteria(filters, Message.class);

        return r2dbcEntityTemplate.select(Message.class)
                .matching(Query.query(criteria).with(pageable))
                .all()
                .map(MessageVO::from)
                .collectList()
                .zipWith(r2dbcEntityTemplate.count(Query.query(criteria), Message.class))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<MessageVO> fetch(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return messageRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .doOnNext(message -> message.setUnread(Boolean.TRUE))
                .flatMap(messageRepository::save)
                .map(MessageVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<MessageVO> create(MessageDTO dto) {
        return messageRepository.existsByTitle(dto.getTitle())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("title already exists: " + dto.getTitle()));
                    }
                    return messageRepository.save(MessageDTO.toEntity(dto))
                            .map(MessageVO::from);
                });
    }

    @Override
    public Mono<MessageVO> modify(Long id, MessageDTO dto) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        return messageRepository.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .flatMap(existing -> {
                    if (!existing.getTitle().equals(dto.getTitle())) {
                        return Mono.just(existing);
                    }

                    return messageRepository.existsByTitle(dto.getTitle())
                            .flatMap(exists -> {
                                if (exists) {
                                    return Mono.error(new IllegalArgumentException("title already exists: " + dto.getTitle()));
                                }
                                return Mono.just(existing);
                            });
                })
                .flatMap(existing -> {
                    copier.copy(dto, existing, null);
                    return messageRepository.save(existing);
                })
                .map(MessageVO::from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> remove(Long id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        return messageRepository.deleteById(id);
    }

}
