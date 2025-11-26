package io.leafage.hypervisor.service;

import io.leafage.hypervisor.domain.dto.SchedulerLogDTO;
import io.leafage.hypervisor.domain.vo.SchedulerLogVO;
import top.leafage.common.data.jpa.JpaCrudService;

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
