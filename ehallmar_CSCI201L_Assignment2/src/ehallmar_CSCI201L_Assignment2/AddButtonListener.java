package ehallmar_CSCI201L_Assignment2;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.text.Highlighter.Highlight;

public class AddButtonListener extends IgnoreButtonListener {
	String word;
	
	public AddButtonListener(GUIController _w, SpellCheckContainer _c, JTextArea _t) {
		super(_w, _c, _t);
	}
	
	public void actionPerformed(ActionEvent e) {
		// get highlighted word
		Highlight h = spell_check_container.highlight.getHighlights()[0];
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
			spell_check_container.word_corrections.remove(word);
		}
		
		// continue
		super.actionPerformed(e);
	}

}
