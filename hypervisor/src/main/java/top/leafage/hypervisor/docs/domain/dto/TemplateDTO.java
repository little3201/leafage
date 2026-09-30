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
import jakarta.validation.constraints.NotNull;
import top.leafage.hypervisor.docs.domain.Template;

/**
 * dto class for template.
 *
 * @author wq li
 */
public class TemplateDTO {

    @NotBlank
    private String name;

    @NotNull
    private Template.Type type;


    public static Template toEntity(TemplateDTO dto) {
        return new Template(
                dto.getName(),
                dto.getType()
        );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Template.Type getType() {
        return type;
    }

    public void setType(Template.Type type) {
        this.type = type;
    }

}
