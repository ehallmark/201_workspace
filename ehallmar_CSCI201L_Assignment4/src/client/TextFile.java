package client;

import java.io.Serializable;

public class TextFile implements Serializable {
	private static final long serialVersionUID = 1L;
	private String username;
	private String filename;
	private String filetext;
	
	public TextFile(String username, String filename, String filetext) {
		this.username = username;
		this.filename = filename;
		this.filetext = filetext;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public String getFiletext() {
		return filetext;
	}
	
	@Override
	public String toString() {
		return "User:"+username+" File:"+filename;
	}
}
