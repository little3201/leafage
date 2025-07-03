package io.leafage.hypervisor.service;

import io.leafage.hypervisor.dto.SchedulerLogDTO;
import io.leafage.hypervisor.vo.SchedulerLogVO;
import top.leafage.common.jpa.JpaCrudService;

/**
 * service for scheduler_logs.
 *
 * @author wq li
 */
public interface SchedulerLogService extends JpaCrudService<SchedulerLogDTO, SchedulerLogVO> {

    /**
     * 清空日志
     */
    void clear();
}
