/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.designer.swing.undo;

import com.klm.cons.impl.Floor;
import cs.designer.module.TempWall;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * @author rongyang
 */
public class WallInUndoEditor extends AbstractUndoableEdit {
    private HouseUndoEditable wallOper;

    public WallInUndoEditor(final Floor floor, final TempWall wall) {
        wallOper = new WallUndoEditImpl(wall);
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        wallOper.remove();

    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        wallOper.add();
    }

    @Override
    public String getPresentationName() {
        return "wall";
    }

}
