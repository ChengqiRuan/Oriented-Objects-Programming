package Service;

import interfaces.ReservationService;
import users.User;
import users.Student;
import reservation_seat.Seat;
import reservation_seat.Reservation;
import io.FileIO;
import java.util.ArrayList;
import java.util.List;

public class StudentService implements ReservationService {

    private List<Reservation> reservationList;
    private List<Seat> allSeats;

    public StudentService(List<User> users, List<Seat> seats) {
        this.allSeats = seats;
        this.reservationList = FileIO.loadReservations(users, seats);
    }

    @Override
    public boolean reserveSeat(Student student, Seat seat, String time) {
        // check credits
        if (!student.canReserve()) {
            System.out.println("Credit score too low (<80). Cannot reserve.");
            return false;
        }

        // seat status
        if (!seat.isAvailable()) {
            System.out.println("Seat is already occupied!");
            return false;
        }

        // limit
        for (Reservation r : reservationList) {
            if (r.getStudent().equals(student) && r.getStatus().equals("approved")) {
                System.out.println("You can only have ONE active reservation! Please cancel the old one first.");
                return false;
            }
        }

        //reservation
        Reservation newRes = new Reservation(student, seat, time);
        newRes.setStatus("approved");
        reservationList.add(newRes);
        seat.reserve();

        FileIO.saveReservations(reservationList);
        FileIO.saveSeats(allSeats);
        return true;
    }

    // cancel
    @Override
    public boolean cancelReservation(Reservation reservation) {
        if (reservation == null) return false;
        // students
        reservation.setStatus("cancelled");
        // release
        reservation.getSeat().release();

        FileIO.saveReservations(reservationList);
        FileIO.saveSeats(allSeats);
        return true;
    }

    @Override
    public List<Reservation> getMyReservations(Student student) {
        List<Reservation> myList = new ArrayList<>();
        for (Reservation r : reservationList) {
            if (r.getStudent().equals(student)) {
                myList.add(r);
            }
        }
        return myList;
    }

    public List<Reservation> getAllReservations() {
        return reservationList;
    }
}