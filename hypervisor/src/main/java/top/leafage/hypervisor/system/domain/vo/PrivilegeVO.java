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
package top.leafage.hypervisor.system.domain.vo;

import top.leafage.hypervisor.system.domain.Privilege;

import java.util.Set;

/**
 * vo class for privilege.
 *
 * @author wq li
 */
public record PrivilegeVO(
        Long id,
        String name,
        Long superiorId,
        String path,
        String redirect,
        String component,
        String icon,
        Set<String> actions,
        String description,
        boolean enabled,
        long count
) {
    public static PrivilegeVO from(Privilege entity) {
        return from(entity, 0);
    }

    public static PrivilegeVO from(Privilege entity, long count) {
        return new PrivilegeVO(
                entity.getId(),
                entity.getName(),
                entity.getSuperiorId(),
                entity.getPath(),
                entity.getRedirect(),
                entity.getComponent(),
                entity.getIcon(),
                entity.getActions(),
                entity.getDescription(),
                entity.isEnabled(),
                count
        );
    }
}
