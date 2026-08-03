package reservation_seat;
import users.Student;
public class Reservation {
	private Student student;
	private Seat seat;
	private String time;
	private String status;
	public Reservation(Student student, Seat seat, String time) {
		this.student=student;
		this.seat = seat;
		this.time = time;
		this.status="valid";
	}
	public void cancel() {
		this.status = "canceled";
		this.seat.release();
	}
	public Student getStudent() {
		return this.student;
	}
	public Seat getSeat() {
		return this.seat;
	}
	public String getTime() {
		return this.time;
	}
	public String getStatus() {
		return this.status;
	}
	public void setStatus(String status) {
		this.status= status;
	}
}
