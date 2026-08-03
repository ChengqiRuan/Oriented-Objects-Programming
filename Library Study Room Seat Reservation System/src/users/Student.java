package users;

public class Student extends User{
	private int creditScore;
	private String role;
	public Student(String username, String password) {
		super(username,password);
		this.creditScore = 100;
		this.role = "Student";
	}
	public boolean canReserve() {
		if (this.creditScore<80) {
			return false;
		}
		else return true;
	}
	public int getCreditScore() {
		return this.creditScore;
	}
	public void setCreditScore(int creditScore) {
		this.creditScore=creditScore;
	}
	public String getRole() {
		return this.role;
	}
}
	
