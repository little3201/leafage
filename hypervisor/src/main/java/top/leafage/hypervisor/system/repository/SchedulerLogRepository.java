package top.leafage.hypervisor.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import top.leafage.hypervisor.system.domain.SchedulerLog;

/**
 * Repository interface for scheduler_logs.
 * Extends JpaRepository.
 *
 * @author wq li
 **/
@Repository
public interface SchedulerLogRepository extends JpaRepository<SchedulerLog, Long>, JpaSpecificationExecutor<SchedulerLog> {
}
