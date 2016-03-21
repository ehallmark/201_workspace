package server;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import client.CustomButtonUI;
import client.CustomHorizontalScrollBar;
import client.CustomVerticalScrollBar;
import client.GUIController;

public class ServerWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	AppendDocument doc;
	private JButton startServer;
	private JButton stopServer;
	private Vector<ServerThread> serverThreads;
	private int port;
	private Thread currentConnection;
	private ServerSocket ss;
	private static final String sqlUrl = "jdbc:mysql://localhost:3306/ehallmar_text_editor?useSSL=false";
	private static final String sqlUsername = "root";
	private static final String sqlPassword = "root";

	
	ServerWindow(int port) {
		super("Server");
		serverThreads = new Vector<ServerThread>();
		
		this.port = port;
		
		// Set up GUI
		createServerGUI();
		
	}
	
	void createServerGUI() {
		setLayout(new BorderLayout());
		JTextPane text_area = new JTextPane();
		text_area.setSelectionColor(GUIController.OFFSET_COLOR);
		text_area.setEditable(false);
		JScrollPane scroll_pane = new JScrollPane(text_area);
		scroll_pane.setViewportView(text_area);
		doc = new AppendDocument();
		text_area.setStyledDocument(doc);
		add(scroll_pane, BorderLayout.CENTER);
		
		CustomVerticalScrollBar v_scroll_bar = new CustomVerticalScrollBar();
		v_scroll_bar.setUI(v_scroll_bar.new CustomScrollUI());
		scroll_pane.setVerticalScrollBar(v_scroll_bar);
		scroll_pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		CustomHorizontalScrollBar h_scroll_bar = new CustomHorizontalScrollBar();
		h_scroll_bar.setUI(h_scroll_bar.new CustomScrollUI());
		scroll_pane.setHorizontalScrollBar(h_scroll_bar);
		scroll_pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// add start stop button
		startServer = new JButton("Start");
		startServer.setUI(new CustomButtonUI(startServer));
		add(startServer, BorderLayout.SOUTH);
		startServer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					ServerWindow.this.remove(startServer);
					ServerWindow.this.add(stopServer, BorderLayout.SOUTH);
					ServerWindow.this.revalidate();
					currentConnection = new Thread() {
						
						@Override
						public void run() {
							startServer();
						}
						
					};
					currentConnection.start();

				} catch (Exception e) {
					// Error
					doc.append("Server unable to start on Port:"+port+"\n");

				} 
			}
			
		});
		
		// Stop server button
		stopServer = new JButton("Stop");
		stopServer.setUI(new CustomButtonUI(stopServer));
		stopServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					ServerWindow.this.remove(stopServer);
					ServerWindow.this.add(startServer, BorderLayout.SOUTH);
					ServerWindow.this.revalidate();

				} catch (Exception e) {
					// no window
				} finally {
					closeServer();
				}
			}
			
		});
		
		// Other options
		setSize(600,400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// assignment 3
		initCursor();
		initApplicationIcon();
		this.getContentPane().setBackground(GUIController.BACKGROUND_COLOR);
	}
	
	private void initCursor() {
		// get cursor icon from resources
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image image = toolkit.getImage("img/icon/cursor.png");
		Cursor c = toolkit.createCustomCursor(image , new Point(0, 
				0), "img");
		setCursor(c);
	}

	private void initApplicationIcon() {
		// set custom application icon
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image image = toolkit.getImage("img/icon/office.png");
		setIconImage(image);
	}

	private void closeServer() {
		if(ss!=null) {
			try {
				ss.close();
			} catch (IOException e) {
				// Error closing
			}
		}
		serverThreads.clear();
		
		doc.append("Server stopped.\n");
		
	}
	
	private void startServer() {
		// Add
		try {
			ss = new ServerSocket(port);
			doc.append("Server started on Port:"+port+"\n");
			
		} catch (IOException e) {
			// port in use
			ServerWindow.this.remove(stopServer);
			ServerWindow.this.add(startServer, BorderLayout.SOUTH);
			ServerWindow.this.repaint();
			JOptionPane.showMessageDialog(ServerWindow.this, "Port "+port+" already in use...", "IO Error", JOptionPane.WARNING_MESSAGE);
			return;
		}
		

		try {
			while (true) {
				Socket s = ss.accept();
				ServerThread st = new ServerThread(s, this);
				serverThreads.add(st);
			}
			
		} catch (IOException ioe) {
			// Connection lost

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
		} finally {
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException ioe) {
					// Error closing
				}
			}
		}
	}
	
	public void removeServerThread(ServerThread st) {
		serverThreads.remove(st);
	}
	
	// SQL INTERACTIONS
	
	boolean insertUser(String username, String password) {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(sqlUrl, sqlUsername, sqlPassword);
			  // the mysql insert statement
			String query = " insert into users (username, password)"
					  + " values ('"+username+"','"+password+"')";
			
			PreparedStatement ps = connection.prepareStatement(query);
			ps.execute();

		} catch (Exception e) {
		    return false;
		} finally {
			try {
				if(connection!=null) connection.close();
			} catch (SQLException e) {

			}
		}
		return true;
		
	}

	public boolean findUser(String username, String password) {
		Connection connection = null;
		boolean valid = false;
		try {
			connection = DriverManager.getConnection(sqlUrl, sqlUsername, sqlPassword);
			  // the mysql insert statement
			String query = " select * from users where username='"+username+"' and password='"+password+"' ";
			
			PreparedStatement ps = connection.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			valid = rs.next();

		} catch (Exception e) {
		    return false;
		} finally {
			try {
				if(connection!=null) connection.close();
			} catch (SQLException e) {

			}
		}
		return valid;
		
	}
	
	boolean insertFile(String username, String filename, String filetext) {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(sqlUrl, sqlUsername, sqlPassword);
			  // the mysql insert statement
			String query = " insert into files (username, filename, filetext)"
					  + " values ('"+username+"','"+filename+"','"+filetext+"') ";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.execute();

		} catch (Exception e) {
		    return false;
		} finally {
			try {
				if(connection!=null) connection.close();
			} catch (SQLException e) {

			}
		}
		return true;
		
	}

	public Vector<String> findFiles(String username) {
		Vector<String> files = new Vector<String>();
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(sqlUrl, sqlUsername, sqlPassword);
			  // the mysql insert statement
			String query = " select filename from files where username='"+username+"' ";
			
			PreparedStatement ps = connection.prepareStatement(query);
			ResultSet results = ps.executeQuery();
			while(results.next()) {
				// add filenames to files vector
				files.add(results.getString(1));
			}

		} catch (Exception e) {

		} finally {
			try {
				if(connection!=null) connection.close();
			} catch (SQLException e) {

			}
		}
		return files;
		
	}

	public String getFileText(String username, String filename) {
		String filetext = null;
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(sqlUrl, sqlUsername, sqlPassword);
			  // the mysql insert statement
			String query = " select filetext from files where username='"+username+"' and filename='"+filename+"' ";
			
			PreparedStatement ps = connection.prepareStatement(query);
			ResultSet results = ps.executeQuery();
			results.next(); 
			// add filetext to files vector
			filetext = results.getString(1);
			

		} catch (Exception e) {

		} finally {
			try {
				if(connection!=null) connection.close();
			} catch (SQLException e) {
				
			}
		}
		return filetext;
		
	}
}
