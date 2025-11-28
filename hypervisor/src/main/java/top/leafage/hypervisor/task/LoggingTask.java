package top.leafage.hypervisor.task;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.leafage.hypervisor.service.OperationLogService;
import top.leafage.hypervisor.service.SchedulerLogService;

@Component
public class LoggingTask {

    private final OperationLogService operationLogService;
    private final SchedulerLogService schedulerLogService;

    public LoggingTask(OperationLogService operationLogService, SchedulerLogService schedulerLogService) {
        this.operationLogService = operationLogService;
        this.schedulerLogService = schedulerLogService;
    }

    @Scheduled(initialDelay = 10000, fixedDelay = 1000 * 60 * 60 * 24)
    public void clear() {
        operationLogService.clear();
        schedulerLogService.clear();
    }
}
