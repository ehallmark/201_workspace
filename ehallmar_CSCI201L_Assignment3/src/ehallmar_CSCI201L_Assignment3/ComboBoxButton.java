package ehallmar_CSCI201L_Assignment3;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;

public class ComboBoxButton extends JButton {
	private static final long serialVersionUID = 1L;
	Image image;
	
	ComboBoxButton() {
		super();
		this.setRolloverEnabled(false);
		try {
			image = ImageIO.read(new File("img/menu/red_sliderDown.png"));
		} catch (IOException e) {
			// File will be there for now
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
	}
	
}
