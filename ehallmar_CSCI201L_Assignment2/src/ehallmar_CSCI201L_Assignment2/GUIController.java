package ehallmar_CSCI201L_Assignment2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.zip.DataFormatException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.undo.UndoManager;

import ehallmar_CSCI201L_Assignment1.Controller;

public class GUIController  extends JFrame {
	private static final long serialVersionUID = 1L;
	
	// static variables
	private static final String NEW_FILE_NAME = "new";
	static final String DEFAULT_KEYBOARD_FILE_NAME = "querty-us.kb";
	static final String DEFAULT_WORDLIST_FILE_NAME = "wordlist.wl";
	static File keyboard_file = new File(DEFAULT_KEYBOARD_FILE_NAME);;
	static File wordlist_file = new File(DEFAULT_WORDLIST_FILE_NAME);
	private static ConfigureWindow config_menu = new ConfigureWindow();
	static JTabbedPane tabbed_pane;
	static boolean inConfig = false;
	private static JMenuBar menu_bar;
	private static UndoManager undo_manager;
	
	public GUIController() {
		undo_manager = new UndoManager();
		
		// Generate UI
		generateUI();
		// Display
		setVisible(true);
		validate();
	}
	
	// main tab window class
	class TabWindow extends JPanel {
		private static final long serialVersionUID = 1L;
		JTextArea text_area;
		File file;
		SpellCheckSidebar spell_check_sidebar;
		
		//Set up tab GUI
		TabWindow(File f) {
			file = f;
			setLayout(new BorderLayout());
			text_area = new JTextArea();
			JScrollPane scroll_pane = new JScrollPane(text_area);
			text_area.getDocument().addUndoableEditListener(undo_manager);
			add(scroll_pane, BorderLayout.CENTER);
			scroll_pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			
			String tab_name = NEW_FILE_NAME;
			if(file != null) { 
				// Get opened file name
				tab_name = file.getName();
			}
			
			// add to tabbed pane
			tabbed_pane.add(tab_name, this);
			tabbed_pane.setSelectedIndex(tabbed_pane.getTabCount()-1);
			
			// add spell check stuff
			spell_check_sidebar = new SpellCheckSidebar(this);
		}
		
		private void runSpellCheck() {
			spell_check_sidebar.reset();
			spellCheckHelper(text_area.getText());
		}
		
		private void spellCheckHelper(String text) {
			if(text.replaceAll("\\s", "").length()==0) {
				JOptionPane.showMessageDialog(this, "No words to check!", "SpellCheck complete...", JOptionPane.INFORMATION_MESSAGE);
				closeSpellCheckWindow();
				return;
			}
			openSpellCheckWindow();
			// check to make sure there are words to check
		    String[] words = text.split("\\s");
		    spell_check_sidebar.words = words;
		    spell_check_sidebar.text = text;
			// try to get word corrections from spell checker
			try {
				spell_check_sidebar.word_corrections = Controller.spellCheck(wordlist_file.getAbsolutePath(),keyboard_file.getAbsolutePath(),text);
			} catch (FileNotFoundException e) {
				// File not found
				JOptionPane.showMessageDialog(this, "Cannot perform action\nProblem finding configuration files.",
						"File not found...", JOptionPane.ERROR_MESSAGE);
				return;
			} catch (IOException e) {
				// Error reading from file
				JOptionPane.showMessageDialog(this, "Cannot perform action\nInvalid configuration files.",
						"Error reading file...", JOptionPane.ERROR_MESSAGE);
				return;
			} catch (DataFormatException e) {
				// In valid file format
				JOptionPane.showMessageDialog(this, "Cannot perform action\nMissing files or incorrect file extensions.",
						"Configuration Error...", JOptionPane.ERROR_MESSAGE);
				return;
			}		
			
			// lets get it goin'
			int next_word = spell_check_sidebar.min_index;
			while(next_word == spell_check_sidebar.min_index && spell_check_sidebar.word_index<spell_check_sidebar.words.length) {
				nextWordCorrection();
				spell_check_sidebar.word_index++;
			}
			
			if(next_word==spell_check_sidebar.min_index) {
				JOptionPane.showMessageDialog(this, "All words spelled correctly!", "SpellCheck complete...", JOptionPane.INFORMATION_MESSAGE);
				closeSpellCheckWindow();
			}
		}
		
