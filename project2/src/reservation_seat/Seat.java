package reservation_seat;

public class Seat {
	private int seatID;
	private int floor;
	private String area;
	private boolean status;
	public Seat(int seatID, int floor, String area) {
		this.seatID=seatID;
		this.floor = floor;
		this.area = area;
		this.status= true;
	}
	public boolean isAvailable() {
		return status;
	}
	public void reserve() {
		this.status=false;
	}
	public void release() {
		this.status=true;
	}
	public int getSeatID() {
		return this.seatID;
	}
	public int getFloor() {
		return this.floor;
	}
	public String getArea() {
		return this.area;
	}
	public boolean getStatus() {
		return this.status;
	}
}
