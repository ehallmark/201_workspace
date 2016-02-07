package ehallmar_CSCI201L_Assignment2;

import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.undo.UndoManager;

public class UndoListener implements ChangeListener {

	JMenuItem undo_button;
	JMenuItem redo_button;
	UndoManager undo_manager;

	UndoListener(JMenuItem _undo_button, JMenuItem _redo_button, UndoManager _undo_manager) {
		super();
		undo_button = _undo_button;
		redo_button = _redo_button;
		undo_manager = _undo_manager;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (undo_manager.canUndo() && !undo_button.isEnabled()) {
			undo_button.setEnabled(true);
		} else if (!undo_manager.canUndo() && undo_button.isEnabled()) {
			undo_button.setEnabled(false);
		}
		if (undo_manager.canRedo() && !redo_button.isEnabled()) {
			redo_button.setEnabled(true);
		} else if (!undo_manager.canRedo() && redo_button.isEnabled()) {
			redo_button.setEnabled(false);
		}
	}
}
