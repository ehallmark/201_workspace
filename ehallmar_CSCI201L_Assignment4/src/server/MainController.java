package server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.UIManager;

public class MainController {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Warning! Cross-platform L&F not used!");
		}
		
		// Parse CONFIG file

    	Properties config = new Properties();
    	InputStream is = null;
    	
    	try {
        
    		String f = "config/server.properties";
    		is = MainController.class.getClassLoader().getResourceAsStream(f);
    		if(is==null){
    	            // Unable to find file
    		    return;
    		}

    		//load
    		config.load(is);
 
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
    	Integer port = null;
    	String host = null;
    	
    	try {
    		port = Integer.parseInt(config.getProperty("port"));
    	} catch (Exception e) {
    		// Bad port argument
    	}
    	
    	try {
    		host = config.getProperty("host");
    	} catch (Exception e) {
    		// Bad port argument
    	}
		
		ServerWindow server = new ServerWindow(port, host);
		server.setVisible(true);
	}

}
