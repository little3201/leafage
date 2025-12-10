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
        int atIndex = email.lastIndexOf('@');
        if (atIndex <= 0) {
            // 没有@或@在开头，非法邮箱，直接返回原值或空
            return email;
        }

        String prefix = email.substring(0, atIndex); // @前的用户名部分
        String domain = email.substring(atIndex);    // 包含@的域名部分

        if (prefix.length() <= 1) {
            // 用户名只有1个字符，如 a@qq.com
            return prefix.charAt(0) + "****" + domain;
        } else {
            // 用户名 ≥2 个字符，保留第一个，后面全部变*
            return prefix.charAt(0) + "****" + domain;
        }
    }
}
