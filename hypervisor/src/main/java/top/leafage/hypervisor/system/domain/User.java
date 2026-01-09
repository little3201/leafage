/*
 * Copyright (c) 2026.  little3201.
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
package top.leafage.hypervisor.system.domain;


import org.jspecify.annotations.NonNull;
import org.springframework.data.relational.core.mapping.Table;
import top.leafage.common.data.domain.AbstractAuditable;

/**
 * entity class for user.
 *
 * @author wq li
 */
@Table(name = "users")
public class User extends AbstractAuditable<@NonNull String, @NonNull Long> {


    private String username;

    private String fullName;

    private String email;

    private String password;

    private boolean accountNonLocked = true;

    private boolean accountNonExpired = true;

    private boolean credentialsNonExpired = true;

    private boolean enabled = true;

    public User() {
    }

    public User(String username, String fullName, String email, String password) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    public enum UserStatus {
        NORMAL,           // 正常
        DISABLED,         // 已禁用
        LOCKED,           // 已锁定
        ACCOUNT_EXPIRED,  // 账户已过期
        CREDENTIALS_EXPIRED; // 密码已过期

        public static UserStatus of(boolean enabled,
                                    boolean accountNonExpired,
                                    boolean accountNonLocked,
                                    boolean credentialsNonExpired) {

            if (!enabled) {
                return DISABLED;
            }
            if (!accountNonExpired) {
                return ACCOUNT_EXPIRED;
            }
            if (!accountNonLocked) {
                return LOCKED;
            }
            if (!credentialsNonExpired) {
                return CREDENTIALS_EXPIRED;
            }
            return NORMAL;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
