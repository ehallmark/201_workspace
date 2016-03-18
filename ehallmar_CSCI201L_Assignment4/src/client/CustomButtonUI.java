package client;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;

public class CustomButtonUI extends BasicButtonUI {
	Image hover_image;
	Image image;
	boolean is_hover = false;
	CustomButtonUI self;
	
	public CustomButtonUI(JButton btn) {
		super();
		self = this;
		btn.setRolloverEnabled(false);
		try {
			image = ImageIO.read(new File("img/menu/red_button11.png"));
			hover_image = ImageIO.read(new File("img/menu/red_button11_selected.png"));
		} catch (IOException e) {
			// File will be there for now
		}
	}
	
	@Override
	protected void  paintText(Graphics g, AbstractButton b, Rectangle textRect, String text)   {
		if(textRect != null) {
			if(is_hover) {
				g.drawImage(hover_image, 0, 0, b.getWidth(), b.getHeight(), b);
			} else {
				g.drawImage(image, 0, 0, b.getWidth(), b.getHeight(), b);
			}
			super.paintText(g, b, textRect, text);
		}
	}
	
	@Override
	protected void installListeners(AbstractButton b) {
		super.installListeners(b);
		b.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				is_hover = true;
				self.update(b.getGraphics(), b);
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				is_hover = false;
				self.update(b.getGraphics(), b);
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			
		});
	}
		
}
