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
package io.leafage.hypervisor.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import top.leafage.common.servlet.audit.AuditMetadata;

import java.time.Instant;

/**
 * model class for user.
 *
 * @author wq li
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class User extends AuditMetadata {

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

    @Column(name = "phone_number")
    private String phoneNumber;

    private String avatar;

    @Column(name = "account_expires_at")
    private Instant accountExpiresAt;

    @Column(name = "account_non_locked")
    private boolean accountNonLocked;

    @Column(name = "credentials_expires_at")
    private Instant credentialsExpiresAt;

    /**
     * <p>Getter for the field <code>username</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getUsername() {
        return username;
    }

    /**
     * <p>Setter for the field <code>username</code>.</p>
     *
     * @param username a {@link String} object
     */
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

    /**
     * <p>Getter for the field <code>password</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getPassword() {
        return password;
    }

    /**
     * <p>Setter for the field <code>password</code>.</p>
     *
     * @param password a {@link String} object
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * <p>Getter for the field <code>email</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getEmail() {
        return email;
    }

    /**
     * <p>Setter for the field <code>email</code>.</p>
     *
     * @param email a {@link String} object
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * <p>Getter for the field <code>avatar</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * <p>Setter for the field <code>avatar</code>.</p>
     *
     * @param avatar a {@link String} object
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * <p>Getter for the field <code>accountExpiresAt</code>.</p>
     *
     * @return a {@link java.time.Instant} object
     */
    public Instant getAccountExpiresAt() {
        return accountExpiresAt;
    }

    /**
     * <p>Setter for the field <code>accountExpiresAt</code>.</p>
     *
     * @param accountExpiresAt a {@link java.time.Instant} object
     */
    public void setAccountExpiresAt(Instant accountExpiresAt) {
        this.accountExpiresAt = accountExpiresAt;
    }

    /**
     * <p>isAccountNonLocked.</p>
     *
     * @return a boolean
     */
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    /**
     * <p>Setter for the field <code>accountNonLocked</code>.</p>
     *
     * @param accountNonLocked a boolean
     */
    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    /**
     * <p>Getter for the field <code>credentialsExpiresAt</code>.</p>
     *
     * @return a {@link java.time.Instant} object
     */
    public Instant getCredentialsExpiresAt() {
        return credentialsExpiresAt;
    }

    /**
     * <p>Setter for the field <code>credentialsExpiresAt</code>.</p>
     *
     * @param credentialsExpiresAt a {@link java.time.Instant} object
     */
    public void setCredentialsExpiresAt(Instant credentialsExpiresAt) {
        this.credentialsExpiresAt = credentialsExpiresAt;
    }
}
