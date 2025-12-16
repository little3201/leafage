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
package top.leafage.hypervisor.system.service;

import org.jspecify.annotations.NonNull;
import top.leafage.common.data.domain.TreeNode;
import top.leafage.common.data.jpa.JpaCrudService;
import top.leafage.hypervisor.system.domain.dto.GroupDTO;
import top.leafage.hypervisor.system.domain.vo.GroupVO;

import java.util.List;

/**
 * group service.
 *
 * @author wq li
 */
public interface GroupService extends JpaCrudService<GroupDTO, GroupVO> {

    List<TreeNode<@NonNull Long>> tree();
}
