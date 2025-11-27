package top.leafage.hypervisor.controller;

import top.leafage.hypervisor.domain.vo.SchedulerLogVO;
import top.leafage.hypervisor.service.SchedulerLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * controller for scheduler_logs.
 *
 * @author wq li
 */
@RestController
@RequestMapping("/scheduler-logs")
public class SchedulerLogController {

    private final Logger logger = LoggerFactory.getLogger(SchedulerLogController.class);

    private final SchedulerLogService schedulerLogService;

    public SchedulerLogController(SchedulerLogService schedulerLogService) {
        this.schedulerLogService = schedulerLogService;
    }

    /**
     * Retrieves a paginated list of records.
     *
     * @param page       The page number.
     * @param size       The number of records per page.
     * @param sortBy     The field to sort by.
     * @param descending Whether sorting should be in descending order.
     * @param filters    The filters.
     * @return A paginated list of records, or 204 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_scheduler_logs')")
    @GetMapping
    public ResponseEntity<Page<SchedulerLogVO>> retrieve(@RequestParam int page, @RequestParam int size,
                                                         String sortBy, boolean descending, String filters) {
        Page<SchedulerLogVO> voPage;
        try {
            voPage = schedulerLogService.retrieve(page, size, sortBy, descending, filters);
        } catch (Exception e) {
            logger.error("Retrieve schedulerlogs occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(voPage);
    }

    /**
     * Fetches a record by ID.
     *
     * @param id The record ID.
     * @return The record data, or 204 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_scheduler_logs')")
    @GetMapping("/{id}")
    public ResponseEntity<SchedulerLogVO> fetch(@PathVariable Long id) {
        SchedulerLogVO vo;
        try {
            vo = schedulerLogService.fetch(id);
        } catch (Exception e) {
            logger.error("Fetch scheduler logs occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(vo);
    }

    /**
     * Checks if a record exists by name.
     *
     * @param name The record name.
     * @param id   The record ID.
     * @return True if the record exists, or 204 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_scheduler_logs')")
    @GetMapping("/exists")
    public ResponseEntity<Boolean> exists(@RequestParam String name, Long id) {
        boolean exist;
        try {
            exist = schedulerLogService.exists(name, id);
        } catch (Exception e) {
            logger.info("Query scheduler logs exist occurred an error: ", e);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(exist);
    }

    /**
     * Removes a record by ID.
     *
     * @param id The record ID.
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_scheduler_logs:remove')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        try {
            schedulerLogService.remove(id);
        } catch (Exception e) {
            logger.error("Remove scheduler logs occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Enable a record when enabled is false or disable when enabled is ture.
     *
     * @param id The record ID.
     * @return 200 status code if successful, or 417 status code if an error occurs.
     */
    @PreAuthorize("hasAuthority('SCOPE_scheduler_logs:enable')")
    @PatchMapping("/{id}")
    public ResponseEntity<Boolean> enable(@PathVariable Long id) {
        boolean enabled;
        try {
            enabled = schedulerLogService.enable(id);
        } catch (Exception e) {
            logger.error("Toggle enabled occurred an error: ", e);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.accepted().body(enabled);
    }


    /**
     * 清空信息
     *
     */
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('SCOPE_operation_logs:clear')")
    @DeleteMapping
    public ResponseEntity<Void> clear() {
        try {
            schedulerLogService.clear();
        } catch (Exception e) {
            logger.error("Clear scheduler log error: ", e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
        return ResponseEntity.ok().build();
    }

}
