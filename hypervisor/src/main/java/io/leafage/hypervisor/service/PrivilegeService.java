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

package io.leafage.hypervisor.service;

import io.leafage.hypervisor.dto.PrivilegeDTO;
import io.leafage.hypervisor.vo.PrivilegeVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.TreeNode;
import top.leafage.common.reactive.ReactiveBasicService;

import java.util.List;

/**
 * privilege service
 *
 * @author wq li
 */
public interface PrivilegeService extends ReactiveBasicService<PrivilegeDTO, PrivilegeVO> {

    /**
     * 查询构造树结构的数据
     *
     * @param username 账号
     * @return 数据集
     */
    Mono<List<TreeNode<Long>>> tree(String username);

    /**
     * 获取下级
     *
     * @param id 主键
     * @return 数据集
     */
    Flux<PrivilegeVO> subset(Long id);

}
