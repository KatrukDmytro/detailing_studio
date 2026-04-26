package detailingstudio.config;

import detailingstudio.model.ServiceType;
import detailingstudio.model.User;
import detailingstudio.repository.ServiceTypeRepository;
import detailingstudio.repository.UserRepository;
import detailingstudio.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ScheduleService scheduleService;
    private final ServiceTypeRepository serviceTypeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        log.info("Starting data initialization...");
        scheduleService.initDefaultScheduleIfEmpty();
        initDefaultServicesIfEmpty();
        initDefaultAdminIfEmpty();
        log.info("Data initialization completed.");
    }

    private void initDefaultAdminIfEmpty() {
        if (userRepository.count() == 0) {
            log.info("No users found. Creating default admin: {}", adminUsername);
            userRepository.save(User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .role("ADMIN")
                    .build());
            log.info("Default admin created successfully.");
        } else {
            log.info("Users already exist. Skipping admin creation.");
        }
    }

    private void initDefaultServicesIfEmpty() {
        if (serviceTypeRepository.count() == 0) {
            serviceTypeRepository.save(ServiceType.builder()
                    .name("Exterior Polishing")
                    .description("Professional paint correction and polishing to restore your car's showroom shine. Removes swirl marks, minor scratches, and oxidation.")
                    .durationMinutes(180)
                    .price(new BigDecimal("250.00"))
                    .displayOrder(1)
                    .icon("sparkles")
                    .build());

            serviceTypeRepository.save(ServiceType.builder()
                    .name("Interior Cleaning")
                    .description("Deep cleaning of all interior surfaces including dashboard, seats, carpets, and door panels. Includes vacuuming and surface treatment.")
                    .durationMinutes(120)
                    .price(new BigDecimal("150.00"))
                    .displayOrder(2)
                    .icon("spray-can")
                    .build());

            serviceTypeRepository.save(ServiceType.builder()
                    .name("Interior Dry Cleaning")
                    .description("Professional chemical dry cleaning of upholstery, carpets, and fabric surfaces. Removes deep stains, odors, and allergens.")
                    .durationMinutes(150)
                    .price(new BigDecimal("200.00"))
                    .displayOrder(3)
                    .icon("wind")
                    .build());

            serviceTypeRepository.save(ServiceType.builder()
                    .name("Exterior Restoration")
                    .description("Complete exterior restoration including paint decontamination, clay bar treatment, multi-stage polishing, and ceramic coating application.")
                    .durationMinutes(300)
                    .price(new BigDecimal("450.00"))
                    .displayOrder(4)
                    .icon("shield")
                    .build());

            serviceTypeRepository.save(ServiceType.builder()
                    .name("Interior Restoration")
                    .description("Full interior restoration including leather conditioning, plastic restoration, headliner cleaning, and UV protection treatment.")
                    .durationMinutes(240)
                    .price(new BigDecimal("350.00"))
                    .displayOrder(5)
                    .icon("car")
                    .build());

            serviceTypeRepository.save(ServiceType.builder()
                    .name("Full Detail Package")
                    .description("The ultimate detailing experience combining exterior polishing, interior deep clean, and ceramic protection. Complete transformation of your vehicle.")
                    .durationMinutes(480)
                    .price(new BigDecimal("650.00"))
                    .displayOrder(6)
                    .icon("crown")
                    .build());
        }
    }
}
