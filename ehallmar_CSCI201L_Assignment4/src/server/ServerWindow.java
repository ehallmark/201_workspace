package server;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.SimpleAttributeSet;

import client.CustomButtonUI;
import client.CustomHorizontalScrollBar;
import client.CustomVerticalScrollBar;
import client.GUIController;

public class ServerWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private AppendDocument doc;
	private JButton startServer;
	private JButton stopServer;
	private int port;
	private String host;
	
	ServerWindow(int port, String host) {
		super("Server");
		this.port = port;
		this.host = host;

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
					doc.append("Server started on Port:"+port+"\n");
				} catch (Exception e) {
					// no window
				} finally {
					startServerConnection();
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
					doc.append("Server stopped.\n");

				} catch (Exception e) {
					// no window
				} finally {
					closeServerConnection();
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

	private void closeServerConnection() {
		// ADDDD
	}
	
	private void startServerConnection() {
		// ADD
	}

}
