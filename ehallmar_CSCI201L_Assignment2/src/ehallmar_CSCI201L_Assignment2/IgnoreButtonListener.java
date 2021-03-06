package ehallmar_CSCI201L_Assignment2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextPane;

import ehallmar_CSCI201L_Assignment2.GUIController.TabWindow;
import ehallmar_CSCI201L_Assignment2.GUIController.TabWindow.SpellCheckSidebar;

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

		int next_word = spell_check_sidebar.min_index;
		while (next_word == spell_check_sidebar.min_index
				&& spell_check_sidebar.word_index < spell_check_sidebar.words.length) {
			tab.nextWordCorrection();
			spell_check_sidebar.word_index++;
		}
		// adjust window if necessary
		/*
	    JScrollBar scroll_bar = scroll_pane.getVerticalScrollBar();
	    scroll_bar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent ae) {
				// scroll horizontally
				scroll_bar.scr
				
				
			}
	    });*/
		// double check if we are done
		if (spell_check_sidebar.words.length <= spell_check_sidebar.word_index) {
			// done
			GUIController.closeSpellCheckWindow();
			tab.getParent().getParent().revalidate();
			return;
		}
	}

}
