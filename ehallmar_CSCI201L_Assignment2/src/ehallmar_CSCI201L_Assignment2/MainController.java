package ehallmar_CSCI201L_Assignment2;

import javax.swing.UIManager;

public class MainController {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Warning! Cross-platform L&F not used!");
		}
		GUIController controller = new GUIController();
		controller.setVisible(true);
	}

}
