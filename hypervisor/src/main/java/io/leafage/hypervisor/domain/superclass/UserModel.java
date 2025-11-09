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

package io.leafage.hypervisor.domain.superclass;


import org.springframework.data.relational.core.mapping.Column;

import java.time.Instant;

/**
 * superclass class for User
 *
 * @author wq li
 */
public abstract class UserModel {

    @Column(value = "username")
    private String username;

    private String name;

    private String email;

    @Column(value = "account_expires_at")
    private Instant accountExpiresAt;

    @Column(value = "credentials_expires_at")
    private Instant credentialsExpiresAt;

    private boolean enabled = true;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getAccountExpiresAt() {
        return accountExpiresAt;
    }

    public void setAccountExpiresAt(Instant accountExpiresAt) {
        this.accountExpiresAt = accountExpiresAt;
    }

    public Instant getCredentialsExpiresAt() {
        return credentialsExpiresAt;
    }

    public void setCredentialsExpiresAt(Instant credentialsExpiresAt) {
        this.credentialsExpiresAt = credentialsExpiresAt;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
