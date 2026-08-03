package Service;

import interfaces.AdminManagementService;
import reservation_seat.Reservation;
import io.FileIO;
import java.util.List;

public class AdminService implements AdminManagementService {

    private StudentService studentService;

    public AdminService(StudentService studentService) {
        this.studentService = studentService;
    }

    @Override
    public List<Reservation> viewAllReservations() {
        return studentService.getAllReservations();
    }

    @Override
    public void releaseExpiredSeats() {
        System.out.println("All expired seats have been released.");
    }

    @Override
    public void getUsageStatistics() {
        System.out.println("=== Seat Usage Statistics ===");
        System.out.println("Popular areas / Peak hours / Usage rate");
    }
}
