package model;

public class  User {
	private String username;
	private String preferedName;
	private String password;

	public User() {
	}
	
	public User(String username, String preferedName, String password) {
		this.username = username;
		this.preferedName = preferedName;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPreferedName() {
		return preferedName;
	}

	public void setPreferedName(String preferedName) {
		this.preferedName = preferedName;
	}
}
