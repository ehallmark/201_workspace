package ehallmar_CSCI201L_Assignment3;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class CustomHorizontalScrollBar extends JScrollBar {
	private static final long serialVersionUID = 1L;
	private Image decr_image;
	private Image incr_image;
	private Image thumb_image;
	private Image track_image;
	
	CustomHorizontalScrollBar() {
		super(JScrollBar.HORIZONTAL);
		try {
			incr_image = ImageIO.read(new File("img/scrollbar/red_sliderRight.png"));
			decr_image = ImageIO.read(new File("img/scrollbar/red_sliderLeft.png"));
			thumb_image = ImageIO.read(new File("img/scrollbar/red_button06.png"));
			track_image = ImageIO.read(new File("img/scrollbar/red_button04.png"));
		} catch (IOException e) {
			// File will be there for now
		}
		setUI(new CustomScrollUI());
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(incr_image, this.getWidth()-this.getHeight(), 0, this.getHeight(), this.getHeight(), this);
		g.drawImage(decr_image, 0, 0, this.getHeight(), this.getHeight(), this);
	}
	
    class CustomScrollUI extends BasicScrollBarUI {
	    @Override
	    protected JButton createIncreaseButton(int orientation) {
	    	JButton increase = super.createIncreaseButton(orientation);
	    	increase.setVisible(false);
	        return increase;
	    }
	    
	    @Override
	    protected JButton createDecreaseButton(int orientation) {
	    	JButton decrease = super.createDecreaseButton(orientation);
	    	decrease.setVisible(false);
	        return decrease;
	    }
	    
		@Override
		protected void paintThumb(final Graphics g, JComponent c, Rectangle thumbBounds) {
			g.drawImage(thumb_image, thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, c);
		}
		
		@Override
	    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
			g.drawImage(track_image, trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, c);
		} 
	}
    
    
}