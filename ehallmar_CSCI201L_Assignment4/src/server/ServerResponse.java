package server;

import java.io.Serializable;

public class ServerResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean success;
	
	ServerResponse(boolean success) {
		this.success = success;
	}
	
	public boolean wasSuccessful() {
		return success;
	}
}
