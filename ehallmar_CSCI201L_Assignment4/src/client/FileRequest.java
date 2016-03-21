package client;

import java.io.Serializable;

public class FileRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private String username;
	private String filename;
	
	FileRequest(String username, String filename) {
		this.username = username;
		this.filename = filename;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getFilename() {
		return filename;
	}
	
	
	@Override
	public String toString() {
		return "User:"+username+" File:"+filename;
	}
}
