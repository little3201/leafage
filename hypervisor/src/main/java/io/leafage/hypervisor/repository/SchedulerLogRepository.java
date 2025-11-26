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
}
