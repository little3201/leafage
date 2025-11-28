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
package top.leafage.hypervisor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.leafage.hypervisor.domain.GroupMembers;

import java.util.List;
import java.util.Optional;

/**
 * group members repository.
 *
 * @author wq li
 */
@Repository
public interface GroupMembersRepository extends JpaRepository<GroupMembers, Long> {

    /**
     * 根据group查user
     *
     * @param groupId group主键
     * @return 关联数据集
     */
    List<GroupMembers> findAllByGroupId(Long groupId);

    /**
     * 根据group查user
     *
     * @param groupId privilege主键
     * @return 关联数据集
     */
    Optional<GroupMembers> findByGroupIdAndUsername(Long groupId, String username);

    /**
     * 根据user查group
     *
     * @param username 用户名
     * @return 关联数据集
     */
    List<GroupMembers> findAllByUsername(String username);

}
