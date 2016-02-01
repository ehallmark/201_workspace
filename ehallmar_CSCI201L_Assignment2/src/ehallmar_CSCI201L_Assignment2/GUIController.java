package ehallmar_CSCI201L_Assignment2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Vector;
import java.util.zip.DataFormatException;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
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
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
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
	
	private LinkedList<File> files;
	private LinkedList<JTextArea> tabs_text;
	private JTabbedPane tabbed_pane;
	private JMenuBar menu_bar;
	private UndoManager undo_manager;
	private static final String NEW_FILE_NAME = "new";
	private static final String DEFAULT_KEYBOARD_FILE_NAME = "querty-us.kb";
	private static final String DEFAULT_WORDLIST_FILE_NAME = "wordlist.wl";
	private String keyboard_file_name;
	private String wordlist_file_name; 
	private LinkedList<SpellCheckContainer> spell_check_containers;

	
	public GUIController() {
		files = new LinkedList<File>();
		tabs_text = new LinkedList<JTextArea>();
		spell_check_containers = new LinkedList<SpellCheckContainer>();
		
		undo_manager = new UndoManager();
	
		// Set defaults for SpellCheck
		keyboard_file_name = DEFAULT_KEYBOARD_FILE_NAME;
		wordlist_file_name = DEFAULT_WORDLIST_FILE_NAME;
		// Generate UI
		generateUI();
		// Display
		setVisible(true);
		validate();
	}
	
	private void createTab(File file) {
		JPanel new_tab = new JPanel();
		new_tab.setLayout(new BorderLayout());
		JTextArea text_area = new JTextArea();
		JScrollPane scroll_pane = new JScrollPane(text_area);
		text_area.getDocument().addUndoableEditListener(undo_manager);
		new_tab.add(scroll_pane, BorderLayout.CENTER);
		scroll_pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		String tab_name = NEW_FILE_NAME;
		if(file != null) { 
			// Open file
			tab_name = file.getName();
		}
		files.add(file);
		tabs_text.add(text_area);
		tabbed_pane.add(tab_name, new_tab);
		tabbed_pane.setSelectedIndex(tabs_text.size()-1);
		// set up spellcheck menu for this tab
		initSpellCheckTab();
	}
	
	private void closeTab(int tab_index) {
		if(tab_index >= 0) {
			tabbed_pane.remove(tab_index);
			tabs_text.remove(tab_index);
			files.remove(tab_index);
			spell_check_containers.remove(tab_index);
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
	    	// check for previously open tab
	    	for(File f: files) {
	    		if(file.equals(f)){
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
	
	private void openSpellCheckWindow() {
		JPanel panel = (JPanel) (tabbed_pane.getSelectedComponent());
		panel.add(spell_check_containers.get(tabbed_pane.getSelectedIndex()).sidebar,BorderLayout.EAST);
		tabs_text.get(tabbed_pane.getSelectedIndex()).setEditable(false);
		validate();
	}
	
	private void closeSpellCheckWindow() {
		JPanel panel = (JPanel) (tabbed_pane.getSelectedComponent());
		panel.remove(spell_check_containers.get(tabbed_pane.getSelectedIndex()).sidebar);
		JTextArea text_area = tabs_text.get(tabbed_pane.getSelectedIndex());
		text_area.setEditable(true);
		text_area.getHighlighter().removeAllHighlights();
		validate();
	}
	
	private void spellCheckHelper(String text) {
		Hashtable<String, ArrayList<String>> word_corrections = null;
		openSpellCheckWindow();
		// try to get word corrections from spell checker
		try {
			word_corrections = Controller.spellCheck(wordlist_file_name,keyboard_file_name,text);
			System.out.println(word_corrections.toString());
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
		SpellCheckContainer spell_check_container = spell_check_containers.get(tabbed_pane.getSelectedIndex());
		spell_check_container.word_corrections = word_corrections;
		JLabel word_label = spell_check_container.word_label;
		JComboBox<String> selections = spell_check_container.combo_box;
		JButton change_button = spell_check_container.change_button;
		JButton ignore_button = spell_check_container.ignore_button;
		JButton add_button = spell_check_container.add_button;
	    Highlighter highlight = spell_check_container.highlight;
	    HighlightPainter highlighter = spell_check_container.highlighter;
		JTextArea text_area = tabs_text.get(tabbed_pane.getSelectedIndex());

	    String[] words = text.split("\\s");
	    spell_check_container.words = words;

		add_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!tabs_text.isEmpty()) {
					SpellCheckContainer sp_container = spell_check_containers.get(tabbed_pane.getSelectedIndex()); 
					int next_word = sp_container.min_index;
					while(next_word == sp_container.min_index && sp_container.word_index<words.length) {
						next_word = nextWordCorrection(sp_container.word_corrections, sp_container.min_index, word_label, highlight, highlighter,
								text_area, selections, text, sp_container.word_index, words);
						sp_container.word_index++;
					}
					sp_container.min_index = next_word;	
					if(sp_container.word_index>=words.length) {
						closeSpellCheckWindow();
					}
				}
			}
        });
		
		// begin running
		int next_word = 0;
		while(next_word == spell_check_container.min_index && spell_check_container.word_index<words.length) {
			next_word = nextWordCorrection(word_corrections, spell_check_container.min_index, word_label, highlight, highlighter,
					text_area, selections, text, spell_check_container.word_index, words);
			spell_check_container.word_index++;
		}
		spell_check_container.min_index=next_word;

	}
	
	private int nextWordCorrection(Hashtable<String, ArrayList<String>> word_corrections, int min_index, JLabel word_label, 
			Highlighter highlight, HighlightPainter highlighter, JTextArea text_area, JComboBox<String> selections,
			String text, int word_index, String[] words) 
	{
		String word = null;
		if (word_index<words.length) { 
			word = words[word_index];
		} 
		if(word!=null && word_corrections.containsKey(word.toLowerCase())) {
			word_label.setText("Spelling: "+word);
			selections.removeAllItems();
			min_index=text.indexOf(word,min_index);
			
			// standardize index to textarea size
			min_index+= (text_area.getText().length()-text.length());
			
			int max_index = min_index+word.length();
			for(String suggestion: word_corrections.get(word.toLowerCase())) {
				selections.addItem(suggestion);
			}
			// unhighlight previous words
			highlight.removeAllHighlights();
			// highlight word
			try {
				highlight.addHighlight(min_index, max_index, highlighter);
			} catch (BadLocationException e) {
				// text no longer exists- keep going
			}
			min_index = max_index;
			revalidate();
		}
		return min_index;

	}
	
	private void initSpellCheckTab() {
		// spell check sidebar
		JPanel spell_check_sidebar = new JPanel();
		spell_check_sidebar.setLayout(new BorderLayout());
		JLabel spell_check_label = new JLabel("Spelling: ");
		JButton add_button = new JButton("Add");
		JButton ignore_button = new JButton("Ignore");
		JComboBox<String> combo_box = new JComboBox<String>();
		JButton change_button = new JButton("Change");
	    Highlighter highlight = tabs_text.get(tabbed_pane.getSelectedIndex()).getHighlighter();
	    HighlightPainter highlighter = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);
		spell_check_containers.add(new SpellCheckContainer(spell_check_sidebar, change_button, 
				ignore_button, add_button, null, spell_check_label,
				combo_box, highlight, highlighter, null));
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
		spell_check_sidebar.add(main_panel, BorderLayout.NORTH);

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
		
		JButton close = new JButton("Close");
		spell_check_sidebar.add(close, BorderLayout.SOUTH);
		// set up close action
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeSpellCheckWindow();
			}
        });	
	}
	
	private void initSpellCheckMenu() {
		// create spell check menu
		JMenu spellcheck_menu = new JMenu("SpellCheck");
		spellcheck_menu.setMnemonic(KeyEvent.VK_S);
		
		// run button
		JMenuItem run_button = new JMenuItem("Run");
		spellcheck_menu.add(run_button);
		
		// copy button accelerator (F7)
		run_button.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7,0));
		
		// copy button action
		run_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!tabs_text.isEmpty()) {
					JTextArea current = tabs_text.get(tabbed_pane.getSelectedIndex());
					spellCheckHelper(current.getText());
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
