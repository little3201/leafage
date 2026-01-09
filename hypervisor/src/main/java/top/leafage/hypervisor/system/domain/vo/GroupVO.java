/*
 * Copyright (c) 2024-2026.  little3201.
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
package top.leafage.hypervisor.system.domain.vo;

import top.leafage.hypervisor.system.domain.Group;

/**
 * vo class for group.
 *
 * @author wq li
 */
public record GroupVO(
        Long id,
        String name,
        String description,
        boolean enabled
) {
    public static GroupVO from(Group entity) {
        return new GroupVO(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.isEnabled()
        );
    }
}
