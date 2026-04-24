package detailingstudio.service;

import detailingstudio.dto.AvailableSlotResponse;
import detailingstudio.model.*;
import detailingstudio.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SlotCalculationService {

    private final BookingRepository bookingRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final BreakConfigRepository breakConfigRepository;

    /**
     * Calculate available time slots for a given date and total service duration.
     */
    public List<AvailableSlotResponse> getAvailableSlots(LocalDate date, int totalDurationMinutes) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        Optional<WorkSchedule> scheduleOpt = workScheduleRepository.findByDayOfWeek(dayOfWeek);

        if (scheduleOpt.isEmpty() || !scheduleOpt.get().getIsWorkingDay()) {
            return Collections.emptyList();
        }

        WorkSchedule schedule = scheduleOpt.get();
        BreakConfig breakConfig = breakConfigRepository.findAll().stream().findFirst()
                .orElse(BreakConfig.builder().breakBetweenAppointmentsMinutes(30).build());

        int breakMinutes = breakConfig.getBreakBetweenAppointmentsMinutes();

        // Get existing bookings for this day
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        List<Booking> existingBookings = bookingRepository.findBookingsForDay(dayStart, dayEnd);

        // Generate slots every 30 minutes within working hours
        List<AvailableSlotResponse> slots = new ArrayList<>();
        LocalTime slotStart = schedule.getStartTime();
        LocalTime workEnd = schedule.getEndTime();

        while (slotStart.plusMinutes(totalDurationMinutes).compareTo(workEnd) <= 0) {
            LocalTime slotEnd = slotStart.plusMinutes(totalDurationMinutes);

            boolean isAvailable = isSlotAvailable(
                    date, slotStart, slotEnd, existingBookings, breakConfig, breakMinutes
            );

            slots.add(AvailableSlotResponse.builder()
                    .startTime(slotStart)
                    .endTime(slotEnd)
                    .available(isAvailable)
                    .build());

            slotStart = slotStart.plusMinutes(30);
        }

        return slots;
    }

    public List<LocalDate> getAvailableDates(int year, int month, int totalDurationMinutes) {
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
        List<LocalDate> availableDates = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate date = yearMonth.atDay(i);
            if (date.isBefore(today)) {
                continue; // Skip past dates
            }

            List<AvailableSlotResponse> slots = getAvailableSlots(date, totalDurationMinutes);
            boolean hasAvailableSlot = slots.stream().anyMatch(AvailableSlotResponse::isAvailable);

            if (hasAvailableSlot) {
                availableDates.add(date);
            }
        }

        return availableDates;
    }

    private boolean isSlotAvailable(LocalDate date, LocalTime slotStart, LocalTime slotEnd,
                                     List<Booking> existingBookings, BreakConfig breakConfig,
                                     int breakMinutes) {
        // Check lunch break overlap
        if (breakConfig.getLunchStart() != null && breakConfig.getLunchEnd() != null) {
            if (timesOverlap(slotStart, slotEnd, breakConfig.getLunchStart(), breakConfig.getLunchEnd())) {
                return false;
            }
        }

        // Don't allow booking in the past
        if (date.equals(LocalDate.now()) && slotStart.isBefore(LocalTime.now())) {
            return false;
        }

        // Check overlap with existing bookings (including break buffer)
        for (Booking booking : existingBookings) {
            LocalTime bookingStart = booking.getStartTime().toLocalTime();
            LocalTime bookingEnd = booking.getEndTime().toLocalTime();

            // Add break buffer around existing bookings
            LocalTime bufferedStart = bookingStart.minusMinutes(breakMinutes);
            LocalTime bufferedEnd = bookingEnd.plusMinutes(breakMinutes);

            if (timesOverlap(slotStart, slotEnd, bufferedStart, bufferedEnd)) {
                return false;
            }
        }

        return true;
    }

    private boolean timesOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    /**
     * Verify that a specific slot is still available before confirming booking.
     */
    public boolean isSlotStillAvailable(LocalDate date, LocalTime startTime, int totalDurationMinutes) {
        LocalTime endTime = startTime.plusMinutes(totalDurationMinutes);
        BreakConfig breakConfig = breakConfigRepository.findAll().stream().findFirst()
                .orElse(BreakConfig.builder().breakBetweenAppointmentsMinutes(30).build());
        int breakMinutes = breakConfig.getBreakBetweenAppointmentsMinutes();

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        List<Booking> existingBookings = bookingRepository.findBookingsForDay(dayStart, dayEnd);

        return isSlotAvailable(date, startTime, endTime, existingBookings, breakConfig, breakMinutes);
    }
}
