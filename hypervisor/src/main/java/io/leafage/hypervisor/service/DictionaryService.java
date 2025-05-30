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

package io.leafage.hypervisor.service;

import io.leafage.hypervisor.dto.DictionaryDTO;
import io.leafage.hypervisor.vo.DictionaryVO;
import top.leafage.common.servlet.ServletBasicService;

import java.util.List;

/**
 * dictionary service.
 *
 * @author wq li
 */
public interface DictionaryService extends ServletBasicService<DictionaryDTO, DictionaryVO> {

    /**
     * 获取子节点
     *
     * @param id a {@link Long} object
     * @return 数据集
     */
    List<DictionaryVO> subset(Long id);

    /**
     * Checks if a record exists by it's superiorId and name.
     *
     * @param superiorId the record's superiorId.
     * @param name       the record's name.
     * @param id         the record's id.
     * @return a Mono emitting true if the record exists, false otherwise.
     */
    boolean exists(Long superiorId, String name, Long id);
}