		void nextWordCorrection() 
		{	
			String word = null; 
			if (spell_check_sidebar.word_index<spell_check_sidebar.words.length) { 
				word = spell_check_sidebar.words[spell_check_sidebar.word_index];
			} 
			if(word!=null && spell_check_sidebar.word_corrections.containsKey(word.toLowerCase())) {
				spell_check_sidebar.spell_check_label.setText("Spelling: "+word);
				spell_check_sidebar.combo_box.removeAllItems();
				// make sure we get the whole word and not a prefix
				if(!(spell_check_sidebar.min_index==0 && spell_check_sidebar.text.startsWith(word))) {
					spell_check_sidebar.min_index=spell_check_sidebar.text.indexOf(" "+word+" ",Math.max(0,spell_check_sidebar.min_index-1))+1;
				}
				// fallback
				if(spell_check_sidebar.min_index<=0) {
					spell_check_sidebar.min_index=spell_check_sidebar.text.indexOf(word,spell_check_sidebar.min_index);
				}
				int max_index = spell_check_sidebar.min_index+word.length();
				for(String suggestion: spell_check_sidebar.word_corrections.get(word.toLowerCase())) {
					spell_check_sidebar.combo_box.addItem(suggestion);
				}
				// unhighlight previous words
				spell_check_sidebar.highlight.removeAllHighlights();
				// highlight word
				try {
					spell_check_sidebar.highlight.addHighlight(spell_check_sidebar.min_index, max_index, spell_check_sidebar.highlighter);
				} catch (BadLocationException e) {
					// text no longer exists- keep going
				}
				spell_check_sidebar.min_index++;
				revalidate();
			}
		}
		
		// Spell check class
		class SpellCheckSidebar extends JPanel  {
			private static final long serialVersionUID = 1L;
			
			public JButton change_button;
			public JButton ignore_button;
			public JButton add_button;
			public JButton close_button;
			public Hashtable<String, ArrayList<String>> word_corrections;
			public JLabel spell_check_label;
			public JComboBox<String> combo_box;
			public Highlighter highlight;
			public HighlightPainter highlighter;
			public String[] words;
			public int min_index;
			public int word_index;
			public String text;
			
			SpellCheckSidebar(TabWindow _parent) {
				// spell check side bar
				setLayout(new BorderLayout());
				spell_check_label = new JLabel("Spelling: ");
				Font font = spell_check_label.getFont();
				spell_check_label.setFont(font.deriveFont((float)18));
				add_button = new JButton("Add");
				ignore_button = new JButton("Ignore");
				combo_box = new JComboBox<String>();
				change_button = new JButton("Change");
			    text_area = get_current_tab().text_area;
			    highlight = text_area.getHighlighter();
			    highlighter = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);
			    // explicitly set pointer to get buttons to behave
			    _parent.spell_check_sidebar = this;
			    
			    // set indices
				min_index = 0;
				word_index = 0;
			    
			    // Add button functionality
				add_button.addActionListener(new AddButtonListener(_parent));
				// Ignore button functionality
				ignore_button.addActionListener(new IgnoreButtonListener(_parent));
				// Change button functionality
				change_button.addActionListener(new ChangeButtonListener(_parent));
				
				// rest of spell check layout
				JPanel main_panel = new JPanel();
				JPanel second_row = new JPanel();
				JPanel third_row = new JPanel();
				GridLayout row_layout = new GridLayout(1,2);
				second_row.setLayout(row_layout);
				third_row.setLayout(row_layout);
				GridLayout grid = new GridLayout(3,1);
				grid.setVgap(10);
				main_panel.setLayout(grid);
				//border
				main_panel.setBorder(BorderFactory.createTitledBorder("Spell Check"));
				
				// add main panel
				add(main_panel, BorderLayout.NORTH);

				// first row
				main_panel.add(spell_check_label);

				// second row
				main_panel.add(second_row);
				second_row.add(ignore_button);
				second_row.add(add_button);

				// third_row
				main_panel.add(third_row);
				third_row.add(combo_box);
				third_row.add(change_button);
				
				//Set up actions
				
