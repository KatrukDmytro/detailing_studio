import re

app_yaml = """spring:
  application:
    name: DetailingStudio

  # PostgreSQL configuration
  datasource:
    url: jdbc:postgresql:///detailing_studio?cloudSqlInstance=project-f0c6dffa-7d80-4aef-9b0:europe-west3:a767891abrisa&socketFactory=com.google.cloud.sql.postgres.SocketFactory
    username: postgres
    password: numb151515!A
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true

  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB

  # Spring Security
  security:
    user:
      name: admin
      password: brisa2026

server:
  port: 8080

# Application custom properties
app:
  admin:
    username: admin
    password: brisa2026
  cors:
    allowed-origins: http://localhost:5173,http://localhost:3000,https://detailing-frontend-518542558773.europe-west3.run.app
  s3:
    endpoint: ${R2_ENDPOINT:https://b570f853bda76e7e510fbe098d826a55.r2.cloudflarestorage.com}
    bucket: ${R2_BUCKET:detailing-studio}
    access-key: ${R2_ACCESS_KEY:your_access_key}
    secret-key: ${R2_SECRET_KEY:your_secret_key}
    region: ${R2_REGION:auto}
    public-url-prefix: ${R2_PUBLIC_URL:https://pub-your_pub_id.r2.dev}

---
# H2 profile for development without PostgreSQL
spring:
  config:
    activate:
      on-profile: h2
  datasource:
    url: jdbc:h2:file:./data/detailing_studio;DB_CLOSE_DELAY=-1
    username: sa
    password:
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
  h2:
    console:
      enabled: true
      path: /h2-console
"""

booking_photo = """package detailingstudio.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "booking_photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String photoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
}
"""

booking_request = """package detailingstudio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {

    @NotBlank(message = "Phone number is required")
    private String customerPhone;

    private String customerEmail;

    private String whatsapp;

    private String instagram;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotEmpty(message = "At least one service must be selected")
    private List<Long> serviceTypeIds;

    private String notes;

    private List<String> photoUrls;
}
"""

with open('/home/unit/Projects/detailing_studio/app/src/main/resources/application.yml', 'w') as f:
    f.write(app_yaml)

with open('/home/unit/Projects/detailing_studio/app/src/main/java/detailingstudio/model/BookingPhoto.java', 'w') as f:
    f.write(booking_photo)

with open('/home/unit/Projects/detailing_studio/app/src/main/java/detailingstudio/dto/BookingRequest.java', 'w') as f:
    f.write(booking_request)

print("Files restored successfully.")
