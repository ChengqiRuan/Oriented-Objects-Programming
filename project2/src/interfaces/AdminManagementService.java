package interfaces;
import reservation_seat.Reservation;
import java.util.List;

public interface AdminManagementService {
    List<Reservation> viewAllReservations();
    void releaseExpiredSeats();
    void getUsageStatistics();
}
