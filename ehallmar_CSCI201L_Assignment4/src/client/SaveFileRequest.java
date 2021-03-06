package client;

import java.io.Serializable;

public class SaveFileRequest implements Serializable {
	public static final long serialVersionUID = 1;
	public String username; 
	
	public SaveFileRequest(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	@Override
	public String toString() {
		return "User:"+username;
	}

}

