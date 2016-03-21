package client;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.UIManager;

public class MainController {
	private static boolean onlineMode = false;
	private static int port;
	private static String host;
	private static String username;
	
	public static String getUsername() {
		return username;
	}
	
	public static void setUsername(String username) {
		MainController.username = username;
	}
	
	public static String getHost() {
		return host;
	}
	
	public static int getPort() {
		return port;
	}
	
	public static void setOffline() {
		onlineMode = false;
	}
	
	public static void setOnline() {
		onlineMode = true;
	}
	
	public static boolean isOnline() {
		return onlineMode;
	}

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
		     UIManager.put("List.font", default_font);
		     UIManager.put("ListItem.font", default_font);
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
		
		// Parse CONFIG file

    	Properties config = new Properties();
    	InputStream is = null;
    	
    	try {
        
    		String f = "config/client.properties";
    		is = new FileInputStream(new File(f)); 
    		
    		if(is!=null) config.load(is);
 
    	} catch (IOException ex) {
    		// Error loading config file
        } finally{
        	if(is!=null){
        		try {
        			is.close();
        		} catch (IOException e) {
        			// Error closing config file
        		}	
        	}
        }
    	
    	// Make sure we have properties    	
    	try {
    		port = Integer.parseInt(config.getProperty("port"));
    	} catch (Exception e) {
    		// Bad port argument
    		
    	}
    	
    	try {
    		host = config.getProperty("host");
    	} catch (Exception e) {
    		// Bad port argument
    		System.out.println("Cannot find host");
    	}
		
		GUIController controller = new GUIController();
		controller.setVisible(true);
	}

}
