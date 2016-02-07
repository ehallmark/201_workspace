package ehallmar_CSCI201L_Assignment2;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.swing.JOptionPane;
import javax.swing.text.Highlighter.Highlight;

import ehallmar_CSCI201L_Assignment2.GUIController.TabWindow;

public class AddButtonListener extends IgnoreButtonListener {
	String word;
	
	public AddButtonListener(TabWindow _tab) {
		super(_tab);
	}
	
	public void actionPerformed(ActionEvent e) {
		// get highlighted word
		Highlight h = spell_check_sidebar.highlight.getHighlights()[0];
		int start = h.getStartOffset();
		int end = h.getEndOffset();
		word = text_area.getText().substring(start,end);
		
		// Add button functionality
		try {
		    Files.write(Paths.get(GUIController.wordlist_file.getAbsolutePath()), ("\n"+word).getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e1) {
		    // Error
			JOptionPane.showMessageDialog(GUIController.tabbed_pane, "Cannot perform action\nError updating WordList file.",
					"Error...", JOptionPane.ERROR_MESSAGE);
		} finally {
			spell_check_sidebar.word_corrections.remove(word.toLowerCase());
		}
		
		// continue
		super.actionPerformed(e);
	}

}
