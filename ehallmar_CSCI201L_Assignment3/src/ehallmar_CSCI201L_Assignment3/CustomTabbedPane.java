package ehallmar_CSCI201L_Assignment3;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JTabbedPane;

public class CustomTabbedPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	private String name = "Evan's Text Editor";
	private Font font;
	
	CustomTabbedPane(Font _font) {
		super();
		font = _font;
		setUI(new CustomTabbedPaneUI());
		setForeground(Color.WHITE);
	}
	public void paintComponent(Graphics g)
	{	
		// Draw original stuff
		super.paintComponent(g);
		// And then draw logo if no tabs open
		if (this.getTabCount()==0) {
			// draw background
			g.setColor(Color.GRAY);
			g.fillRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
			// draw logo
			g.setColor(GUIController.LOGO_COLOR);
			g.setFont(font);
			FontMetrics fm = g.getFontMetrics();
			g.drawString(name, (this.getWidth()-fm.stringWidth(name))/2, (this.getHeight()-fm.getHeight())/2);
		}
	}

}
