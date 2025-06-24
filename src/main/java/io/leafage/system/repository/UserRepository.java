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

import io.leafage.system.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * user repository.
 *
 * @author wq li
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long>,
        PagingAndSortingRepository<User, Long> {

    /**
     * Finds all records with username or full_name containing the specified string.
     *
     * @param name     The name filter for the records.
     * @param pageable The pagination information.
     * @return A paginated list of templates.
     */
    Page<User> findAllByUsernameContaining(String name, Pageable pageable);

    /**
     * Finds a record by username.
     *
     * @param username The username of the record.
     * @return An Optional containing the record if found, or an empty Optional if not found.
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a record exists by username.
     *
     * @param username The username of the record.
     * @return true if the record exists, false otherwise.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a record exists by username, excluding a specific ID.
     *
     * @param username The username of the record.
     * @param id       The ID to exclude from the check.
     * @return true if the record exists, false otherwise.
     */
    boolean existsByUsernameAndIdNot(String username, Long id);

    /**
     * Toggles the enabled status of a record by its ID.
     *
     * @param id The ID of the record.
     * @return true if the update was successful, false otherwise.
     */
    @Modifying
    @Query("UPDATE users SET enabled = NOT enabled WHERE id = :id")
    boolean updateEnabledById(Long id);
}
