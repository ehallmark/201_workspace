package ehallmar_CSCI201L_Assignment3;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class CustomTabbedPaneUI extends BasicTabbedPaneUI {
	
    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected)  {
    	if(isSelected) {
	    	g.setColor(GUIController.OFFSET_COLOR);
    	} else {
    		g.setColor(Color.DARK_GRAY);
    	}
    	int[] x_s = {x, x, x+w/12, x+w, x+w};
    	int[] y_s = {y+h,y+h/5,y,y,y+h};
    	g.fillPolygon(x_s, y_s, 5);
    }
    
    @Override
    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
    	if(selectedIndex >= 0) {
    		g.setColor(GUIController.OFFSET_COLOR);
    		g.fillRect(x, y, w, h);
    	}
    }
    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
    	if(selectedIndex >= 0) {
    		g.setColor(GUIController.OFFSET_COLOR);
    		g.fillRect(x, y, w, h);
    	}
    }
    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
    	if(selectedIndex >= 0) {
    		g.setColor(GUIController.OFFSET_COLOR);
    		g.fillRect(x, y, w, h);
    	}
    }
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h)  {
    	if(selectedIndex >= 0) {
    		g.setColor(GUIController.OFFSET_COLOR);
    		g.fillRect(x, y, w, h);
    	}
    }

    
    @Override
    protected void  paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected)  {
    	g.setColor(GUIController.OFFSET_COLOR);
    	int[] x_s = {x, x, x+w/8, x+w, x+w};
    	int[] y_s = {y+h,y+h/5,y,y,y+h};
    	g.drawPolygon(x_s, y_s, 5);
    }

}
