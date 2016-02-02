package ehallmar_CSCI201L_Assignment2;

import java.awt.event.ActionEvent;

import javax.swing.JTextArea;
import javax.swing.text.Highlighter.Highlight;

public class ChangeButtonListener extends IgnoreButtonListener {

	public ChangeButtonListener(GUIController _w, SpellCheckContainer _c, JTextArea _t) {
		super(_w, _c, _t);
	}
	
	public void actionPerformed(ActionEvent e) {
		// Change button functionality
		String selected = (String)spell_check_container.combo_box.getSelectedItem();
		Highlight h = spell_check_container.highlight.getHighlights()[0];
		int start = h.getStartOffset();
		int end = h.getEndOffset();
		text_area.replaceRange(selected,start,end);
		spell_check_container.text = text_area.getText();
		spell_check_container.min_index -= (end-start);
		if(selected!=null) spell_check_container.min_index += selected.length();
		// continue
		super.actionPerformed(e);
	}
}
