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

import io.leafage.system.domain.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * group repository.
 *
 * @author wq li
 */
@Repository
public interface GroupRepository extends ListCrudRepository<Group, Long>,
        PagingAndSortingRepository<Group, Long> {

    /**
     * Finds all records where the superior ID is null.
     *
     * @param pageable The pagination information.
     * @return A paginated list of records.
     */
    Page<Group> findAllBySuperiorIdIsNull(Pageable pageable);

    /**
     * Finds all records where the superior ID.
     *
     * @param pageable The pagination information.
     * @return A paginated list of records.
     */
    Page<Group> findAllBySuperiorId(Long superiorId, Pageable pageable);

    /**
     * Checks if a record exists by name.
     *
     * @param name The name of the record.
     * @return true if the record exists, false otherwise.
     */
    boolean existsByName(String name);

    /**
     * Checks if a record exists by name, excluding a specific ID.
     *
     * @param name The name of the record.
     * @param id   The ID to exclude from the check.
     * @return true if the record exists, false otherwise.
     */
    boolean existsByNameAndIdNot(String name, Long id);

    /**
     * Toggles the enabled status of a record by its ID.
     *
     * @param id The ID of the record.
     * @return true if the update was successful, false otherwise.
     */
    @Modifying
    @Query("UPDATE groups SET enabled = NOT enabled WHERE id = :id")
    boolean updateEnabledById(Long id);
}
