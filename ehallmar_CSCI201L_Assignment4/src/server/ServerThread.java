package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

import client.SignupRequest;

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
						server.doc.append("Sign-up attempt "+message.toString()+"\n");
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
