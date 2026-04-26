package detailingstudio.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReviewResponse {
    private Long id;
    private String authorName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
    private List<String> photoUrls;
}
