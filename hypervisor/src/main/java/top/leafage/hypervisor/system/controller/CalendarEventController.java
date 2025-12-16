package top.leafage.hypervisor.system.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * calendar events controller.
 *
 * @author wq li
 */
@RestController
@RequestMapping("calendar-events")
public class CalendarEventController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> retrieve(@RequestParam Integer month) {

        return ResponseEntity.ok().build();
    }
}
