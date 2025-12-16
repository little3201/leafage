package top.leafage.hypervisor.system.service;

import top.leafage.common.data.jpa.JpaCrudService;
import top.leafage.hypervisor.system.domain.vo.SchedulerLogVO;

/**
 * service for scheduler_logs.
 *
 * @author wq li
 */
public interface SchedulerLogService extends JpaCrudService<Object, SchedulerLogVO> {

    /**
     * 清空日志
     */
    void clear();
}
