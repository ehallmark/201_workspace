package ehallmar_CSCI201L_Assignment3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.zip.DataFormatException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.undo.UndoManager;

import ehallmar_CSCI201L_Assignment1.Controller;

public class GUIController extends JFrame {
	private static final long serialVersionUID = 1L;

	// static variables
	private static final String NEW_FILE_NAME = "new";
	static final String DEFAULT_KEYBOARD_FILE_NAME = "querty-us.kb";
	static final String DEFAULT_WORDLIST_FILE_NAME = "wordlist.wl";
	static File keyboard_file = new File(DEFAULT_KEYBOARD_FILE_NAME);;
	static File wordlist_file = new File(DEFAULT_WORDLIST_FILE_NAME);
	private static ConfigureWindow config_menu;
	static CustomTabbedPane tabbed_pane;
	static boolean inConfig = false;
	private static CustomMenuBar menu_bar;
	private static JMenuItem undo_button;
	private static JMenuItem redo_button;
	private static JMenu edit_menu;
	private static JMenuItem save_file_option;
	private static JMenuItem config_option;
	private static JMenuItem run_option;
	private static JMenuItem select_all_option;
	private static JMenuItem copy_option;
	private static JMenuItem cut_option;
	private static JMenuItem paste_option;
	private static JMenuItem close_file_option;
	static Font DEFAULT_FONT;
	static Color BACKGROUND_COLOR = Color.GRAY;
	static Color OFFSET_COLOR = new Color(255,110,0);
	static Color LOGO_COLOR = new Color(255,69,0);

	public GUIController() {
		// Check if we found a font
		if(DEFAULT_FONT == null) {
			// fall back on a built in font
			DEFAULT_FONT = new Font("Segoe UI", Font.PLAIN, 12);
		}
		// create configure menu
		config_menu = new ConfigureWindow();
		 
		// Generate UI
		generateUI();

		// Display
		setVisible(true);
		validate();
	}

	// main tab window class
	class TabWindow extends JPanel {
		private static final long serialVersionUID = 1L;
		JTextPane text_area;
		File file;
		SpellCheckSidebar spell_check_sidebar;
		UndoManager undo_manager;
		UndoListener undo_listener;
		boolean is_config_window;

		// empty constructor
		TabWindow() {
			is_config_window = true;
			setBackground(BACKGROUND_COLOR);
		}

