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


import top.leafage.hypervisor.system.domain.User;

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
        return from(entity, true);
    }

    public static UserVO from(User entity, boolean maskEmail) {
        return new UserVO(
                entity.getId(),
                entity.getUsername(),
                entity.getFullName(),
                mask(entity.getEmail(), maskEmail),
                Status.determineStatus(entity).name(),
                entity.isEnabled()
        );
    }

    private static String mask(String email, boolean mask) {
        if (email == null || email.isEmpty()) {
            return "";
        } else if (!mask) {
            return email;
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

    public enum Status {
        ACTIVE,                  // 正常可用
        LOCKED,                  // 账户被锁定
        EXPIRED,                // 账户已过期
        CREDENTIALS_EXPIRED, // 凭证（密码）已过期
        DISABLED;              // 账户被禁用

        public static Status determineStatus(User entity) {
            if (entity.isAccountNonExpired() &&
                    entity.isAccountNonLocked() &&
                    entity.isCredentialsNonExpired() &&
                    entity.isEnabled()) {
                return ACTIVE;
            } else if (!entity.isAccountNonExpired()) {
                return EXPIRED;
            } else if (!entity.isAccountNonLocked()) {
                return LOCKED;
            } else if (!entity.isCredentialsNonExpired()) {
                return CREDENTIALS_EXPIRED;
            } else {
                return DISABLED;
            }
        }
    }
}
