package detailingstudio.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer rating;

    @NotBlank(message = "Review text is required")
    @Size(max = 2000, message = "Review text is too long")
    private String comment;

    private List<String> photoUrls;
}
