package ehallmar_CSCI201L_Assignment2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.AbstractButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.UndoManager;

public class GUIController  extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private LinkedList<File> files;
	public LinkedList<JTextArea> tabs_text;
	public JTabbedPane tabbed_pane;
	public JMenuBar menu_bar;
	public UndoManager undo_manager;
	private static final String NEW_FILE_NAME = "new";
	
	public GUIController() {
		files = new LinkedList<File>();
		tabs_text = new LinkedList<JTextArea>();
		undo_manager = new UndoManager();
		generateUI();
		setVisible(true);
	}
	
	private void createTab(File file) {
		JPanel new_tab = new JPanel();
		new_tab.setLayout(new BorderLayout());
		JTextArea text_area = new JTextArea();
		text_area.getDocument().addUndoableEditListener(undo_manager);
		new_tab.add(text_area, BorderLayout.CENTER);
		tabs_text.add(text_area);
		String tab_name = NEW_FILE_NAME;
		if(file != null) { 
			// Open file
			tab_name = file.getName();
		}
		files.add(file);
		tabbed_pane.add(tab_name, new_tab);
		tabbed_pane.setSelectedIndex(tabs_text.size()-1);
	}
	
	private void closeTab(int tab_index) {
		if(tab_index >= 0) {
			tabbed_pane.remove(tab_index);
			tabs_text.remove(tab_index);
			files.remove(tab_index);
		}
	}
	
	private void newFileHelper() {
		createTab(null);
	}
	
	private void openFileHelper() {
		// Open file chooser
		JFileChooser file_chooser = new JFileChooser();
		file_chooser.setName("Open File...");
		file_chooser.setAcceptAllFileFilterUsed(false);
		file_chooser.setFileFilter(new FileNameExtensionFilter("txt files (*.txt)", "txt"));
	    if(file_chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	    	// create new tab
	    	File file = file_chooser.getSelectedFile();
	    	createTab(file);
	    	String text = "";
	    	Scanner scanner = null;
			try {
				scanner = new Scanner(file);
		    	while(scanner.hasNextLine()) {
		    		text+=scanner.nextLine()+'\n';
		    	}
		    	tabs_text.getLast().setText(text);
			} catch (FileNotFoundException e) {
				System.out.println("Error reading in file: "+file.getAbsolutePath());
				System.out.println(e.getMessage());
			} finally {
				if(scanner!=null) scanner.close();
			}
	    } 
	}
	
	private File createNewFileHelper(File file) {
		// Open file chooser
		JFileChooser file_chooser = new JFileChooser();
		file_chooser.setName("Save As...");
		file_chooser.setAcceptAllFileFilterUsed(false);
		if (file != null) {
			file_chooser.setSelectedFile(file);;
		}
		file_chooser.setFileFilter(new FileNameExtensionFilter("txt files (*.txt)", "txt"));
	    if(file_chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
	    	// create new tab
	    	return file_chooser.getSelectedFile();
	    } 
	    return null;
	}
	
	private void saveFileHelper() {
		if(tabs_text.isEmpty()) return; 
		int tab_index = tabbed_pane.getSelectedIndex();
		JTextArea text = tabs_text.get(tab_index);
		FileWriter writer = null;
		String filename = null;
		try {
			// create new file helper
			File file = createNewFileHelper(files.get(tab_index));	
			if (file == null) { return; }
			filename = file.getAbsolutePath();
			// make sure we have the .txt suffix
			if(!filename.endsWith(".txt")) {
				filename = filename+".txt";
			}
			file = new File(filename);
			if(!file.createNewFile()) {
				// filename already exists, prompt user to confirm
				int user_response = JOptionPane.showConfirmDialog(null, file.getName()+" already exists\nDo you want to replace it?", "Confirm Save As", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (user_response == JOptionPane.NO_OPTION) {
					// User does not want to save anymore
					return;
				}
			}
			
			writer = new FileWriter(file);
			writer.write(text.getText());
			tabbed_pane.setTitleAt(tabbed_pane.getSelectedIndex(), file.getName());			
		} catch (IOException e) {
			System.out.println("Error writing to file: "+filename);
			System.out.println(e.getMessage());
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					if(filename!=null) {
						System.out.println("Error closing file: "+filename);
					}
					System.out.println(e.getMessage());
				}
			}
		}
	}
	
	private void closeFileHelper() {
		int index = tabbed_pane.getSelectedIndex();
		closeTab(index);
	}
	
	private void initFileMenu() {
		// create file menu
		JMenu file_menu = new JMenu("File");
		file_menu.setMnemonic(KeyEvent.VK_F);
		
		// create new button
		JMenuItem new_file_option = new JMenuItem("New");
		file_menu.add(new_file_option);
		
		// new button accelerator (control-n)
        new_file_option.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,KeyEvent.CTRL_DOWN_MASK));
        
        // new file action
        new_file_option.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newFileHelper();
			}
        });
		
		// create open button
		JMenuItem open_file_option = new JMenuItem("Open");
		file_menu.add(open_file_option);
		
		// open button accelerator (control-o)
		open_file_option.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,KeyEvent.CTRL_DOWN_MASK));
		
		// open button action
		open_file_option.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openFileHelper();
			}
        });
		
		// create save button
		JMenuItem save_file_option = new JMenuItem("Save");
		file_menu.add(save_file_option);
		
		// save button accelerator (control-s)
		save_file_option.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,KeyEvent.CTRL_DOWN_MASK));
		
		// save button action
		save_file_option.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFileHelper();
			}
        });
		
		// create close button
		JMenuItem close_file_button = new JMenuItem("Close");
		file_menu.add(close_file_button);
		// close button action
		close_file_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeFileHelper();
			}
        });
		
		// Apply the menu
		menu_bar.add(file_menu);
	}
	
	private void initTabbedPane() {
		tabbed_pane = new JTabbedPane();
		add(tabbed_pane, BorderLayout.CENTER);
	}
	
	private void initEditMenu() {
		// create edit menu
		JMenu edit_menu = new JMenu("Edit");
		edit_menu.setMnemonic(KeyEvent.VK_E);
		
		// create undo button
		JMenuItem undo_button = new JMenuItem("Undo");
		edit_menu.add(undo_button);
		
		// undo button accelerator (control-z)
		undo_button.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,KeyEvent.CTRL_DOWN_MASK));
		
		// undo button action
		undo_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (undo_manager.canUndo()) {
					undo_manager.undo();
				}
			}
        });
		
		// create redo button
		JMenuItem redo_button = new JMenuItem("Redo");
		edit_menu.add(redo_button);
		
		// redo button accelerator (control-y)
		redo_button.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,KeyEvent.CTRL_DOWN_MASK));
		
		// redo button action
		redo_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(undo_manager.canRedo()) {
					undo_manager.redo();
				}				
			}
        });
		
		// Enable/disable redo and edit
		UndoListener listener = new UndoListener(undo_button,redo_button,undo_manager);
		edit_menu.addChangeListener(listener);
		
		edit_menu.addSeparator();
		
		// create cut button
		JMenuItem cut_button = new JMenuItem("Cut");
		edit_menu.add(cut_button);
		
		// cut button accelerator (control-x)
		cut_button.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,KeyEvent.CTRL_DOWN_MASK));
		
		// cut button action
		cut_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!tabs_text.isEmpty()) {
					JTextArea current = tabs_text.get(tabbed_pane.getSelectedIndex());
					current.cut();
				}
			}
        });
		
		// create copy button
		JMenuItem copy_button = new JMenuItem("Copy");
		edit_menu.add(copy_button);
		
		// copy button accelerator (control-c)
		copy_button.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,KeyEvent.CTRL_DOWN_MASK));
		
		// copy button action
		copy_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!tabs_text.isEmpty()) {
					JTextArea current = tabs_text.get(tabbed_pane.getSelectedIndex());
					current.copy();
				}
			}
        });
		
		// create paste button
		JMenuItem paste_button = new JMenuItem("Paste");
		edit_menu.add(paste_button);
		
		// copy button accelerator (control-c)
		paste_button.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,KeyEvent.CTRL_DOWN_MASK));
		
		// copy button action
		paste_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!tabs_text.isEmpty()) {
					JTextArea current = tabs_text.get(tabbed_pane.getSelectedIndex());
					current.paste();
				}
			}
        });
		edit_menu.addSeparator();
		
		// create select all button
		JMenuItem select_all_button = new JMenuItem("Select All");
		edit_menu.add(select_all_button);
		
		// save button accelerator (control-a)
		select_all_button.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,KeyEvent.CTRL_DOWN_MASK));
		// save button action
		select_all_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!tabs_text.isEmpty()) {
					JTextArea current = tabs_text.get(tabbed_pane.getSelectedIndex());
					current.selectAll();
				}
			}
        });
		
		// Apply the menu
		menu_bar.add(edit_menu);
	}
	
	private void generateUI() {
		menu_bar = new JMenuBar();
		setJMenuBar(menu_bar);
		initFileMenu();
		initEditMenu();
		initTabbedPane();
        setTitle("Evan's Text Editor");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
}
