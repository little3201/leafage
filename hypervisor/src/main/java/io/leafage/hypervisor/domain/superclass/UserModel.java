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

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.Instant;

/**
 * superclass class for User
 *
 * @author wq li
 */
@MappedSuperclass
public abstract class UserModel {

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "given_name")
    private String givenName;

    @Column(name = "family_name")
    private String familyName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "password", nullable = false)
    private String password;

    private String email;

    private String avatar;

    @Column(name = "account_expires_at")
    private Instant accountExpiresAt;

    @Column(name = "credentials_expires_at")
    private Instant credentialsExpiresAt;

    private boolean enabled = true;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
