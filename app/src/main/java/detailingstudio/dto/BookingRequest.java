package detailingstudio.dto;

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
