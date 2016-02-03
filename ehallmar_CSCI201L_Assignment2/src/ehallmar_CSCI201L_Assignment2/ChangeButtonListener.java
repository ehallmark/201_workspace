package ehallmar_CSCI201L_Assignment2;

import java.awt.event.ActionEvent;

import javax.swing.text.Highlighter.Highlight;

import ehallmar_CSCI201L_Assignment2.GUIController.TabWindow;

public class ChangeButtonListener extends IgnoreButtonListener {

	public ChangeButtonListener(TabWindow _tab) {
		super(_tab);
	}
	
	public void actionPerformed(ActionEvent e) {
		// Change button functionality
		String selected = (String)spell_check_sidebar.combo_box.getSelectedItem();
		Highlight h = spell_check_sidebar.highlight.getHighlights()[0];
		int start = h.getStartOffset();
		int end = h.getEndOffset();
		text_area.replaceRange(selected,start,end);
		spell_check_sidebar.text = text_area.getText();
		spell_check_sidebar.min_index -= (end-start);
		if(selected!=null) spell_check_sidebar.min_index += selected.length();
		// continue
		super.actionPerformed(e);
	}
}
