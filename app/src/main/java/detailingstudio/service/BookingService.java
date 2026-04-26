package detailingstudio.service;

import detailingstudio.dto.BookingResponse;
import detailingstudio.model.BookingStatus;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class BookingService {
    public List<BookingResponse> getAllBookings() { 
        return new ArrayList<>(); 
    }
    
    public BookingResponse getBookingById(Long id) { 
        return null; 
    }
    
    public BookingResponse updateBookingStatus(Long id, BookingStatus status) { 
        return null; 
    }
    
    public void deleteBooking(Long id) {
    }
}
