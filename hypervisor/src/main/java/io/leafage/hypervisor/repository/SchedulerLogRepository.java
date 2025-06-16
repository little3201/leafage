package io.leafage.hypervisor.repository;

import io.leafage.hypervisor.domain.SchedulerLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for scheduler_logs.
 * Extends JpaRepository.
 *
 * @author wq li
 **/
@Repository
public interface SchedulerLogRepository extends JpaRepository<SchedulerLog, Long>, JpaSpecificationExecutor<SchedulerLog> {

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

}
