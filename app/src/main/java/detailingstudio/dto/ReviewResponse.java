package detailingstudio.dto;

import lombok.Data;
import java.util.List;
import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long id;
    private String authorName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
    private List<Long> photoIds;
}
