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

package io.leafage.assets.service;

import io.leafage.assets.dto.PostDTO;
import io.leafage.assets.vo.PostVO;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.r2dbc.R2dbcCrudService;

/**
 * posts service
 *
 * @author wq li
 */
public interface PostService extends R2dbcCrudService<PostDTO, PostVO> {

    Mono<Page<PostVO>> retrieve(int page, int size, String sortBy, boolean descending);

    /**
     * 全文搜索
     *
     * @param keyword 关键字
     * @return 匹配结果
     */
    Flux<PostVO> search(String keyword);
}
