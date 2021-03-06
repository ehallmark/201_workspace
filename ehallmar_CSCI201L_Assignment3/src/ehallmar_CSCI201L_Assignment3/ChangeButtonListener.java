package ehallmar_CSCI201L_Assignment3;

import java.awt.event.ActionEvent;

import javax.swing.text.Highlighter.Highlight;

import ehallmar_CSCI201L_Assignment3.GUIController.TabWindow;

public class ChangeButtonListener extends IgnoreButtonListener {

	public ChangeButtonListener(TabWindow _tab) {
		super(_tab);
	}

	public void actionPerformed(ActionEvent e) {
		// Change button functionality
		String selected = (String) spell_check_sidebar.combo_box.getSelectedItem();
		if(selected==null) selected = "";
		Highlight h = spell_check_sidebar.highlight.getHighlights()[0];
		int start = h.getStartOffset();
		int end = h.getEndOffset();
		String text = text_area.getText();
		text_area.setText(text.substring(0, start)+selected+text.substring(end,text.length()));
		spell_check_sidebar.text = text_area.getText().toLowerCase();
		spell_check_sidebar.min_index -= (end - start);
		// adjust window if necessary
		int size = (text.substring(0, start)+selected).length();
		if(size>=0 && size <spell_check_sidebar.text.length()) {
			text_area.setCaretPosition((text.substring(0, start)+selected).length());
		}
		if (selected != null)
			spell_check_sidebar.min_index += selected.length();
		// continue
		super.actionPerformed(e);
	}
}
