package detailingstudio.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "break_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BreakConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Builder.Default
    private Integer breakBetweenAppointmentsMinutes = 30;

    private LocalTime lunchStart;

    private LocalTime lunchEnd;
}
