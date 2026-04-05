package detailingstudio.repository;

import detailingstudio.model.BookingPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingPhotoRepository extends JpaRepository<BookingPhoto, Long> {
    List<BookingPhoto> findByBookingId(Long bookingId);
}
