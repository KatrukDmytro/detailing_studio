package detailingstudio.repository;

import detailingstudio.model.Booking;
import detailingstudio.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.startTime >= :dayStart AND b.startTime < :dayEnd AND b.status <> 'CANCELLED' ORDER BY b.startTime")
    List<Booking> findBookingsForDay(@Param("dayStart") LocalDateTime dayStart, @Param("dayEnd") LocalDateTime dayEnd);

    List<Booking> findByStatusOrderByStartTimeAsc(BookingStatus status);

    @Query("SELECT b FROM Booking b ORDER BY b.createdAt DESC")
    List<Booking> findAllOrderByCreatedAtDesc();
}
