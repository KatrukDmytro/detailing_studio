package detailingstudio.service;

import detailingstudio.dto.*;
import detailingstudio.model.*;
import detailingstudio.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final BookingPhotoRepository bookingPhotoRepository;
    private final SlotCalculationService slotCalculationService;

    @Transactional
    public BookingResponse createBooking(BookingRequest request, List<MultipartFile> photos) {
        // Fetch selected services
        Set<ServiceType> services = new HashSet<>(serviceTypeRepository.findAllById(request.getServiceTypeIds()));
        if (services.isEmpty()) {
            throw new IllegalArgumentException("At least one valid service must be selected");
        }

        // Calculate total duration
        int totalDuration = services.stream()
                .mapToInt(ServiceType::getDurationMinutes)
                .sum();

        // Verify slot availability
        LocalDate date = request.getStartTime().toLocalDate();
        if (!slotCalculationService.isSlotStillAvailable(date,
                request.getStartTime().toLocalTime(), totalDuration)) {
            throw new IllegalStateException("Selected time slot is no longer available");
        }

        // Build booking
        Booking booking = Booking.builder()
                .customerPhone(request.getCustomerPhone())
                .customerEmail(request.getCustomerEmail())
                .whatsapp(request.getWhatsapp())
                .instagram(request.getInstagram())
                .address(request.getAddress())
                .startTime(request.getStartTime())
                .endTime(request.getStartTime().plusMinutes(totalDuration))
                .totalDurationMinutes(totalDuration)
                .status(BookingStatus.PENDING)
                .notes(request.getNotes())
                .serviceTypes(services)
                .build();

        booking = bookingRepository.save(booking);

        // Save photos to DB
        if (photos != null && !photos.isEmpty()) {
            for (MultipartFile file : photos) {
                if (!file.isEmpty()) {
                    try {
                        BookingPhoto photo = BookingPhoto.builder()
                                .originalName(file.getOriginalFilename())
                                .contentType(file.getContentType())
                                .data(file.getBytes())
                                .booking(booking)
                                .build();
                        bookingPhotoRepository.save(photo);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to save photo: " + file.getOriginalFilename(), e);
                    }
                }
            }
        }

        return mapToResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + id));
        return mapToResponse(booking);
    }

    @Transactional
    public BookingResponse updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + id));
        booking.setStatus(status);
        return mapToResponse(bookingRepository.save(booking));
    }

    @Transactional
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    public byte[] getPhotoData(Long photoId) {
        BookingPhoto photo = bookingPhotoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found: " + photoId));
        return photo.getData();
    }

    @Transactional(readOnly = true)
    public BookingPhoto getPhoto(Long photoId) {
        return bookingPhotoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found: " + photoId));
    }

    private BookingResponse mapToResponse(Booking booking) {
        List<BookingResponse.PhotoInfo> photoInfos = bookingPhotoRepository.findByBookingId(booking.getId())
                .stream()
                .map(p -> BookingResponse.PhotoInfo.builder()
                        .id(p.getId())
                        .originalName(p.getOriginalName())
                        .contentType(p.getContentType())
                        .build())
                .collect(Collectors.toList());

        return BookingResponse.builder()
                .id(booking.getId())
                .customerPhone(booking.getCustomerPhone())
                .customerEmail(booking.getCustomerEmail())
                .whatsapp(booking.getWhatsapp())
                .instagram(booking.getInstagram())
                .address(booking.getAddress())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .totalDurationMinutes(booking.getTotalDurationMinutes())
                .status(booking.getStatus())
                .notes(booking.getNotes())
                .createdAt(booking.getCreatedAt())
                .services(booking.getServiceTypes().stream()
                        .map(s -> ServiceTypeDto.builder()
                                .id(s.getId())
                                .name(s.getName())
                                .description(s.getDescription())
                                .durationMinutes(s.getDurationMinutes())
                                .price(s.getPrice())
                                .build())
                        .collect(Collectors.toList()))
                .photos(photoInfos)
                .hasPhotos(!photoInfos.isEmpty())
                .build();
    }
}
