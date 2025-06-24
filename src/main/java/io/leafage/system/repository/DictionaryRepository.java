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

package io.leafage.system.repository;

import io.leafage.system.domain.Dictionary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * dictionary repository.
 *
 * @author wq li
 */
@Repository
public interface DictionaryRepository extends CrudRepository<Dictionary, Long>,
        PagingAndSortingRepository<Dictionary, Long> {

    /**
     * Finds all records where the superior ID is null.
     *
     * @param pageable The pagination information.
     * @return A paginated list of records.
     */
    Page<Dictionary> findAllBySuperiorIdIsNull(Pageable pageable);

    /**
     * 是否存在
     *
     * @param name 名称
     * @return true-存在，false-否
     */
    boolean existsBySuperiorIdAndName(Long superiorId, String name);

    /**
     * 是否存在
     *
     * @param superiorId superior id
     * @param name       名称
     * @param id         主键
     * @return true-存在，false-否
     */
    boolean existsBySuperiorIdAndNameAndIdNot(Long superiorId, String name, Long id);

    /**
     * 查询下级信息
     *
     * @param superiorId 上级主键
     * @return 结果
     */
    List<Dictionary> findAllBySuperiorId(Long superiorId);

    @Modifying
    @Query("UPDATE dictionaries SET enabled = NOT enabled WHERE id = :id")
    boolean updateEnabledById(Long id);
}
