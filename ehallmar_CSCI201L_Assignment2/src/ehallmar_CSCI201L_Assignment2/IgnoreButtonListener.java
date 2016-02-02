package ehallmar_CSCI201L_Assignment2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class IgnoreButtonListener implements ActionListener {
	public SpellCheckContainer spell_check_container;
	public JTextArea text_area;
	public GUIController main_window;
	
	public IgnoreButtonListener(GUIController _w, SpellCheckContainer _c, JTextArea _t) {
		spell_check_container = _c;
		text_area = _t;
		main_window = _w;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int next_word = spell_check_container.min_index;
		while(next_word == spell_check_container.min_index && spell_check_container.word_index<spell_check_container.words.length) {
			main_window.nextWordCorrection(spell_check_container,
					text_area);
			spell_check_container.word_index++;
		}
		// check if we are done
		if(spell_check_container.words.length<=spell_check_container.word_index) {
			// done
			main_window.closeSpellCheckWindow();
		}
	}

}
