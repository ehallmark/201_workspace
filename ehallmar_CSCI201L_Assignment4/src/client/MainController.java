package client;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

import javax.swing.UIManager;

public class MainController {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Warning! Cross-platform L&F not used!");
		}
		// let's set the font stuff here
		try {
		     Font default_font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/kenvector_future.ttf")).deriveFont(12f);
		     UIManager.put("MenuItem.acceleratorFont", default_font.deriveFont(11f));
		     UIManager.put("MenuItem.font", default_font);
		     UIManager.put("MenuBar.font", default_font);
		     UIManager.put("Menu.font", default_font.deriveFont(14f));
		     UIManager.put("TabbedPane.font", default_font);
		     UIManager.put("Label.font", default_font);
		     UIManager.put("Button.font", default_font.deriveFont(11f));
		     UIManager.put("ComboBox.font", default_font.deriveFont(11f));
		     UIManager.put("TextPane.font", default_font);
		     UIManager.put("TitledBorder.font", default_font.deriveFont(11f));
		     GUIController.DEFAULT_FONT = default_font;

		} catch (IOException|FontFormatException e) {
		     //Handle exception
			// unable to find font :(
		}
		GUIController controller = new GUIController();
		controller.setVisible(true);
	}

}
