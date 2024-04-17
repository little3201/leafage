/*
 *  Copyright 2018-2024 little3201.
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

package io.leafage.basic.hypervisor.domain;

import io.leafage.basic.hypervisor.audit.AuditMetadata;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * model class for message.
 *
 * @author wq li 2022/1/29 17:20
 **/
@Entity
@Table(name = "messages")
public class Message extends AuditMetadata {

    /**
     * 主键
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * 内容
     */
    @Column(name = "content", length = 1000)
    private String content;

    /**
     * 是否已读
     */
    @Column(name = "read", nullable = false)
    private boolean read;

    /**
     * 接收人
     */
    @Column(name = "receiver", nullable = false, length = 50)
    private String receiver;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
