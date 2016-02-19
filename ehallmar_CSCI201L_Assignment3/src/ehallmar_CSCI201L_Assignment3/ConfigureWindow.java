package ehallmar_CSCI201L_Assignment3;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ConfigureWindow extends JPanel {
	private static final long serialVersionUID = 1L;
	public String word_file;
	public String keyboard_file;
	public JButton close_button;
	public JButton select_keyboard_file;
	public JButton select_word_file;
	public JLabel word_file_label;
	public JLabel keyboard_file_label;

	ConfigureWindow() {
		// Spell check configure window stream of consciousness
		setLayout(new BorderLayout());
		close_button = new JButton("Close");
		close_button.setUI(new CustomButtonUI(close_button));
		
		select_keyboard_file = new JButton("Select Keyboard...");
		select_keyboard_file.setUI(new CustomButtonUI(select_keyboard_file));
		
		select_word_file = new JButton("Select WordList...");
		select_word_file.setUI(new CustomButtonUI(select_word_file));

		word_file_label = new JLabel(GUIController.wordlist_file.getName());
		word_file_label.setOpaque(true);
		word_file_label.setBackground(GUIController.BACKGROUND_COLOR);
		
		keyboard_file_label = new JLabel(GUIController.keyboard_file.getName());
		keyboard_file_label.setOpaque(true);
		keyboard_file_label.setBackground(GUIController.BACKGROUND_COLOR);
		// word list stuff
		JPanel w_holder = new JPanel();
		w_holder.setLayout(new GridLayout(2, 1));
		w_holder.add(word_file_label);
		w_holder.add(select_word_file);
		// keyboard file stuff
		JPanel kb_holder = new JPanel();
		kb_holder.setLayout(new GridLayout(2, 1));
		kb_holder.add(keyboard_file_label);
		kb_holder.add(select_keyboard_file);
		// border
		setBorder(BorderFactory.createTitledBorder("Configure"));
		// set look
		setBackground(GUIController.BACKGROUND_COLOR);
		// setup
		JPanel bottom = new JPanel();
		JPanel top = new JPanel();
		bottom.setLayout(new GridLayout(1, 2));
		bottom.add(close_button);
		JPanel p = new JPanel();
		p.setBackground(GUIController.BACKGROUND_COLOR);
		bottom.add(p);
		add(new JPanel(), BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		GridLayout grid = new GridLayout(2, 1);
		grid.setVgap(10);
		top.setLayout(grid);
		top.add(w_holder);
		top.add(kb_holder);
		add(top, BorderLayout.NORTH);
		
		for(Component c: this.getComponents()) {
			if(c instanceof JPanel) c.setBackground(GUIController.BACKGROUND_COLOR);
		}
		
		// configure close button
		close_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GUIController.closeConfigMenu();
			}
		});

		// configure keyboard file button
		select_keyboard_file.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Open file chooser
				JFileChooser file_chooser = new JFileChooser();
				file_chooser.setName("Open File...");
				file_chooser.setAcceptAllFileFilterUsed(false);
				file_chooser.setFileFilter(new FileNameExtensionFilter("kb files (*.kb)", "kb"));
				if (file_chooser.showOpenDialog(GUIController.tabbed_pane) == JFileChooser.APPROVE_OPTION) {
					File file = file_chooser.getSelectedFile();
					// double check extension
					if (!file.getName().endsWith(".kb")) {
						// invalid file type
						JOptionPane.showMessageDialog(GUIController.tabbed_pane,
								"Cannot perform action\nInvalid Keyboard file extension.", "Warning...",
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					// set file
					GUIController.keyboard_file = file_chooser.getSelectedFile();
					keyboard_file_label.setText(GUIController.keyboard_file.getName());
				}
			}
		});

		// configure keyboard file button
		select_word_file.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Open file chooser
				JFileChooser file_chooser = new JFileChooser();
				file_chooser.setName("Open File...");
				file_chooser.setAcceptAllFileFilterUsed(false);
				file_chooser.setFileFilter(new FileNameExtensionFilter("wl files (*.wl)", "wl"));
				if (file_chooser.showOpenDialog(GUIController.tabbed_pane) == JFileChooser.APPROVE_OPTION) {
					File file = file_chooser.getSelectedFile();
					// double check extension
					if (!file.getName().endsWith(".wl")) {
						// invalid file type
						JOptionPane.showMessageDialog(GUIController.tabbed_pane,
								"Cannot perform action\nInvalid WordList file extension.", "Warning...",
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					// set file
					GUIController.wordlist_file = file;
					word_file_label.setText(GUIController.wordlist_file.getName());
				}
			}
		});
	}

}
