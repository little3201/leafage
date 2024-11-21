/*
 * Copyright (c) 2024.  little3201.
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
package io.leafage.basic.hypervisor.service;

import io.leafage.basic.hypervisor.dto.PrivilegeDTO;
import io.leafage.basic.hypervisor.vo.PrivilegeVO;
import org.springframework.data.domain.Page;
import top.leafage.common.TreeNode;
import top.leafage.common.servlet.ServletBasicService;

import java.util.List;

/**
 * privilege service.
 *
 * @author wq li
 */
public interface PrivilegeService extends ServletBasicService<PrivilegeDTO, PrivilegeVO> {

    /**
     * Retrieves a paginated list of records.
     *
     * @param page       The page number (zero-based).
     * @param size       The number of records per page.
     * @param sortBy     The field to sort by. If null, records are unsorted.
     * @param descending Whether sorting should be in descending order.
     * @param name       The name filter for the records.
     * @return A paginated list of records.
     */
    Page<PrivilegeVO> retrieve(int page, int size, String sortBy, boolean descending, String name);

    /**
     * 获取树结构数据
     *
     * @param username username
     * @return 树结构数据集
     */
    List<TreeNode> tree(String username);

    /**
     * get subset
     *
     * @param superiorId superior主键
     * @return 结果
     */
    List<PrivilegeVO> subset(Long superiorId);
}