				close_button = new JButton("Close");
				add(close_button, BorderLayout.SOUTH);
				// set up close action
				close_button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						closeSpellCheckWindow();
					}
		        });	
			}
			
			private void reset() {
				word_index = 0;
				min_index = 0;
				word_corrections = null;
				words = null;
				text = null;
				spell_check_label.setText("Spelling: ");
				combo_box.removeAllItems();
			}
		} // end of spell check class
	} // end of main tab class
	
	private TabWindow get_last_tab() {
    	return (TabWindow) tabbed_pane.getComponentAt(tabbed_pane.getTabCount()-1);
	}
	
	private static TabWindow get_current_tab() {
    	return (TabWindow) tabbed_pane.getSelectedComponent();
	}
		
	private void createTab(File file) {
		// Instantiate new tab
		new TabWindow(file);
	}
	
	private void closeTab(int tab_index) {
		if(tab_index >= 0 && tab_index < tabbed_pane.getTabCount()) {
			tabbed_pane.remove(tab_index);
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
	    if(file_chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
	    	// create new tab
	    	File file = file_chooser.getSelectedFile();
	    	if(!file.getName().endsWith(".txt")) { return; }
	    	// check for previously open tab
	    	for(Component c: tabbed_pane.getComponents()) {
	    		TabWindow tab = (TabWindow) c;
	    		if(file.equals(tab.file)){
	    			// Uh oh
	    			JOptionPane.showMessageDialog(this, "Cannot perform action\n"+file.getName()+" already open.",
	    					"File already open...", JOptionPane.WARNING_MESSAGE);
	    			return;
	    		}
	    	}
	    	createTab(file);
	    	String text = "";
	    	Scanner scanner = null;
			try {
				scanner = new Scanner(file);
		    	while(scanner.hasNextLine()) {
		    		text+=scanner.nextLine()+'\n';
		    	}
		    	get_last_tab().text_area.setText(text);
			} catch (FileNotFoundException e) {
    			// Uh oh
    			JOptionPane.showMessageDialog(this, "Cannot perform action\n"+file.getName()+" not found.",
    					"File not found...", JOptionPane.ERROR_MESSAGE);
    			return;
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
		if(tabbed_pane.getTabCount()<=0) return; 
		TabWindow current_tab = get_current_tab();
		JTextArea text = current_tab.text_area;
		FileWriter writer = null;
		String filename = null;
		try {
			// create new file helper
			File file = createNewFileHelper(current_tab.file);	
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
			JOptionPane.showMessageDialog(this, "Cannot perform action\nError writing to file.",
					"Error...", JOptionPane.ERROR_MESSAGE);
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this, "Cannot perform action\nError closing file.",
							"Error...", JOptionPane.ERROR_MESSAGE);
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
		// switch config menu based on tab change if open
		tabbed_pane.addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent e) {
	        	if(inConfig) {
	        		openConfigMenu();
	        	}
	        }
	    });
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
				if (!(tabbed_pane.getTabCount()<=0)) {
					JTextArea current = get_current_tab().text_area;
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
				if (!(tabbed_pane.getTabCount()<=0)) {
					JTextArea current = get_current_tab().text_area;
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
				if (!(tabbed_pane.getTabCount()<=0)) {
					JTextArea current = get_current_tab().text_area;
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
				if (!(tabbed_pane.getTabCount()<=0)) {
					JTextArea current = get_current_tab().text_area;
					current.selectAll();
				}
			}
        });
		
		// Apply the menu
		menu_bar.add(edit_menu);
	}
	
	private void openSpellCheckWindow() {
		if(inConfig) {
			closeConfigMenu();
		}
		TabWindow current_tab = get_current_tab();
		current_tab.add(current_tab.spell_check_sidebar,BorderLayout.EAST);
		current_tab.text_area.setEditable(false);
		current_tab.text_area.getHighlighter().removeAllHighlights();
		validate();
	}
	
	static void closeSpellCheckWindow() {
		TabWindow current_tab = get_current_tab();
		current_tab.remove(current_tab.spell_check_sidebar);
		current_tab.text_area.setEditable(true);
		current_tab.text_area.getHighlighter().removeAllHighlights();
		tabbed_pane.getParent().validate();
	}
	
	static void openConfigMenu() {
		closeSpellCheckWindow();
		inConfig = true;
		JPanel panel = (JPanel) (tabbed_pane.getSelectedComponent());
		panel.add(config_menu,BorderLayout.EAST);
		tabbed_pane.getParent().validate();
	}
	
	static void closeConfigMenu() {
		JPanel panel = (JPanel) (config_menu.getParent());
		panel.remove(config_menu);
		inConfig = false;
		tabbed_pane.getParent().validate();
	}
	
	
	

	
	private void initSpellCheckMenu() {
		// create spell check menu
		JMenu spellcheck_menu = new JMenu("SpellCheck");
		spellcheck_menu.setMnemonic(KeyEvent.VK_S);
		
		// run button
		JMenuItem run_button = new JMenuItem("Run");
		spellcheck_menu.add(run_button);
		
		// run button accelerator (F7)
		run_button.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7,0));
		
		// run button action
		run_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!(tabbed_pane.getTabCount()<=0)) {
					//reset this tabs spell check container
					get_current_tab().runSpellCheck();
					
				} else {
	    			JOptionPane.showMessageDialog(GUIController.tabbed_pane, "Cannot perform action\nMust have a tab open.",
	    					"No tab open...", JOptionPane.WARNING_MESSAGE);
				}
			}
        });
		
		// configure tab
		JMenuItem config_button = new JMenuItem("Configure");
		config_button.setMnemonic(KeyEvent.VK_C);
		spellcheck_menu.add(config_button);
		
		// config button action
		config_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!(tabbed_pane.getTabCount()<=0)) {
					openConfigMenu();
				} else {
	    			JOptionPane.showMessageDialog(GUIController.tabbed_pane, "Cannot perform action\nMust have a tab open.",
	    					"No tab open...", JOptionPane.WARNING_MESSAGE);
				}
			}
        });
				
		// Apply the menu
		menu_bar.add(spellcheck_menu);
	}
	
	private void generateUI() {
		menu_bar = new JMenuBar();
		setJMenuBar(menu_bar);
		initFileMenu();
		initEditMenu();
		initTabbedPane();
		initSpellCheckMenu();
        setTitle("Evan's Text Editor");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
}
