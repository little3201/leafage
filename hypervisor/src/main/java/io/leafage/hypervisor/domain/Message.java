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
import jakarta.persistence.Table;
import top.leafage.common.servlet.audit.AuditMetadata;

/**
 * model class for message.
 *
 * @author wq li
 */
@Entity
@Table(name = "messages")
public class Message extends AuditMetadata {


    @Column(name = "title", nullable = false)
    private String title;

    /**
     * 内容
     */
    @Column(name = "content", length = 1000)
    private String content;

    /**
     * 是否未读
     */
    private boolean unread = true;

    /**
     * 接收人
     */
    @Column(name = "receiver", nullable = false, length = 50)
    private String receiver;


    /**
     * <p>Getter for the field <code>title</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getTitle() {
        return title;
    }

    /**
     * <p>Setter for the field <code>title</code>.</p>
     *
     * @param title a {@link String} object
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * <p>Getter for the field <code>content</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getContent() {
        return content;
    }

    /**
     * <p>Setter for the field <code>content</code>.</p>
     *
     * @param content a {@link String} object
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * <p>isRead.</p>
     *
     * @return a boolean
     */
    public boolean isUnread() {
        return unread;
    }

    /**
     * <p>Setter for the field <code>unread</code>.</p>
     *
     * @param unread a boolean
     */
    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    /**
     * <p>Getter for the field <code>receiver</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * <p>Setter for the field <code>receiver</code>.</p>
     *
     * @param receiver a {@link String} object
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
