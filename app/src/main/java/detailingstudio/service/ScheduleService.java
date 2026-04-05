package detailingstudio.service;

import detailingstudio.dto.*;
import detailingstudio.model.*;
import detailingstudio.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final BreakConfigRepository breakConfigRepository;
    private final ServiceTypeRepository serviceTypeRepository;

    // ---- Work Schedule ----

    public List<ScheduleDto> getAllSchedules() {
        return workScheduleRepository.findAll().stream()
                .map(this::mapScheduleToDto)
                .sorted(Comparator.comparing(s -> s.getDayOfWeek().getValue()))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ScheduleDto> updateSchedules(List<ScheduleDto> schedules) {
        for (ScheduleDto dto : schedules) {
            WorkSchedule schedule = workScheduleRepository.findByDayOfWeek(dto.getDayOfWeek())
                    .orElse(WorkSchedule.builder().dayOfWeek(dto.getDayOfWeek()).build());
            schedule.setStartTime(dto.getStartTime());
            schedule.setEndTime(dto.getEndTime());
            schedule.setIsWorkingDay(dto.getIsWorkingDay());
            workScheduleRepository.save(schedule);
        }
        return getAllSchedules();
    }

    public boolean isWorkingDay(DayOfWeek dayOfWeek) {
        return workScheduleRepository.findByDayOfWeek(dayOfWeek)
                .map(WorkSchedule::getIsWorkingDay)
                .orElse(false);
    }

    // ---- Break Config ----

    public BreakConfigDto getBreakConfig() {
        BreakConfig config = breakConfigRepository.findAll().stream().findFirst()
                .orElse(BreakConfig.builder().breakBetweenAppointmentsMinutes(30).build());
        return mapBreakToDto(config);
    }

    @Transactional
    public BreakConfigDto updateBreakConfig(BreakConfigDto dto) {
        BreakConfig config = breakConfigRepository.findAll().stream().findFirst()
                .orElse(new BreakConfig());
        config.setBreakBetweenAppointmentsMinutes(dto.getBreakBetweenAppointmentsMinutes());
        config.setLunchStart(dto.getLunchStart());
        config.setLunchEnd(dto.getLunchEnd());
        config = breakConfigRepository.save(config);
        return mapBreakToDto(config);
    }

    // ---- Service Types ----

    public List<ServiceTypeDto> getActiveServices() {
        return serviceTypeRepository.findByActiveTrueOrderByDisplayOrderAsc().stream()
                .map(this::mapServiceToDto)
                .collect(Collectors.toList());
    }

    public List<ServiceTypeDto> getAllServices() {
        return serviceTypeRepository.findAll().stream()
                .map(this::mapServiceToDto)
                .sorted(Comparator.comparing(ServiceTypeDto::getDisplayOrder))
                .collect(Collectors.toList());
    }

    @Transactional
    public ServiceTypeDto createService(ServiceTypeDto dto) {
        ServiceType service = ServiceType.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .durationMinutes(dto.getDurationMinutes())
                .price(dto.getPrice())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                .icon(dto.getIcon())
                .build();
        return mapServiceToDto(serviceTypeRepository.save(service));
    }

    @Transactional
    public ServiceTypeDto updateService(Long id, ServiceTypeDto dto) {
        ServiceType service = serviceTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service not found: " + id));
        service.setName(dto.getName());
        service.setDescription(dto.getDescription());
        service.setDurationMinutes(dto.getDurationMinutes());
        service.setPrice(dto.getPrice());
        if (dto.getActive() != null) service.setActive(dto.getActive());
        if (dto.getDisplayOrder() != null) service.setDisplayOrder(dto.getDisplayOrder());
        if (dto.getIcon() != null) service.setIcon(dto.getIcon());
        return mapServiceToDto(serviceTypeRepository.save(service));
    }

    @Transactional
    public void deleteService(Long id) {
        ServiceType service = serviceTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service not found: " + id));
        service.setActive(false);
        serviceTypeRepository.save(service);
    }

    // ---- Init default schedule ----

    @Transactional
    public void initDefaultScheduleIfEmpty() {
        if (workScheduleRepository.count() == 0) {
            for (DayOfWeek day : DayOfWeek.values()) {
                boolean isWorking = day != DayOfWeek.SUNDAY;
                workScheduleRepository.save(WorkSchedule.builder()
                        .dayOfWeek(day)
                        .startTime(LocalTime.of(8, 0))
                        .endTime(LocalTime.of(18, 0))
                        .isWorkingDay(isWorking)
                        .build());
            }
        }
        if (breakConfigRepository.count() == 0) {
            breakConfigRepository.save(BreakConfig.builder()
                    .breakBetweenAppointmentsMinutes(30)
                    .lunchStart(LocalTime.of(12, 0))
                    .lunchEnd(LocalTime.of(13, 0))
                    .build());
        }
    }

    // ---- Mappers ----

    private ScheduleDto mapScheduleToDto(WorkSchedule ws) {
        return ScheduleDto.builder()
                .id(ws.getId())
                .dayOfWeek(ws.getDayOfWeek())
                .startTime(ws.getStartTime())
                .endTime(ws.getEndTime())
                .isWorkingDay(ws.getIsWorkingDay())
                .build();
    }

    private BreakConfigDto mapBreakToDto(BreakConfig bc) {
        return BreakConfigDto.builder()
                .id(bc.getId())
                .breakBetweenAppointmentsMinutes(bc.getBreakBetweenAppointmentsMinutes())
                .lunchStart(bc.getLunchStart())
                .lunchEnd(bc.getLunchEnd())
                .build();
    }

    private ServiceTypeDto mapServiceToDto(ServiceType st) {
        return ServiceTypeDto.builder()
                .id(st.getId())
                .name(st.getName())
                .description(st.getDescription())
                .durationMinutes(st.getDurationMinutes())
                .price(st.getPrice())
                .active(st.getActive())
                .displayOrder(st.getDisplayOrder())
                .icon(st.getIcon())
                .build();
    }
}
