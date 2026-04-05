package detailingstudio.controller;

import detailingstudio.dto.*;
import detailingstudio.model.BookingStatus;
import detailingstudio.service.BookingService;
import detailingstudio.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BookingService bookingService;
    private final ScheduleService scheduleService;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    // ---- Auth ----

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        if (adminUsername.equals(loginRequest.getUsername()) &&
                adminPassword.equals(loginRequest.getPassword())) {
            // Generate base64 credentials for HTTP Basic auth
            String credentials = Base64.getEncoder().encodeToString(
                    (loginRequest.getUsername() + ":" + loginRequest.getPassword()).getBytes());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "token", "Basic " + credentials,
                    "username", loginRequest.getUsername()
            ));
        }
        return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid credentials"));
    }

    // ---- Bookings ----

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<BookingResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }

    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Services ----

    @GetMapping("/services")
    public ResponseEntity<List<ServiceTypeDto>> getAllServices() {
        return ResponseEntity.ok(scheduleService.getAllServices());
    }

    @PostMapping("/services")
    public ResponseEntity<ServiceTypeDto> createService(@RequestBody ServiceTypeDto dto) {
        return ResponseEntity.ok(scheduleService.createService(dto));
    }

    @PutMapping("/services/{id}")
    public ResponseEntity<ServiceTypeDto> updateService(@PathVariable Long id, @RequestBody ServiceTypeDto dto) {
        return ResponseEntity.ok(scheduleService.updateService(id, dto));
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        scheduleService.deleteService(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Schedule ----

    @GetMapping("/schedule")
    public ResponseEntity<List<ScheduleDto>> getSchedule() {
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }

    @PutMapping("/schedule")
    public ResponseEntity<List<ScheduleDto>> updateSchedule(@RequestBody List<ScheduleDto> schedules) {
        return ResponseEntity.ok(scheduleService.updateSchedules(schedules));
    }

    // ---- Breaks ----

    @GetMapping("/breaks")
    public ResponseEntity<BreakConfigDto> getBreaks() {
        return ResponseEntity.ok(scheduleService.getBreakConfig());
    }

    @PutMapping("/breaks")
    public ResponseEntity<BreakConfigDto> updateBreaks(@RequestBody BreakConfigDto dto) {
        return ResponseEntity.ok(scheduleService.updateBreakConfig(dto));
    }
}
