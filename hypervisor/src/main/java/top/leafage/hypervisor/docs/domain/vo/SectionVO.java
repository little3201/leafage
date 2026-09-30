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

import top.leafage.hypervisor.docs.domain.Section;

import java.util.Map;

/**
 * vo class for section.
 *
 * @author wq li
 */
public record SectionVO(
        Long id,
        String name,
        Long superiorId,
        Long ownerId,
        Section.OwnerType ownerType,
        Integer sequence,
        Integer level,
        Map<String, Object> body,
        long count
) {
    public static SectionVO from(Section entity) {
        return from(entity, 0);
    }

    public static SectionVO from(Section entity, long count) {
        return new SectionVO(
                entity.getId(),
                entity.getName(),
                entity.getSuperiorId(),
                entity.getOwnerId(),
                entity.getOwnerType(),
                entity.getSequence(),
                entity.getLevel(),
                entity.getBody(),
                count
        );
    }
}
