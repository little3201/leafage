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
import io.leafage.assets.domain.PostBody;
import io.leafage.assets.dto.PostDTO;
import io.leafage.assets.repository.PostBodyRepository;
import io.leafage.assets.repository.PostRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveSelectOperation;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

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
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    @Mock
    private PostBodyRepository postBodyRepository;

    @InjectMocks
    private PostServiceImpl postsService;

    private PostDTO dto;
    private Post entity;

    @BeforeEach
    void setUp() {
        dto = new PostDTO();
        dto.setTitle("标题");
        dto.setTags(Set.of("test"));
        dto.setSummary("内容信息");

        entity = new Post();
        entity.setTitle("标题");
        entity.setTags(Set.of("test"));
        entity.setSummary("内容信息");
    }

    @Test
    void retrieve() {
        ReactiveSelectOperation.ReactiveSelect<Post> select = mock(ReactiveSelectOperation.ReactiveSelect.class);
        ReactiveSelectOperation.TerminatingSelect<Post> terminating = mock(ReactiveSelectOperation.TerminatingSelect.class);

        given(r2dbcEntityTemplate.select(Post.class)).willReturn(select);
        given(select.matching(any(Query.class))).willReturn(terminating);
        given(terminating.all()).willReturn(Flux.just(entity));
        given(r2dbcEntityTemplate.count(any(Query.class), eq(Post.class))).willReturn(Mono.just(1L));

        StepVerifier.create(postsService.retrieve(0, 2, "id", true, "name:like:a"))
                .assertNext(page -> {
                    assertThat(page.getContent()).hasSize(1);
                    AssertionsForClassTypes.assertThat(page.getTotalElements()).isEqualTo(1);
                    AssertionsForClassTypes.assertThat(page.getNumber()).isEqualTo(0);
                    AssertionsForClassTypes.assertThat(page.getSize()).isEqualTo(2);
                }).verifyComplete();
    }

    @Test
    void fetch() {
        given(this.postRepository.findById(anyLong())).willReturn(Mono.just(mock(Post.class)));

        given(this.postBodyRepository.getByPostId(anyLong())).willReturn(Mono.just(mock(PostBody.class)));

        StepVerifier.create(this.postsService.fetch(anyLong())).expectNextCount(1).verifyComplete();
    }

    @Test
    void exists() {
        given(this.postRepository.existsByTitleAndIdNot(anyString(), anyLong())).willReturn(Mono.just(Boolean.TRUE));

        StepVerifier.create(postsService.exists("test", 1L)).expectNext(Boolean.TRUE).verifyComplete();
    }

    @Test
    void exists_id_null() {
        given(this.postRepository.existsByTitle(anyString())).willReturn(Mono.just(Boolean.TRUE));

        StepVerifier.create(postsService.exists("test", null)).expectNext(Boolean.TRUE).verifyComplete();
    }

    @Test
    void create() {
        given(this.postRepository.save(any(Post.class))).willReturn(Mono.just(mock(Post.class)));

        given(this.postBodyRepository.save(any(PostBody.class))).willReturn(Mono.empty());

        StepVerifier.create(this.postsService.create(mock(PostDTO.class))).verifyComplete();
    }

    @Test
    void modify() {
        given(this.postRepository.findById(anyLong())).willReturn(Mono.just(mock(Post.class)));

        given(this.postRepository.save(any(Post.class))).willReturn(Mono.just(mock(Post.class)));

        given(this.postBodyRepository.getByPostId(anyLong())).willReturn(Mono.just(mock(PostBody.class)));

        given(this.postBodyRepository.save(any(PostBody.class))).willReturn(Mono.empty());

        StepVerifier.create(this.postsService.modify(1L, dto)).verifyComplete();
    }

    @Test
    void remove() {
        given(this.postBodyRepository.getByPostId(anyLong())).willReturn(Mono.just(mock(PostBody.class)));

        given(this.postBodyRepository.deleteById(anyLong())).willReturn(Mono.empty());

        given(this.postRepository.deleteById(anyLong())).willReturn(Mono.empty());

        StepVerifier.create(postsService.remove(anyLong())).verifyComplete();
    }

    @Test
    void search() {
        given(this.postRepository.findAllByTitle(anyString())).willReturn(Flux.just(mock(Post.class)));

        StepVerifier.create(postsService.search("test")).expectNextCount(1).verifyComplete();
    }

}