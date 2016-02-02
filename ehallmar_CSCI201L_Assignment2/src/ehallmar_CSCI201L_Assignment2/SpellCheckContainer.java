package ehallmar_CSCI201L_Assignment2;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

public class SpellCheckContainer  {
	public JButton change_button;
	public JButton ignore_button;
	public JButton add_button;
	public Hashtable<String, ArrayList<String>> word_corrections;
	public JLabel word_label;
	public JComboBox<String> combo_box;
	public Highlighter highlight;
	public HighlightPainter highlighter;
	public String[] words;
	public JPanel sidebar;
	public int min_index;
	public int word_index;
	public String text;
	
	SpellCheckContainer(JPanel _sidebar, JButton _change, JButton _ignore, JButton _add, 
			Hashtable<String, ArrayList<String>> _corrections, JLabel _label,
			JComboBox<String> _combo, Highlighter _highlight, HighlightPainter _highlighter,
			String[] _words) {
		change_button = _change;
		ignore_button = _ignore;
		add_button = _add;
		word_corrections = _corrections;
		combo_box = _combo;
		word_label = _label;
		highlight = _highlight;
		highlighter = _highlighter;
		words = _words;
		sidebar = _sidebar;
		min_index = 0;
		word_index = 0;
		text = null;
	}

}
