package top.leafage.hypervisor.service;

import top.leafage.common.data.jpa.JpaCrudService;
import top.leafage.hypervisor.domain.dto.SchedulerLogDTO;
import top.leafage.hypervisor.domain.vo.SchedulerLogVO;

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
