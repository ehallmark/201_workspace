package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import client.FileRequest;
import client.LoginRequest;
import client.OpenFileRequest;
import client.SaveFileRequest;
import client.SignupRequest;
import client.TextFile;

public class ServerThread extends Thread {

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private ServerWindow server;
	
	public ServerThread(Socket s, ServerWindow server) throws NoSuchAlgorithmException {
		this.server = server;
		try {
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());

		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
		this.start();
	}
	
	public void sendMessage(Object message) throws IOException {
		oos.writeObject(message);
		oos.flush();		
	}
	
	public void run() {
		try {
			while(true) {
				Object message = ois.readObject();
				if(message != null) {
					if(message instanceof SignupRequest) {
						server.doc.append("Sign-up attempt "+message.toString());
						String decryptedPassword = decrypt(((SignupRequest) message).getEncryptedPassword());
						boolean valid = server.insertUser(((SignupRequest) message).getUsername(), decryptedPassword);
						
						if(valid) {
							server.doc.append("Sign-up success User:"+((SignupRequest) message).getUsername());
							// Sign in user
							sendMessage(new ServerResponse(true));
						} else {
							server.doc.append("Sign-up failure User:"+((SignupRequest) message).getUsername());
							// Alert user of error
							sendMessage(new ServerResponse(false));
						}
					} else if(message instanceof LoginRequest) {
						server.doc.append("Sign-up attempt "+message.toString());
						String decryptedPassword = decrypt(((LoginRequest) message).getEncryptedPassword());
						boolean valid = server.findUser(((LoginRequest) message).getUsername(), decryptedPassword);
						
						if(valid) {
							server.doc.append("Log-in success User:"+((LoginRequest) message).getUsername());
							// Sign in user
							sendMessage(new ServerResponse(true));
						} else {
							server.doc.append("Log-in failure User:"+((LoginRequest) message).getUsername());
							// Alert user of error
							sendMessage(new ServerResponse(false));
						}
					} else if(message instanceof OpenFileRequest) {						
						Vector<String> files = server.findFiles(((OpenFileRequest) message).getUsername());
						
						// send file names to user
						sendMessage(new FileResponse(files));

					} else if(message instanceof SaveFileRequest) {						
						Vector<String> files = server.findFiles(((SaveFileRequest) message).getUsername());
						
						// send file names to user
						sendMessage(new FileResponse(files));

					} else if (message instanceof TextFile) {
						TextFile tf = (TextFile) message;
						String username = tf.getUsername();
						String filename = tf.getFilename();
						String filetext = tf.getFiletext();
						boolean valid = server.insertFile(username, filename, filetext);
						if(valid) {
							server.doc.append("File saved "+message.toString());
						} else {
							server.doc.append("File failed to save "+message.toString());
						}
						sendMessage(new ServerResponse(valid));
					
					} else if (message instanceof FileRequest) {
						FileRequest fr = (FileRequest) message;
						String username = fr.getUsername();
						String filename = fr.getFilename();
						String filetext = server.getFileText(username, filename);
						if(filetext != null) {
							server.doc.append("File opened "+message.toString());
						} else {
							server.doc.append("File failed to open "+message.toString());
						}
						sendMessage(new TextFile(username,filename,filetext));
					
					}
				}
			}
		} catch (IOException ioe) {
			System.out.println("ioe in run: " + ioe.getMessage()); 
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			server.removeServerThread(this);
			System.out.println("END OF RUNNING SERVER THREAD");
		}
	}
	
	public String decrypt(String str) {
       return str;
	}
}
