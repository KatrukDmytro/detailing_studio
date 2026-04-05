package detailingstudio.controller;

import detailingstudio.dto.BookingRequest;
import detailingstudio.dto.BookingResponse;
import detailingstudio.model.BookingPhoto;
import detailingstudio.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookingResponse> createBooking(
            @RequestPart("booking") String bookingJson,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {
        try {
            BookingRequest request = objectMapper.readValue(bookingJson, BookingRequest.class);
            BookingResponse response = bookingService.createBooking(request, photos);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process booking: " + e.getMessage(), e);
        }
    }

    @GetMapping("/photos/{photoId}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long photoId) {
        BookingPhoto photo = bookingService.getPhoto(photoId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photo.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + photo.getOriginalName() + "\"")
                .body(photo.getData());
    }
}
