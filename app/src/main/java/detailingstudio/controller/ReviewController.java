package detailingstudio.controller;

import detailingstudio.dto.ReviewRequest;
import detailingstudio.dto.ReviewResponse;
import detailingstudio.model.ReviewPhoto;
import detailingstudio.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> createReview(
            @RequestPart("review") String reviewJson,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {
        try {
            ReviewRequest request = objectMapper.readValue(reviewJson, ReviewRequest.class);
            ReviewResponse response = reviewService.createReview(request, photos);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process review: " + e.getMessage(), e);
        }
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/photos/{photoId}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long photoId) {
        ReviewPhoto photo = reviewService.getPhoto(photoId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photo.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + photo.getOriginalName() + "\"")
                .body(photo.getData());
    }
}
