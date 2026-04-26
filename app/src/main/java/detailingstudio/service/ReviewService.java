package detailingstudio.service;

import detailingstudio.dto.ReviewRequest;
import detailingstudio.dto.ReviewResponse;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class ReviewService {
    public List<ReviewResponse> getAllReviews() {
        return new ArrayList<>();
    }
    
    public ReviewResponse createReview(ReviewRequest request) {
        return null;
    }
}
