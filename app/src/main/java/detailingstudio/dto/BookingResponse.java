package detailingstudio.dto;

import detailingstudio.model.BookingStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String customerPhone;
    private String customerEmail;
    private String address;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;
    private String notes;
    private List<String> photoUrls;
    private List<String> serviceNames;
}
