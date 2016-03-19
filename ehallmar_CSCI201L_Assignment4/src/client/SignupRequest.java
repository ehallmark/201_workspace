package client;

import java.io.Serializable;

public class SignupRequest implements Serializable {
	public static final long serialVersionUID = 1;
	public String username; 
	public String password;

	
	public SignupRequest(String username,String password) {
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getEncryptedPassword() {
		return password;
	}
	
	@Override
	public String toString() {
		return "User:"+username+" Pass:"+password;
	}

}

