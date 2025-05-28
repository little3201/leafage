/*
 *  Copyright 2018-2025 little3201.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.leafage.hypervisor.domain;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import top.leafage.common.reactive.audit.ReactiveAuditMetadata;

import java.time.LocalDateTime;

/**
 * model class for User
 *
 * @author wq li
 */
@Table(name = "users")
public class User extends ReactiveAuditMetadata {

    /**
     * 用户名
     */
    private String username;

    /**
     * 名
     */
    @Column(value = "given_name")
    private String givenName;

    /**
     * 中间名
     */
    @Column(value = "middle_name")
    private String middleName;

    /**
     * 姓
     */
    @Column(value = "family_name")
    private String familyName;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像
     */
    private String avatar;

    /**
     * user有效期
     */
    @Column(value = "account_expires_at")
    private LocalDateTime accountExpiresAt;

    /**
     * 是否锁定
     */
    @Column(value = "account_non_locked")
    private boolean accountNonLocked = true;

    /**
     * 密码有效期
     */
    @Column(value = "credentials_expires_at")
    private LocalDateTime credentialsExpiresAt;

    /**
     * <p>Getter for the field <code>username</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getUsername() {
        return username;
    }

    /**
     * <p>Setter for the field <code>username</code>.</p>
     *
     * @param username a {@link java.lang.String} object
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    /**
     * <p>Getter for the field <code>password</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getPassword() {
        return password;
    }

    /**
     * <p>Setter for the field <code>password</code>.</p>
     *
     * @param password a {@link java.lang.String} object
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * <p>Getter for the field <code>avatar</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * <p>Setter for the field <code>avatar</code>.</p>
     *
     * @param avatar a {@link java.lang.String} object
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * <p>Getter for the field <code>accountExpiresAt</code>.</p>
     *
     * @return a {@link java.time.LocalDateTime} object
     */
    public LocalDateTime getAccountExpiresAt() {
        return accountExpiresAt;
    }

    /**
     * <p>Setter for the field <code>accountExpiresAt</code>.</p>
     *
     * @param accountExpiresAt a {@link java.time.LocalDateTime} object
     */
    public void setAccountExpiresAt(LocalDateTime accountExpiresAt) {
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
     * @return a {@link java.time.LocalDateTime} object
     */
    public LocalDateTime getCredentialsExpiresAt() {
        return credentialsExpiresAt;
    }

    /**
     * <p>Setter for the field <code>credentialsExpiresAt</code>.</p>
     *
     * @param credentialsExpiresAt a {@link java.time.LocalDateTime} object
     */
    public void setCredentialsExpiresAt(LocalDateTime credentialsExpiresAt) {
        this.credentialsExpiresAt = credentialsExpiresAt;
    }
}
