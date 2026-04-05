package detailingstudio.controller;

import detailingstudio.dto.ServiceTypeDto;
import detailingstudio.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceTypeController {

    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<ServiceTypeDto>> getActiveServices() {
        return ResponseEntity.ok(scheduleService.getActiveServices());
    }
}
