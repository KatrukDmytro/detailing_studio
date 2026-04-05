package detailingstudio.dto;

import detailingstudio.model.BookingStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Long id;
    private String customerPhone;
    private String customerEmail;
    private String whatsapp;
    private String instagram;
    private String address;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalDurationMinutes;
    private BookingStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private List<ServiceTypeDto> services;
    private List<PhotoInfo> photos;
    private boolean hasPhotos;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PhotoInfo {
        private Long id;
        private String originalName;
        private String contentType;
    }
}
