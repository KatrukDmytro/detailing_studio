package detailingstudio.service;

import detailingstudio.dto.ReviewRequest;
import detailingstudio.dto.ReviewResponse;
import detailingstudio.model.Review;
import detailingstudio.model.ReviewPhoto;
import detailingstudio.repository.ReviewRepository;
import detailingstudio.repository.ReviewPhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;

    @Transactional
    public ReviewResponse createReview(ReviewRequest request, List<MultipartFile> photos) {
        Review review = new Review();
        review.setAuthorName(request.getAuthorName());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        
        if (photos != null && !photos.isEmpty()) {
            for (MultipartFile file : photos) {
                try {
                    ReviewPhoto photo = new ReviewPhoto();
                    photo.setOriginalName(file.getOriginalFilename());
                    photo.setContentType(file.getContentType());
                    photo.setData(file.getBytes());
                    photo.setReview(review);
                    review.getPhotos().add(photo);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to process photo", e);
                }
            }
        }
        
        Review savedReview = reviewRepository.save(review);
        return mapToResponse(savedReview);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReviewPhoto getPhoto(Long photoId) {
        return reviewPhotoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found"));
    }

    private ReviewResponse mapToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setAuthorName(review.getAuthorName());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        if (review.getPhotos() != null) {
            response.setPhotoIds(review.getPhotos().stream().map(ReviewPhoto::getId).collect(Collectors.toList()));
        } else {
            response.setPhotoIds(new ArrayList<>());
        }
        return response;
    }
}
