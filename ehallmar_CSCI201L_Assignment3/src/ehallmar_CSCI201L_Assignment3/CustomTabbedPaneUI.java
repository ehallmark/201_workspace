package ehallmar_CSCI201L_Assignment3;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class CustomTabbedPaneUI extends BasicTabbedPaneUI {
	
    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected)  {
    	if(isSelected) {
	    	g.setColor(GUIController.OFFSET_COLOR);
    	} else {
    		g.setColor(Color.DARK_GRAY);
    	}
    	int[] x_s = {x, x, x+w/10, x+w, x+w};
    	int[] y_s = {y+h,y+h/5,y,y,y+h};
    	g.fillPolygon(x_s, y_s, 5);
    }
    
    @Override
    protected void  paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected)  {
    	if(isSelected) {
    		g.setColor(Color.DARK_GRAY);
    	} else {
	    	g.setColor(GUIController.OFFSET_COLOR);
    	}
    	int[] x_s = {x, x, x+w/10, x+w, x+w};
    	int[] y_s = {y+h,y+h/5,y,y,y+h};
    	g.drawPolyline(x_s, y_s, 5);
    }
    
    @Override
    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
    	if(selectedIndex >= 0) {
    		g.setColor(Color.DARK_GRAY);
    		g.drawLine(x, y+h-1, x+w, y+h-1);
    	}
    }
    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
    	if(selectedIndex >= 0) {
    		g.setColor(Color.DARK_GRAY);
    		g.drawLine(x, y, x, y+h);
    	}
    }
    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
    	if(selectedIndex >= 0) {
    		g.setColor(Color.DARK_GRAY);
    		g.drawLine(x+w-1, y, x+w-1, y+h);
    	}
    }
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h)  {
    	if(selectedIndex >= 0) {
    		Rectangle b = this.getTabBounds(this.tabPane, selectedIndex);
    		g.setColor(GUIController.OFFSET_COLOR);
        	g.fillRect(x+1, y, w-2, h-1);
    		g.setColor(Color.DARK_GRAY);
    		g.drawLine(x, y, b.x, y);
    		g.drawLine(b.x+b.width, y, x+w, y);
    	}
    }

}
