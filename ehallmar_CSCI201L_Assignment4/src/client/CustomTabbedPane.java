package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JTabbedPane;

public class CustomTabbedPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	private final String name = "Evan's Text Editor";
	private Font font;

	
	CustomTabbedPane() {
		super();
		setUI(new CustomTabbedPaneUI());
		font = GUIController.DEFAULT_FONT.deriveFont(22f);
		setForeground(Color.WHITE);
	}
	
	public void paintComponent(Graphics g) {	
		// Draw original stuff
		super.paintComponent(g);
		
		if(this.getTabCount()==0) {
			// draw background
			g.setColor(Color.GRAY);
			g.fillRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
			// draw logo
			g.setColor(GUIController.LOGO_COLOR);
	
			g.setFont(font);
			FontMetrics fm = g.getFontMetrics();
			g.drawString(name, (this.getWidth()-fm.stringWidth(name))/2, (int) ((this.getHeight()-fm.getHeight())/2));
		}
	}

}
