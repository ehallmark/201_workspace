package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class FirstWindow extends JPanel {
	private static final long serialVersionUID = 1L;
	private String name = "Evan's Text Editor";
	private Font font;
	private SignupPanel signupPanel;
	private LoginPanel loginPanel;
	private DefaultPanel defaultPanel;
	private JPanel panelHolder;
	private GUIController gui;
	
	
	FirstWindow(GUIController gui) {
		this.gui = gui;
		setLayout(new GridLayout(2,1));
		font = GUIController.DEFAULT_FONT.deriveFont(22f);

		
		setForeground(Color.WHITE);
		setBackground(Color.GRAY);
		
		signupPanel = new SignupPanel();
		loginPanel = new LoginPanel();
		defaultPanel = new DefaultPanel();
		
		panelHolder = new JPanel(new BorderLayout());
		panelHolder.setBackground(Color.GRAY);
		add(new LogoPanel());
		add(panelHolder);
		panelHolder.add(defaultPanel);
		
	}
	
	private class LogoPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		public void paintComponent(Graphics g) {	
			// Draw original stuff
			super.paintComponent(g);
			// draw background
			g.setColor(Color.GRAY);
			g.fillRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
			// draw logo
			g.setColor(GUIController.LOGO_COLOR);
			g.setFont(font);
			FontMetrics fm = g.getFontMetrics();
			g.drawString(name, (this.getWidth()-fm.stringWidth(name))/2, (int) ((this.getHeight()-fm.getHeight())/1.5));
		
		}
	}
	
	
	private class DefaultPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private JButton loginButton;
		private JButton signupButton;
		private JButton offlineButton;
		
		DefaultPanel() {
			setBackground(Color.GRAY);
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			JPanel topPanel = new JPanel();
			BoxLayout btnBl = new BoxLayout(topPanel, BoxLayout.LINE_AXIS);
			topPanel.setLayout(btnBl);
			topPanel.setBackground(Color.GRAY);
			// User buttons
			loginButton = new JButton("Login");
			loginButton.setUI(new CustomButtonUI(loginButton));
			loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			signupButton = new JButton("Signup");
			signupButton.setUI(new CustomButtonUI(signupButton));
			signupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			offlineButton = new JButton("Offline");
			offlineButton.setUI(new CustomButtonUI(offlineButton));
			offlineButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			loginButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent ae) {
					panelHolder.remove(defaultPanel);
					panelHolder.add(loginPanel);
					panelHolder.revalidate();
				}
				
			});
			
			signupButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent ae) {
					panelHolder.remove(defaultPanel);
					panelHolder.add(signupPanel);
					panelHolder.revalidate();
				}
				
			});

			// add login signup and offline buttons
			
			topPanel.add(loginButton);
			topPanel.add(Box.createRigidArea(new Dimension(6,6)));
			topPanel.add(signupButton);
			
			add(topPanel);
			add(Box.createRigidArea(new Dimension(6,6)));
			add(offlineButton);

		}
		
	}
	
	private class SignupPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		
		JTextField username;
		JPasswordField password;
		JPasswordField passwordConfirmation;
		JButton login;
		
		SignupPanel() {
			setBackground(Color.GRAY);
			username = new JTextField();
			username.setPreferredSize(new Dimension(200,22));
			username.setAlignmentX(Component.RIGHT_ALIGNMENT);
			password = new JPasswordField();
			password.setPreferredSize(new Dimension(200,22));
			password.setAlignmentX(Component.RIGHT_ALIGNMENT);
			passwordConfirmation = new JPasswordField();
			passwordConfirmation.setPreferredSize(new Dimension(200,22));
			passwordConfirmation.setAlignmentX(Component.RIGHT_ALIGNMENT);
			
			//gl.setVgap(4);
			JPanel grid = new JPanel();
			grid.setLayout(new BoxLayout(grid,BoxLayout.PAGE_AXIS));

			grid.setAlignmentX(Component.CENTER_ALIGNMENT);
			grid.setBackground(Color.GRAY);
			JPanel firstRow = new JPanel();
			firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.LINE_AXIS));
			firstRow.setBackground(Color.GRAY);
			firstRow.add(new JLabel("Username: "));
			firstRow.add(username);
			firstRow.setAlignmentX(Component.CENTER_ALIGNMENT);

			JPanel secondRow = new JPanel();
			secondRow.setLayout(new BoxLayout(secondRow, BoxLayout.LINE_AXIS));
			secondRow.setBackground(Color.GRAY);
			secondRow.add(new JLabel("Password: "));
			secondRow.add(password);
			secondRow.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			JPanel thirdRow = new JPanel();
			thirdRow.setLayout(new BoxLayout(thirdRow, BoxLayout.LINE_AXIS));
			thirdRow.setBackground(Color.GRAY);
			thirdRow.add(new JLabel("    Repeat:    "));
			thirdRow.add(passwordConfirmation);
			thirdRow.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			grid.add(firstRow);
			grid.add(Box.createRigidArea(new Dimension(5,5)));
			grid.add(secondRow);
			grid.add(Box.createRigidArea(new Dimension(5,5)));
			grid.add(thirdRow);
			grid.add(Box.createRigidArea(new Dimension(5,5)));
			login = new JButton("Login");
			login.setAlignmentX(Component.CENTER_ALIGNMENT);
			login.setUI(new CustomButtonUI(login));
			
			JPanel btnHolder = new JPanel();
			btnHolder.setBackground(Color.GRAY);
			btnHolder.add(login);
			grid.add(btnHolder);
			
			// Button actions
			login.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent ae) {
					String user = username.getText();
					String pass = String.copyValueOf(password.getPassword());
					String passConf = String.copyValueOf(passwordConfirmation.getPassword());
					boolean isValid = true;
					if(user == null || user.trim().length() == 0) {
						JOptionPane.showMessageDialog(FirstWindow.this, "Please include a username",
								"Sign-up Failed", JOptionPane.WARNING_MESSAGE);
						return;						
					}
					
					// validate username
					if(user.replaceAll("[a-zA-Z0-9]", "").length() > 0) {
						JOptionPane.showMessageDialog(FirstWindow.this, "Username must contain only:\nAlphanumeric characters",
								"Sign-up Failed", JOptionPane.WARNING_MESSAGE);
						return;	
					}
					
					if (pass != null && passConf != null) {
						if(pass.equals(pass.toLowerCase())) isValid = false;
						if(!pass.equals(passConf)) {
							JOptionPane.showMessageDialog(FirstWindow.this, "Passwords do not match",
									"Sign-up Failed", JOptionPane.WARNING_MESSAGE);
							return;	
						}

						
					} else {
						JOptionPane.showMessageDialog(FirstWindow.this, "Please include:\n Password and Password Confirmation",
								"Sign-up Failed", JOptionPane.WARNING_MESSAGE);
						return;	
	
					}
					
					if(!isValid) {
						JOptionPane.showMessageDialog(FirstWindow.this, "Password must contain at least:\n1-Number 1-Uppercase Letter",
								"Sign-up Failed", JOptionPane.WARNING_MESSAGE);
						return;
					}
					
					
					// try to connect to server
					Thread t = new Thread() {
						
						@Override
						public void run() {
							// SEt up request
							gui.new ClientThread(FirstWindow.this, user, pass);
						}
					};
					
					t.start();
				}
				
			});
			
			add(grid);
			
			
		}
		
		
	}
	
	private class LoginPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		
		JTextField username;
		JPasswordField password;
		JButton login;
		
		LoginPanel() {
			setBackground(Color.GRAY);
			username = new JTextField();
			username.setPreferredSize(new Dimension(200,22));
			password = new JPasswordField();
			password.setPreferredSize(new Dimension(200,22));
			//gl.setVgap(4);
			JPanel grid = new JPanel();
			grid.setLayout(new BoxLayout(grid,BoxLayout.PAGE_AXIS));

			grid.setAlignmentX(Component.CENTER_ALIGNMENT);
			grid.setBackground(Color.GRAY);
			JPanel firstRow = new JPanel();
			firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.LINE_AXIS));
			firstRow.setBackground(Color.GRAY);
			firstRow.add(new JLabel("Username: "));
			firstRow.add(username);
			firstRow.setAlignmentX(Component.CENTER_ALIGNMENT);

			JPanel secondRow = new JPanel();
			secondRow.setLayout(new BoxLayout(secondRow, BoxLayout.LINE_AXIS));
			secondRow.setBackground(Color.GRAY);
			secondRow.add(new JLabel("Password: "));
			secondRow.add(password);
			secondRow.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			grid.add(firstRow);
			grid.add(Box.createRigidArea(new Dimension(5,5)));
			grid.add(secondRow);
			grid.add(Box.createRigidArea(new Dimension(5,5)));
			login = new JButton("Login");
			login.setAlignmentX(Component.CENTER_ALIGNMENT);
			login.setUI(new CustomButtonUI(login));
			
			JPanel btnHolder = new JPanel();
			btnHolder.setBackground(Color.GRAY);
			btnHolder.add(login);
			grid.add(btnHolder);
			
			add(grid);
		}
		
		
	}

}
