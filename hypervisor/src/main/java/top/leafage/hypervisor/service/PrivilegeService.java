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
package top.leafage.hypervisor.service;

import org.jspecify.annotations.NonNull;
import top.leafage.common.data.domain.TreeNode;
import top.leafage.common.data.jpa.JpaCrudService;
import top.leafage.hypervisor.domain.dto.PrivilegeDTO;
import top.leafage.hypervisor.domain.vo.PrivilegeVO;

import java.util.List;

/**
 * privilege service.
 *
 * @author wq li
 */
public interface PrivilegeService extends JpaCrudService<PrivilegeDTO, PrivilegeVO> {

    /**
     * 获取树结构数据
     *
     * @param username username
     * @return 树结构数据集
     */
    List<TreeNode<@NonNull Long>> tree(String username);

    /**
     * get subset
     *
     * @param superiorId superior主键
     * @return the result.
     */
    List<PrivilegeVO> subset(Long superiorId);


    Long countBySuperiorId(Long superiorId);
}
