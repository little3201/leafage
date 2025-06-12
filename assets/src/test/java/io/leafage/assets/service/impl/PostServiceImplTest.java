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

import io.leafage.assets.domain.Post;
import io.leafage.assets.domain.PostContent;
import io.leafage.assets.dto.PostDTO;
import io.leafage.assets.repository.PostContentRepository;
import io.leafage.assets.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.mockito.BDDMockito.given;

/**
 * post service test
 *
 * @author wq li
 */
@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostContentRepository postContentRepository;

    @InjectMocks
    private PostServiceImpl postsService;

    private PostDTO dto;

    @BeforeEach
    void setUp() {
        dto = new PostDTO();
        dto.setTitle("标题");
        dto.setTags(Set.of("test"));
        dto.setSummary("内容信息");
    }

    @Test
    void retrieve() {
        given(this.postRepository.findAllBy(Mockito.any(PageRequest.class))).willReturn(Flux.just(Mockito.mock(Post.class)));

        given(this.postRepository.count()).willReturn(Mono.just(2L));

        StepVerifier.create(this.postsService.retrieve(0, 2, "id", true, "title:like:a")).expectNextCount(1).verifyComplete();
    }

    @Test
    void fetch() {
        given(this.postRepository.findById(Mockito.anyLong())).willReturn(Mono.just(Mockito.mock(Post.class)));

        given(this.postContentRepository.getByPostId(Mockito.anyLong())).willReturn(Mono.just(Mockito.mock(PostContent.class)));

        StepVerifier.create(this.postsService.fetch(Mockito.anyLong())).expectNextCount(1).verifyComplete();
    }

    @Test
    void exists() {
        given(this.postRepository.existsByTitleAndIdNot(Mockito.anyString(), Mockito.anyLong())).willReturn(Mono.just(Boolean.TRUE));

        StepVerifier.create(postsService.exists("test", 1L)).expectNext(Boolean.TRUE).verifyComplete();
    }

    @Test
    void exists_id_null() {
        given(this.postRepository.existsByTitle(Mockito.anyString())).willReturn(Mono.just(Boolean.TRUE));

        StepVerifier.create(postsService.exists("test", null)).expectNext(Boolean.TRUE).verifyComplete();
    }

    @Test
    void create() {
        given(this.postRepository.save(Mockito.any(Post.class))).willReturn(Mono.just(Mockito.mock(Post.class)));

        given(this.postContentRepository.save(Mockito.any(PostContent.class))).willReturn(Mono.empty());

        StepVerifier.create(this.postsService.create(Mockito.mock(PostDTO.class))).verifyComplete();
    }

    @Test
    void modify() {
        given(this.postRepository.findById(Mockito.anyLong())).willReturn(Mono.just(Mockito.mock(Post.class)));

        given(this.postRepository.save(Mockito.any(Post.class))).willReturn(Mono.just(Mockito.mock(Post.class)));

        given(this.postContentRepository.getByPostId(Mockito.anyLong())).willReturn(Mono.just(Mockito.mock(PostContent.class)));

        given(this.postContentRepository.save(Mockito.any(PostContent.class))).willReturn(Mono.empty());

        StepVerifier.create(this.postsService.modify(1L, dto)).verifyComplete();
    }

    @Test
    void remove() {
        given(this.postContentRepository.getByPostId(Mockito.anyLong())).willReturn(Mono.just(Mockito.mock(PostContent.class)));

        given(this.postContentRepository.deleteById(Mockito.anyLong())).willReturn(Mono.empty());

        given(this.postRepository.deleteById(Mockito.anyLong())).willReturn(Mono.empty());

        StepVerifier.create(postsService.remove(Mockito.anyLong())).verifyComplete();
    }

    @Test
    void search() {
        given(this.postRepository.findAllByTitle(Mockito.anyString())).willReturn(Flux.just(Mockito.mock(Post.class)));

        StepVerifier.create(postsService.search("test")).expectNextCount(1).verifyComplete();
    }

}