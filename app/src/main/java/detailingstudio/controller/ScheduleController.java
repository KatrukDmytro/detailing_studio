package detailingstudio.controller;

import detailingstudio.dto.AvailableSlotResponse;
import detailingstudio.dto.ScheduleDto;
import detailingstudio.service.ScheduleService;
import detailingstudio.service.SlotCalculationService;
import detailingstudio.repository.ServiceTypeRepository;
import detailingstudio.model.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final SlotCalculationService slotCalculationService;
    private final ScheduleService scheduleService;
    private final ServiceTypeRepository serviceTypeRepository;

    @GetMapping("/available-slots")
    public ResponseEntity<List<AvailableSlotResponse>> getAvailableSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam List<Long> serviceIds) {

        // Calculate total duration from selected services
        List<ServiceType> services = serviceTypeRepository.findAllById(serviceIds);
        int totalDuration = services.stream()
                .mapToInt(ServiceType::getDurationMinutes)
                .sum();

        if (totalDuration == 0) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        return ResponseEntity.ok(slotCalculationService.getAvailableSlots(date, totalDuration));
    }

    @GetMapping("/available-dates")
    public ResponseEntity<List<LocalDate>> getAvailableDates(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam List<Long> serviceIds) {

        List<ServiceType> services = serviceTypeRepository.findAllById(serviceIds);
        int totalDuration = services.stream()
                .mapToInt(ServiceType::getDurationMinutes)
                .sum();

        if (totalDuration == 0) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        return ResponseEntity.ok(slotCalculationService.getAvailableDates(year, month, totalDuration));
    }

    @GetMapping("/working-days")
    public ResponseEntity<List<ScheduleDto>> getWorkingDays() {
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }
}
