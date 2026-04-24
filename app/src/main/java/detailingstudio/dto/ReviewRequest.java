package detailingstudio.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private String authorName;
    private int rating;
    private String comment;
}
