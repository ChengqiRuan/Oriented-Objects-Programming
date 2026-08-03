package interfaces;
import users.Student;
import reservation_seat.Seat;
import reservation_seat.Reservation;
import java.util.List;

public interface ReservationService {
    boolean reserveSeat(Student student, Seat seat, String time);
    boolean cancelReservation(Reservation reservation);
    List<Reservation> getMyReservations(Student student);
}
