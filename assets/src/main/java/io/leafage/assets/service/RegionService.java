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

package io.leafage.assets.service;

import io.leafage.assets.dto.RegionDTO;
import io.leafage.assets.vo.RegionVO;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.r2dbc.R2dbcCrudService;

/**
 * region service
 *
 * @author wq li
 */
public interface RegionService extends R2dbcCrudService<RegionDTO, RegionVO> {

    /**
     * 获取下级
     *
     * @param id 主键
     * @return 数据集
     */
    Flux<RegionVO> subset(Long id);

}
