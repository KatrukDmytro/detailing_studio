package detailingstudio.dto;

import lombok.*;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BreakConfigDto {
    private Long id;
    private Integer breakBetweenAppointmentsMinutes;
    private LocalTime lunchStart;
    private LocalTime lunchEnd;
}
