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

package io.leafage.assets.service;

import io.leafage.assets.dto.RegionDTO;
import io.leafage.assets.vo.RegionVO;
import org.springframework.data.domain.Page;
import top.leafage.common.servlet.ServletBasicService;

/**
 * region service.
 *
 * @author wq li
 */
public interface RegionService extends ServletBasicService<RegionDTO, RegionVO> {

    /**
     * 分页查询
     *
     * @param page       页码
     * @param size       大小
     * @param sortBy     排序
     * @param descending 顺序
     * @return 查询结果
     */
    Page<RegionVO> retrieve(int page, int size, String sortBy, boolean descending, Long superiorId, String name);

}