		// Set up tab GUI
		TabWindow(File f) {
			is_config_window = false;
			file = f;
			setLayout(new BorderLayout());
			text_area = new JTextPane();
			text_area.setSelectionColor(OFFSET_COLOR);
			JScrollPane scroll_pane = new JScrollPane(text_area);
			scroll_pane.setViewportView(text_area);
			// add text if opened window (before adding undo listener)
			if(file!=null) {
				String text = "";
				Scanner scanner = null;
				try {
					scanner = new Scanner(file);
					while (scanner.hasNextLine()) {
						text += scanner.nextLine() + '\n';
					}
					text_area.setText(text);
				} catch (FileNotFoundException e) {
					// Uh oh
					JOptionPane.showMessageDialog(this, "Cannot perform action\n" + file.getName() + " not found.",
							"File not found...", JOptionPane.ERROR_MESSAGE);
					return;
				} finally {
					if (scanner != null)
						scanner.close();
				}
			}
			undo_manager = new UndoManager();
			text_area.getDocument().addUndoableEditListener(undo_manager);
			text_area.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void changedUpdate(DocumentEvent arg0) {
					// make sure we can undo
					manageEditOptions();
				}

				@Override
				public void insertUpdate(DocumentEvent arg0) {
					// make sure we can undo
					manageEditOptions();
				}

				@Override
				public void removeUpdate(DocumentEvent arg0) {
					// make sure we can undo
					manageEditOptions();
				}
				
			});
			text_area.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent arg0) {
					// make sure we can undo
					manageEditOptions();					
				}

				@Override
				public void focusLost(FocusEvent e) {
				}
				
			});

			add(scroll_pane, BorderLayout.CENTER);
			
			CustomVerticalScrollBar v_scroll_bar = new CustomVerticalScrollBar();
			v_scroll_bar.setUI(v_scroll_bar.new CustomScrollUI());
			scroll_pane.setVerticalScrollBar(v_scroll_bar);
			scroll_pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			CustomHorizontalScrollBar h_scroll_bar = new CustomHorizontalScrollBar();
			h_scroll_bar.setUI(h_scroll_bar.new CustomScrollUI());
			scroll_pane.setHorizontalScrollBar(h_scroll_bar);
			scroll_pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			//undo listener stuff
			undo_listener = new UndoListener();
			
			String tab_name = NEW_FILE_NAME;
			if (file != null) {
				// Get opened file name
				tab_name = file.getName();
			}

			// add to tabbed pane
			tabbed_pane.add(tab_name, this);
			tabbed_pane.setSelectedIndex(tabbed_pane.getTabCount() - 1);

			// add spell check stuff
			spell_check_sidebar = new SpellCheckSidebar(this);
			
			// make sure we have the right edit options showing
			manageEditOptions();

		}

		private void runSpellCheck() {
			spell_check_sidebar.reset();
			spellCheckHelper(text_area.getText());
		}

		private void spellCheckHelper(String text) {
			if (text.replaceAll("\\s", "").length() == 0) {
				JOptionPane.showMessageDialog(this, "No words to check!", "SpellCheck complete...",
						JOptionPane.INFORMATION_MESSAGE);
				closeSpellCheckWindow();
				return;
			}
			openSpellCheckWindow();
			// check to make sure there are words to check
			text = text.toLowerCase();
			ArrayList<String> words = new ArrayList<String>(Arrays.asList(text.split("[.,\"':;!?()\\[\\]\\-\\s]")));
			while (words.contains(null))
				words.remove(null);
			while (words.contains(""))
				words.remove("");
			spell_check_sidebar.words = new String[100];
			spell_check_sidebar.words = words.toArray(spell_check_sidebar.words);
			spell_check_sidebar.text = text;
			// try to get word corrections from spell checker
			try {
				spell_check_sidebar.word_corrections = Controller.spellCheck(wordlist_file.getAbsolutePath(),
						keyboard_file.getAbsolutePath(), words);
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
				JOptionPane.showMessageDialog(this,
						"Cannot perform action\nMissing files or incorrect file extensions.", "Configuration Error...",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			// lets get it goin'
			int next_word = spell_check_sidebar.min_index;
			while (next_word == spell_check_sidebar.min_index
					&& spell_check_sidebar.word_index < spell_check_sidebar.words.length) {
				nextWordCorrection();
				spell_check_sidebar.word_index++;
			}

			if (next_word == spell_check_sidebar.min_index) {
				JOptionPane.showMessageDialog(this, "All words spelled correctly!", "SpellCheck complete...",
						JOptionPane.INFORMATION_MESSAGE);
				closeSpellCheckWindow();
			}
		}

		void nextWordCorrection() {
			String word = null;
			if (spell_check_sidebar.word_index < spell_check_sidebar.words.length) {
				word = spell_check_sidebar.words[spell_check_sidebar.word_index];
			}
			if (word != null && spell_check_sidebar.word_corrections
					.containsKey(word.trim().replaceAll("[^a-zA-Z\\s]", "").toLowerCase())) {
				spell_check_sidebar.spell_check_label.setText("Spelling: " + word);
				spell_check_sidebar.combo_box.removeAllItems();
				// make sure we get the whole word and not a prefix
				HashSet<Character> valid_separators = new HashSet<Character>();
				for (char sep : " \n\t!?,:;.()[]{}-\"'".toCharArray()) {
					valid_separators.add(sep);
				}
				boolean valid = false;
				int counter = 0;
				int starting_min_index = spell_check_sidebar.min_index;
				while (!valid && counter <= (spell_check_sidebar.words.length - spell_check_sidebar.word_index)) {
					counter++;
					spell_check_sidebar.min_index = spell_check_sidebar.text.indexOf(word,
							spell_check_sidebar.min_index);
					if (spell_check_sidebar.min_index > 0) {
						char previous = spell_check_sidebar.text.charAt(spell_check_sidebar.min_index - 1);
						if (!valid_separators.contains(previous)) {
							// is a suffix -- not good
							spell_check_sidebar.min_index += 1;
							continue;
						}
					}
					if (spell_check_sidebar.min_index + word.length() < spell_check_sidebar.text.length()) {
						char next = spell_check_sidebar.text.charAt(spell_check_sidebar.min_index + word.length());
						if (!valid_separators.contains(next)) {
							// is a prefix -- not good
							spell_check_sidebar.min_index += 1;
							continue;
						}
					}
					valid = true;
				}
				if (spell_check_sidebar.min_index < starting_min_index) {
					// no word was found
					spell_check_sidebar.min_index = spell_check_sidebar.text.length();
				}
				// check if nothing was found
				int max_index = spell_check_sidebar.min_index + word.length();
				for (String suggestion : spell_check_sidebar.word_corrections
						.get(word.trim().replaceAll("[^a-zA-Z\\s]", "").toLowerCase())) {
					spell_check_sidebar.combo_box.addItem(suggestion);
				}
				// unhighlight previous words
				spell_check_sidebar.highlight.removeAllHighlights();
				// highlight word
				try {
					spell_check_sidebar.highlight.addHighlight(spell_check_sidebar.min_index, max_index,
							spell_check_sidebar.highlighter);
				} catch (BadLocationException e) {
					// text no longer exists- keep going
				}
				spell_check_sidebar.min_index++;
				revalidate();
			}
		}
		
		void manageEditOptions() {
			if (undo_manager.canUndo() && text_area.isEditable() && !undo_button.isEnabled()) {
				undo_button.setEnabled(true);
			} else if ((!undo_manager.canUndo() || !text_area.isEditable()) && undo_button.isEnabled()) {
				undo_button.setEnabled(false);
			}
			if (undo_manager.canRedo() && text_area.isEditable() && !redo_button.isEnabled()) {
				redo_button.setEnabled(true);
			} else if ((!undo_manager.canRedo() || !text_area.isEditable()) && redo_button.isEnabled()) {
				redo_button.setEnabled(false);
			}
		}
		
		// undo listener class
		public class UndoListener implements ChangeListener {

			UndoListener() {
				super();
			}

			@Override
			public void stateChanged(ChangeEvent e) {
				manageEditOptions();
			}
		}
		
		// Spell check class
		class SpellCheckSidebar extends JPanel {
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
				spell_check_label.setFont(font.deriveFont((float) 13));
				spell_check_label.setHorizontalAlignment(JLabel.CENTER);
				
				add_button = new JButton("Add");
				add_button.setUI(new CustomButtonUI(add_button));
				
				ignore_button = new JButton("Ignore");
				ignore_button.setUI(new CustomButtonUI(ignore_button));
				
				change_button = new JButton("Change");
				change_button.setUI(new CustomButtonUI(change_button));
				
				combo_box = new JComboBox<String>();
				combo_box.setUI(new BasicComboBoxUI() {
					@Override
					protected JButton createArrowButton() {
						JButton btn = new ComboBoxButton();
						return btn;
					}
				});
				text_area = get_current_tab().text_area;
				highlight = text_area.getHighlighter();
		    	Color c = OFFSET_COLOR;
				highlighter = new DefaultHighlighter.DefaultHighlightPainter(c);
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
				GridLayout row_layout = new GridLayout(1, 2);
				row_layout.setHgap(5);
				second_row.setLayout(row_layout);
				third_row.setLayout(row_layout);
				GridLayout grid = new GridLayout(3, 1);
				grid.setVgap(10);
				main_panel.setLayout(grid);
				// border and background
				main_panel.setBorder(BorderFactory.createTitledBorder("Spell Check"));
				main_panel.setBackground(BACKGROUND_COLOR);
				second_row.setBackground(BACKGROUND_COLOR);
				third_row.setBackground(BACKGROUND_COLOR);

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

				// Set up actions

				close_button = new JButton("Close");
				close_button.setUI(new CustomButtonUI(close_button));
				add(close_button, BorderLayout.SOUTH);
				// set up close action
				close_button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						closeSpellCheckWindow();
					}
				});
				
				// set look
				this.setBackground(BACKGROUND_COLOR);
				
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

		void redoActionPerformed() {
			if (undo_manager.canRedo()) {
				text_area.requestFocusInWindow();
				undo_manager.redo();
			}
			manageEditOptions();
		}

		void undoActionPerformed() {
			if (undo_manager.canUndo()) {
				text_area.requestFocusInWindow();
				undo_manager.undo();
			}	
			manageEditOptions();
		}

	} // end of main tab class

	private static TabWindow get_current_tab() {
		return (TabWindow) tabbed_pane.getSelectedComponent();
	}

	private void createTab(File file) {
		// check if config window is open
		if (tabbed_pane.getTabCount() > 0) {
			closeConfigMenu();
		}
		// Instantiate new tab
		TabWindow tw = new TabWindow(file);
		tw.text_area.requestFocusInWindow();
	}

	private void closeTab(int tab_index) {
		if (tab_index >= 0 && tab_index < tabbed_pane.getTabCount()) {
			if (tabbed_pane.getTabCount() == 1 && inConfig) {
				// check to see if config menu is open
				closeConfigMenu();
			}
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
		if (file_chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			// create new tab
			File file = file_chooser.getSelectedFile();
			if (!file.getName().endsWith(".txt")) {
				// bad file extension
				JOptionPane.showMessageDialog(GUIController.tabbed_pane,
						"Cannot perform action\nInvalid file extension.", "Warning...", JOptionPane.WARNING_MESSAGE);
				return;
			}
			// check for previously open tab
			for (Component c : tabbed_pane.getComponents()) {
				TabWindow tab = (TabWindow) c;
				if (file.equals(tab.file)) {
					// Uh oh
					JOptionPane.showMessageDialog(this, "Cannot perform action\n" + file.getName() + " already open.",
							"File already open...", JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
			createTab(file);
		}
	}

	private File createNewFileHelper(File file) {
		// Open file chooser
		JFileChooser file_chooser = new JFileChooser();
		file_chooser.setName("Save As...");
		file_chooser.setAcceptAllFileFilterUsed(false);
		if (file != null) {
			file_chooser.setSelectedFile(file);
			;
		}
		file_chooser.setFileFilter(new FileNameExtensionFilter("txt files (*.txt)", "txt"));
		if (file_chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			// create new tab
			return file_chooser.getSelectedFile();
		}
		return null;
	}

	private void saveFileHelper() {
		if (tabbed_pane.getTabCount() <= 0)
			return;
		TabWindow current_tab = get_current_tab();
		JTextPane text = current_tab.text_area;
		FileWriter writer = null;
		String filename = null;
		try {
			// create new file helper
			File file = createNewFileHelper(current_tab.file);
			if (file == null) {
				return;
			}
			filename = file.getAbsolutePath();
			// make sure we have the .txt suffix
			if (!filename.endsWith(".txt")) {
				filename = filename + ".txt";
			}
			file = new File(filename);
			if (!file.createNewFile()) {
				// filename already exists, prompt user to confirm
				int user_response = JOptionPane.showConfirmDialog(null,
						file.getName() + " already exists\nDo you want to replace it?", "Confirm Save As",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (user_response == JOptionPane.NO_OPTION) {
					// User does not want to save anymore
					return;
				}
			}

			writer = new FileWriter(file);
			writer.write(text.getText());
			tabbed_pane.setTitleAt(tabbed_pane.getSelectedIndex(), file.getName());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Cannot perform action\nError writing to file.", "Error...",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this, "Cannot perform action\nError closing file.", "Error...",
							JOptionPane.ERROR_MESSAGE);
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
		new_file_option.setIcon(new ImageIcon("img/menuitems/new.png"));
		file_menu.add(new_file_option);

		// new button accelerator (control-n)
		new_file_option.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
		// new file action
		new_file_option.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newFileHelper();
			}
		});

		// create open button
		JMenuItem open_file_option = new JMenuItem("Open");
		open_file_option.setIcon(new ImageIcon("img/menuitems/open.png"));
		file_menu.add(open_file_option);

		// open button accelerator (control-o)
		open_file_option.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));

		// open button action
		open_file_option.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openFileHelper();
			}
		});

		// create save button
		save_file_option = new JMenuItem("Save");
		save_file_option.setIcon(new ImageIcon("img/menuitems/save.png"));
		file_menu.add(save_file_option);

		// save button accelerator (control-s)
		save_file_option.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));

		// save button action
		save_file_option.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFileHelper();
			}
		});

		// create close button
		close_file_option = new JMenuItem("Close");
		close_file_option.setIcon(new ImageIcon("img/menuitems/close.png"));
		file_menu.add(close_file_option);
		// close button action
		close_file_option.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeFileHelper();
			}
		});

		// Apply the menu
		menu_bar.add(file_menu);
	}

	private void initTabbedPane() {		
		tabbed_pane = new CustomTabbedPane(DEFAULT_FONT.deriveFont(22f));
		
		// switch config menu based on tab change if open
		tabbed_pane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (inConfig) {
					if (tabbed_pane.getTabCount() > 0) {
						openConfigMenu();
					}
				}
				// remove tab dependent change listeners
				for(ChangeListener cl: edit_menu.getChangeListeners()) {
					edit_menu.removeChangeListener(cl);
				}
				for(ActionListener al: undo_button.getActionListeners()) {
					undo_button.removeActionListener(al);
				}
				for(ActionListener al: redo_button.getActionListeners()) {
					redo_button.removeActionListener(al);
				}
				
				// enable the edit listeners
				enableEditOptions();
				
				// make sure we have default set of no listeners 
				if(edit_menu.getChangeListeners().length==0) {
					redo_button.setEnabled(false);
					undo_button.setEnabled(false);
				}
				// make sure other menu options are enabled/disabled accordingly
				toggleMenuOptions();
			}
		});
		// make sure we have default set of no listeners 
		if(edit_menu.getChangeListeners().length==0) {
			redo_button.setEnabled(false);
			undo_button.setEnabled(false);
		}
		add(tabbed_pane, BorderLayout.CENTER);

	}

	private void toggleMenuOptions() {
		// check if any tabs are open
		if (tabbed_pane.getTabCount() == 0 || ((TabWindow) tabbed_pane.getComponentAt(0)).is_config_window) {
			// make sure stuff is disabled
			save_file_option.setEnabled(false);
			run_option.setEnabled(false);
			close_file_option.setEnabled(false);
			cut_option.setEnabled(false);
			paste_option.setEnabled(false);
			copy_option.setEnabled(false);
			select_all_option.setEnabled(false);

		} else {
			// make sure stuff is enabled
			save_file_option.setEnabled(true);
			run_option.setEnabled(true);
			close_file_option.setEnabled(true);
			cut_option.setEnabled(true);
			paste_option.setEnabled(true);
			copy_option.setEnabled(true);
			select_all_option.setEnabled(true);
			get_current_tab().manageEditOptions();
		}
	}

	private void initEditMenu() {
		// create edit menu
		edit_menu = new JMenu("Edit");
		edit_menu.setMnemonic(KeyEvent.VK_E);

		// create undo button
		undo_button = new JMenuItem("Undo");
		undo_button.setIcon(new ImageIcon("img/menuitems/undo.png"));
		edit_menu.add(undo_button);

		// undo button accelerator (control-z)
		undo_button.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));

		// create redo button
		redo_button = new JMenuItem("Redo");
		redo_button.setIcon(new ImageIcon("img/menuitems/redo.png"));
		edit_menu.add(redo_button);

		// redo button accelerator (control-y)
		redo_button.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));

		edit_menu.addSeparator();

		// create cut button
		cut_option = new JMenuItem("Cut");
		cut_option.setIcon(new ImageIcon("img/menuitems/cut.png"));
		edit_menu.add(cut_option);

		// cut button accelerator (control-x)
		cut_option.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));

		// cut button action
		cut_option.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!(tabbed_pane.getTabCount() <= 0)) {
					JTextPane current = get_current_tab().text_area;
					current.cut();
				}
			}
		});

		// create copy button
		copy_option = new JMenuItem("Copy");
		copy_option.setIcon(new ImageIcon("img/menuitems/copy.png"));
		edit_menu.add(copy_option);

		// copy button accelerator (control-c)
		copy_option.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));

		// copy button action
		copy_option.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!(tabbed_pane.getTabCount() <= 0)) {
					JTextPane current = get_current_tab().text_area;
					current.copy();
				}
			}
		});

		// create paste button
		paste_option = new JMenuItem("Paste");
		paste_option.setIcon(new ImageIcon("img/menuitems/paste.png"));
		edit_menu.add(paste_option);

		// copy button accelerator (control-c)
		paste_option.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));

		// copy button action
		paste_option.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!(tabbed_pane.getTabCount() <= 0)) {
					JTextPane current = get_current_tab().text_area;
					current.paste();
				}
			}
		});
		edit_menu.addSeparator();

		// create select all button
		select_all_option = new JMenuItem("Select All");
		select_all_option.setIcon(new ImageIcon("img/menuitems/select.png"));
		edit_menu.add(select_all_option);

		// save button accelerator (control-a)
		select_all_option.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
		// save button action
		select_all_option.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!(tabbed_pane.getTabCount() <= 0)) {
					JTextPane current = get_current_tab().text_area;
					// select text area before selecting all text
					current.requestFocusInWindow();
					current.selectAll();
				}
			}
		});

		// Apply the menu
		menu_bar.add(edit_menu);
	}

	private void openSpellCheckWindow() {
		if (inConfig) {
			closeConfigMenu();
		}
		TabWindow current_tab = get_current_tab();
		current_tab.add(current_tab.spell_check_sidebar, BorderLayout.EAST);
		current_tab.text_area.setEditable(false);
		current_tab.text_area.getHighlighter().removeAllHighlights();
		current_tab.manageEditOptions();
		validate();
	}

	static void closeSpellCheckWindow() {
		TabWindow current_tab = get_current_tab();
		if (current_tab != null && !current_tab.is_config_window) {
			current_tab.remove(current_tab.spell_check_sidebar);
			current_tab.manageEditOptions();
			current_tab.text_area.setEditable(true);
			current_tab.text_area.getHighlighter().removeAllHighlights();
			current_tab.undo_manager.discardAllEdits();
			tabbed_pane.getParent().validate();
		}
	}

	private static void enableEditOptions() {
		if(tabbed_pane.getTabCount() > 0) {
			TabWindow tw = (TabWindow) tabbed_pane.getSelectedComponent();
			if(!tw.is_config_window) {
				// add the good stuff
				edit_menu.addChangeListener(tw.undo_listener);
				// undo button action
				undo_button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						tw.undoActionPerformed();
					}
				});
				//redo button action
				redo_button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						tw.redoActionPerformed();
					}
				});
				
			}
		}		
	}

	static void openConfigMenu() {
		inConfig = true;
		if (tabbed_pane.getTabCount() > 0) {
			TabWindow panel = (TabWindow) (tabbed_pane.getSelectedComponent());
			if (!panel.is_config_window)
				closeSpellCheckWindow();
			panel.add(config_menu, BorderLayout.EAST);
			tabbed_pane.getParent().validate();
		}
	}

	static void closeConfigMenu() {
		TabWindow panel = (TabWindow) (config_menu.getParent());
		inConfig = false;
		if (panel != null) {
			panel.remove(config_menu);
			if (((TabWindow) tabbed_pane.getComponentAt(0)).is_config_window)
				tabbed_pane.remove(0);
			tabbed_pane.getParent().validate();
		}
	}

	private void initSpellCheckMenu() {
		// create spell check menu
		JMenu spellcheck_menu = new JMenu("SpellCheck");
		spellcheck_menu.setMnemonic(KeyEvent.VK_S);

		// run button
		run_option = new JMenuItem("Run");
		run_option.setIcon(new ImageIcon("img/menuitems/run.png"));
		spellcheck_menu.add(run_option);

		// run button accelerator (F7)
		run_option.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));

		// run button action
		run_option.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!(tabbed_pane.getTabCount() <= 0)) {
					// reset this tabs spell check container
					get_current_tab().runSpellCheck();

				} else {
					JOptionPane.showMessageDialog(GUIController.tabbed_pane,
							"Cannot perform action\nMust have a tab open.", "No tab open...",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});

		// configure tab
		config_option = new JMenuItem("Configure");
		config_option.setIcon(new ImageIcon("img/menuitems/configure.png"));
		config_option.setMnemonic(KeyEvent.VK_C);
		spellcheck_menu.add(config_option);

		// config button action
		config_option.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tabbed_pane.getTabCount() == 0) {
					TabWindow panel = new TabWindow();
					panel.setLayout(new BorderLayout());
					tabbed_pane.add("Configure", panel);
				}
				openConfigMenu();
			}
		});

		// Apply the menu
		menu_bar.add(spellcheck_menu);
	}
	
	private void initCursor() {
		// get cursor icon from resources
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image image = toolkit.getImage("img/icon/cursor.png");
		Cursor c = toolkit.createCustomCursor(image , new Point(0, 
				0), "img");
		setCursor(c);
	}
	
	private void initApplicationIcon() {
		// set custom application icon
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image image = toolkit.getImage("img/icon/office.png");
		setIconImage(image);
	}
	
	// menu bar class
	class CustomMenuBar extends JMenuBar {
		private static final long serialVersionUID = 1L;
		private Image menu_background;
		
		CustomMenuBar() {
			super();
			// set custom application icon
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			menu_background = toolkit.getImage("img/backgrounds/menu_background.png");
		}
		public void paintComponent(Graphics g)
		{	
			// draw background
			g.drawImage(menu_background, 0, 0,  this.getWidth(), this.getHeight(),this);
		}
	}
	
	private void generateUI() {
		menu_bar = new CustomMenuBar();
		setJMenuBar(menu_bar);
		initFileMenu();
		initEditMenu();
		initTabbedPane();
		initSpellCheckMenu();
		toggleMenuOptions();
		setTitle("Evan's Text Editor");
		setSize(800,500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// assignment 3
		initCursor();
		initApplicationIcon();
		this.getContentPane().setBackground(BACKGROUND_COLOR);
	}


}
