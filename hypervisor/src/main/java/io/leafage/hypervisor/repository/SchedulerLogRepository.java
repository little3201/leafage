package io.leafage.hypervisor.repository;

import io.leafage.hypervisor.domain.SchedulerLog;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for scheduler_logs.
 * Extends JpaRepository.
 *
 * @author wq li
 **/
@Repository
public interface SchedulerLogRepository extends JpaRepository<SchedulerLog, Long> {

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
     * @return 1 if the update was successful, 0 otherwise.
     */
    @Transactional
    @Modifying
    @Query("UPDATE SchedulerLog t SET t.enabled = CASE WHEN t.enabled = true THEN false ELSE true END WHERE t.id = :id")
    int updateEnabledById(Long id);
}
