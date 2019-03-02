package cs.designer.swing.undo;

import cs.designer.module.TempWall;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 1/16/12
 * Time: 8:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class WalDeUndoEditor extends AbstractUndoableEdit {
    private HouseUndoEditable wallOper;

    public WalDeUndoEditor(final TempWall wall) {
        wallOper = new WallUndoEditImpl(wall);

    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        wallOper.add();


    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        wallOper.remove();

    }
}
