package cs.designer.swing.undo;

import cs.designer.swing.ToolbarPanel;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import java.io.IOException;

/**
 * @author rongyang
 */
public class HouseEdit {
    public static final int STEP_NUMBER = 10;
    private static HouseEdit houseEditor;
    private UndoManager undoManager;

    private HouseEdit() {
        undoManager = new UndoManager();
        undoManager.setLimit(STEP_NUMBER);
    }

    public static HouseEdit getHouseEditor() {
        if (houseEditor == null) {
            houseEditor = new HouseEdit();
        }
        return houseEditor;
    }

    public void joinObject(final UndoableEdit editer) {
        undoManager.addEdit(editer);
        ToolbarPanel.checkDoEdit();
    }

    public void redo() {
        if (undoManager.canRedo()) {
            undoManager.redo();
        }
    }

    public void undo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
        }
    }

    public void reset() {
        this.undoManager.discardAllEdits();
        houseEditor = null;
    }


    public boolean canRedo() {
        return undoManager.canRedo();
    }

    public boolean canUndo() {
        return undoManager.canUndo();
    }
}
