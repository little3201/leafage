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

package io.leafage.hypervisor.dto;

import io.leafage.hypervisor.domain.superclass.DictionaryModel;

/**
 * dto class for dictionary.
 *
 * @author wq li
 */
public class DictionaryDTO extends DictionaryModel {

    /**
     * superior
     */
    private Long superiorId;

    /**
     * <p>Getter for the field <code>superiorId</code>.</p>
     *
     * @return a {@link Long} object
     */
    public Long getSuperiorId() {
        return superiorId;
    }

    /**
     * {@inheritDoc}
     */
    public void setSuperiorId(Long superiorId) {
        this.superiorId = superiorId;
    }
}
