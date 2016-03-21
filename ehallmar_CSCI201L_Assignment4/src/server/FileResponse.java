package server;

import java.io.Serializable;
import java.util.Vector;

public class FileResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	private Vector<String> files;
	
	FileResponse(Vector<String> files) {
		this.files = files;
	}
	
	public Vector<String> getFiles() {
		return files;
	}
}
