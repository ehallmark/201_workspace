package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JTabbedPane;

public class CustomTabbedPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;

	
	CustomTabbedPane() {
		super();
		setUI(new CustomTabbedPaneUI());
		setForeground(Color.WHITE);
	}

}
