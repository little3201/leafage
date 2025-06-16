package io.leafage.hypervisor.service;

import io.leafage.hypervisor.dto.SchedulerLogDTO;
import io.leafage.hypervisor.vo.SchedulerLogVO;
import top.leafage.common.jdbc.JdbcCrudService;

/**
 * service for scheduler_logs.
 *
 * @author wq li
 */
public interface SchedulerLogService extends JdbcCrudService<SchedulerLogDTO, SchedulerLogVO> {

    /**
     * 清空日志
     */
    void clear();
}
