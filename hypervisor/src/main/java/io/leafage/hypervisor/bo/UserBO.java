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

package io.leafage.hypervisor.bo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

/**
 * bo class for User
 *
 * @author wq li
 */
public abstract class UserBO {

    @NotBlank
    private String username;

    @NotBlank
    private String givenName;

    @NotBlank
    private String familyName;

    private String middleName;

    @Email
    private String email;

    private String avatar;

    private Instant accountExpiresAt;

    private boolean accountNonLocked;

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
