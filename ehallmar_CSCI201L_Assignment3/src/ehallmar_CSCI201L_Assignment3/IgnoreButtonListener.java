package ehallmar_CSCI201L_Assignment3;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextPane;

import ehallmar_CSCI201L_Assignment3.GUIController.TabWindow;
import ehallmar_CSCI201L_Assignment3.GUIController.TabWindow.SpellCheckSidebar;

public class IgnoreButtonListener implements ActionListener {
	public SpellCheckSidebar spell_check_sidebar;
	public JTextPane text_area;
	public TabWindow tab;

	public IgnoreButtonListener(TabWindow _tab) {
		spell_check_sidebar = _tab.spell_check_sidebar;
		text_area = _tab.text_area;
		tab = _tab;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// check if we are done
		if (spell_check_sidebar.words.length <= spell_check_sidebar.word_index) {
			// done
			GUIController.closeSpellCheckWindow();
			tab.getParent().getParent().revalidate();
			return;
		}
		// adjust window if necessary
		int next_word = spell_check_sidebar.min_index;
		while (next_word == spell_check_sidebar.min_index
				&& spell_check_sidebar.word_index < spell_check_sidebar.words.length) {
			tab.nextWordCorrection();
			spell_check_sidebar.word_index++;
			try {
				// adjust window if necessary and we can
				text_area.setCaretPosition(text_area.getSelectionEnd());
			} catch (Exception ex) {
				// do nothing
			}
		}

		// double check if we are done
		if (spell_check_sidebar.words.length <= spell_check_sidebar.word_index) {
			// done
			GUIController.closeSpellCheckWindow();
			tab.getParent().getParent().revalidate();
			return;
		}
	}

}
