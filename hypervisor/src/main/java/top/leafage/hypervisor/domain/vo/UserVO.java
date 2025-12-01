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

package top.leafage.hypervisor.domain.vo;


import top.leafage.hypervisor.domain.User;

/**
 * vo class for user.
 *
 * @author wq li
 */
public record UserVO(
        Long id,
        String username,
        String fullName,
        String email,
        String status,
        boolean enabled
) {
    public static UserVO from(User entity) {
        return new UserVO(
                entity.getId(),
                entity.getUsername(),
                entity.getFullName(),
                mask(entity.getEmail()),
                "ACTIVE",
                entity.isEnabled()
        );
    }

    private static String mask(String email) {
        if (email == null || email.isEmpty()) {
            return "";
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 3) {
            return email; // 邮箱太短不脱敏
        }

        String prefix = email.substring(0, 3);
        String suffix = email.substring(atIndex);
        int starCount = atIndex - 3;

        return prefix + "*".repeat(starCount) + suffix;
    }
}
