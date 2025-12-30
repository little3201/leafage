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

import top.leafage.hypervisor.assets.domain.Region;

/**
 * vo class for region.
 *
 * @author wq li
 */
public record RegionVO(
        Long id,
        Long superioeId,
        String name,
        String areaCode,
        String postalCode,
        String description,
        long count,
        boolean enabled
) {
    public static RegionVO from(Region entity) {
        return RegionVO.from(entity, 0);
    }

    public static RegionVO from(Region entity, long count) {
        return new RegionVO(
                entity.getId(),
                entity.getSuperiorId(),
                entity.getName(),
                entity.getAreaCode(),
                entity.getPostalCode(),
                entity.getDescription(),
                count,
                entity.isEnabled()
        );
    }
}
