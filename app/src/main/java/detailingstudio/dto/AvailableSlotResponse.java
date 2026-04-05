package detailingstudio.dto;

import lombok.*;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailableSlotResponse {
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;
}
