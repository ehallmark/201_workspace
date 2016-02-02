package ehallmar_CSCI201L_Assignment2;

import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.IOException;
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
		FileWriter fw = null;
		try {
			fw = new FileWriter(GUIController.wordlist_file);
			fw.append("\n"+word);
			spell_check_container.word_corrections.remove(word);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(GUIController.tabbed_pane, "Cannot perform action\nError updating WordList file.",
					"Error...", JOptionPane.ERROR_MESSAGE);
		} finally {
			try {
				fw.close();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(GUIController.tabbed_pane, "Cannot perform action\nError closing WordList file.",
						"Error...", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		// continue
		super.actionPerformed(e);
	}

}
