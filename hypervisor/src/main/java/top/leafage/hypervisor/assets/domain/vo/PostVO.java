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
package top.leafage.hypervisor.assets.domain.vo;

import top.leafage.hypervisor.assets.domain.Post;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

/**
 * vo class for posts.
 *
 * @author wq li
 */
public record PostVO(
        Long id,
        String title,
        String summary,
        String body,
        Set<String> tags,
        LocalDateTime publishedAt
) {
    public static PostVO from(Post entity) {
        return new PostVO(
                entity.getId(),
                entity.getTitle(),
                entity.getSummary(),
                entity.getBody(),
                entity.getTags(),
                LocalDateTime.ofInstant(entity.getPublishedAt(), ZoneId.systemDefault())
        );
    }
}
