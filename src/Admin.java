

public class Admin {
	private String Username;
	@Override
	public String toString() {
		return "Admin [Username=" + Username + ", Password=" + Password + "]";
	}
	private String Password;
	public String getUsername() {
		return Username;
	}
	public void setUsername(String username) {
		Username = username;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}

}
