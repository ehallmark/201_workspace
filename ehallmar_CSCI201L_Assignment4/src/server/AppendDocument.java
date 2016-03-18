package server;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

public class AppendDocument extends DefaultStyledDocument implements StyledDocument {
	private static final long serialVersionUID = 1L;
	
	public void append(String str) throws BadLocationException {
		insertString(getLength(), str, new SimpleAttributeSet());
	}

}
