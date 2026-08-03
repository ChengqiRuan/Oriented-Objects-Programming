package users;

public class Admin extends User{
	private String role;
	public Admin(String username, String password) {
		super(username,password);
		this.role="Admin";
	}
	public String getRole() {
		return this.role;
	}
}
