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

package top.leafage.hypervisor.domain.dto;

import jakarta.validation.constraints.NotBlank;
import top.leafage.hypervisor.domain.Message;

/**
 * dto class for message.
 *
 * @author wq li
 */
public class MessageDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String body;

    @NotBlank
    private String receiver;


    public static Message toEntity(MessageDTO dto) {
        return new Message(
                dto.getTitle(),
                dto.getBody(),
                dto.getReceiver()
        );
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
