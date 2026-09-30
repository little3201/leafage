/*
 * Copyright(c) 2019-present the original author or authors.
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

package top.leafage.hypervisor.docs.domain.vo;

import top.leafage.hypervisor.docs.domain.Template;

import java.time.LocalDateTime;

/**
 * vo class for template.
 *
 * @author wq li
 */
public record TemplateVO(
        Long id,
        String name,
        Template.Type type,
        int version,
        Template.Status status,
        boolean enabled,
        LocalDateTime lastModifiedDate
) {
    public static TemplateVO from(Template entity) {
        return new TemplateVO(
                entity.getId(),
                entity.getName(),
                entity.getType(),
                entity.getVersion(),
                entity.getStatus(),
                entity.isEnabled(),
                entity.getLastModifiedDate().isPresent() ? entity.getLastModifiedDate().get() : null
        );
    }
}
