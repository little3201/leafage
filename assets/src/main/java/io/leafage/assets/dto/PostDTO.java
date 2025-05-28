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

package io.leafage.assets.dto;

import io.leafage.assets.bo.PostBO;
import jakarta.validation.constraints.NotNull;

/**
 * dto class for post
 *
 * @author wq li
 */
public class PostDTO extends PostBO {

    /**
     * 分类
     */
    @NotNull(message = "categoryId must not be null.")
    private Long categoryId;

    /**
     * <p>Getter for the field <code>categoryId</code>.</p>
     *
     * @return a {@link java.lang.Long} object
     */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * <p>Setter for the field <code>categoryId</code>.</p>
     *
     * @param categoryId a {@link java.lang.Long} object
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

}
