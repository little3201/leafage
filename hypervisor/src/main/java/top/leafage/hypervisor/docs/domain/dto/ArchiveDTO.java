/*
 * Copyright(c) 2019-present the original author or authors.
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

package top.leafage.hypervisor.docs.domain.dto;

import jakarta.validation.constraints.NotBlank;
import top.leafage.hypervisor.docs.domain.Archive;

/**
 * dto class for report.
 *
 * @author wq li
 */
public class ArchiveDTO {

    @NotBlank
    private String title;

    private Long schemaId;

    private String body;

    private String owner;


    public static Archive toEntity(ArchiveDTO dto) {
        return new Archive(
                dto.getTitle(),
                dto.getSchemaId(),
                dto.getBody(),
                dto.getOwner()
        );
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(Long schemaId) {
        this.schemaId = schemaId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
